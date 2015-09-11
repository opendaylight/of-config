/*
 * Copyright (c) 2015 ZTE, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.ofconfig.southbound.impl.inventory.OfconfigInvTopoinitializer;
import org.opendaylight.ofconfig.southbound.impl.inventory.OfconfigInventoryTopoHandler;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.api.rev150901.OdlOfconfigApiService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.api.rev150901.SyncCapcableSwitchInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentationBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class OfconfigSouthboundImpl
        implements BindingAwareProvider, AutoCloseable, DataChangeListener, OdlOfconfigApiService {

    private static final Logger LOG = LoggerFactory.getLogger(OfconfigSouthboundImpl.class);
    public static final InstanceIdentifier<Topology> NETCONF_TOPO_IID =
            InstanceIdentifier.create(NetworkTopology.class).child(Topology.class,
                    new TopologyKey(new TopologyId(TopologyNetconf.QNAME.getLocalName())));

    private ListenerRegistration<DataChangeListener> dclReg;
    private MountPointService mountService;
    private DataBroker dataBroker;

    private OfconfigSouthboundImplHelper helper = null;

    private MdsalUtils mdsalUtils = new MdsalUtils();



    @Override
    public void onSessionInitiated(ProviderContext session) {

        LOG.info("Ofconfig Southbound Impl Session Initiated");

        // Get references to the data broker and mount service
        this.mountService = session.getSALService(MountPointService.class);
        this.dataBroker = session.getSALService(DataBroker.class);

        helper = new OfconfigSouthboundImplHelper(mountService, dataBroker);


        OfconfigInvTopoinitializer initializer = new OfconfigInvTopoinitializer();
        initializer.initializeOfconfigTopology(dataBroker,
                OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID, LogicalDatastoreType.CONFIGURATION);
        initializer.initializeOfconfigTopology(dataBroker,
                OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID, LogicalDatastoreType.OPERATIONAL);

        initializer.initializeOfconfigTopology(dataBroker,
                OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID, LogicalDatastoreType.CONFIGURATION);
        initializer.initializeOfconfigTopology(dataBroker,
                OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID, LogicalDatastoreType.OPERATIONAL);

        // init ofconfig topo node
        List<NodeId> netconfNodeIds = helper.getAllNetconfNodeIds();
        for (NodeId netconfNodeId : netconfNodeIds) {
            createOfconfigNode(netconfNodeId);
        }
        // Register ourselves as data change listener for changes on Netconf
        // nodes. Netconf nodes are accessed via "Netconf Topology" - a special
        // topology that is created by the system infrastructure. It contains
        // all Netconf nodes the Netconf connector knows about. NETCONF_TOPO_IID
        // is equivalent to the following URL:
        // .../restconf/operational/network-topology:network-topology/topology/topology-netconf
        if (dataBroker != null) {
            this.dclReg = dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL,
                    NETCONF_TOPO_IID.child(Node.class), this, DataChangeScope.SUBTREE);
        }



    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        LOG.info("OnDataChange, change: {}", change);

        for (Entry<InstanceIdentifier<?>, DataObject> entry : change.getCreatedData().entrySet()) {
            if (entry.getKey().getTargetType() == NetconfNode.class) {
                NodeId nodeId = helper.getNodeId(entry.getKey());

                // To determine whether the equipment is support ofconfig
                createOfconfigNode(nodeId);



            }
        }

    }

    private void createOfconfigNode(NodeId nodeId) {
        LOG.info("NETCONF Node: {} was created", nodeId.getValue());
        Optional<OfconfigInventoryTopoHandler> handlerOptional =
                helper.getOfconfigInventoryTopoHandler(nodeId);

        if (handlerOptional.isPresent()) {
            LOG.debug(
                    "NETCONF Node: {} is of-config capable switch,add capable switch configuration to Inventory tolopogy",
                    nodeId.getValue());
            handlerOptional.get().addOfconfigNodeToInventory(nodeId, mountService, dataBroker);
        } else {
            LOG.info("NETCONF Node: {} isn't of-config capable switch", nodeId.getValue());

        }
    }


    @Override
    public void close() throws Exception {
        if (this.dclReg != null) {
            this.dclReg.close();
        }

    }

    @Override
    public Future<RpcResult<Void>> syncCapcableSwitch(SyncCapcableSwitchInput input) {
        // TODO Auto-generated method stub


        SettableFuture<RpcResult<Void>> resultFuture = SettableFuture.create();

        NodeId nodeId = new NodeId(new Uri(input.getNodeId()));
        NodeKey nodeKey = new NodeKey(nodeId);
        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();

        ReadWriteTransaction rwTx = dataBroker.newReadWriteTransaction();

        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, dataBroker);
        if (node == null) {

            RpcResult<Void> result =
                    RpcResultBuilder.<Void>failed()
                            .withError(ErrorType.APPLICATION,
                                    input.getNodeId() + " capable switch node dosen't exist")
                            .build();
            resultFuture.set(result);
        } else {

            String netconfId = node.getAugmentation(OfconfigCapableSwitchAugmentation.class)
                    .getOfconfigCapableSwitchAttributes().getNetconfTopologyNodeId();
            try {
                createOfconfigNode(new NodeId(netconfId));

                RpcResult<Void> result = RpcResultBuilder.<Void>success().build();
                resultFuture.set(result);
            } catch (Exception e) {
                LOG.error("Error sync Capcable Switch,capableNodeId:{}, {}", input.getNodeId(), e);
                RpcResult<Void> result =
                        RpcResultBuilder.<Void>failed().withError(ErrorType.APPLICATION,
                                input.getNodeId() + " sync capcable switch fail").build();
                resultFuture.set(result);
            }

        }


        return resultFuture;
    }



}
