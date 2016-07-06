/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.api;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Future;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.topology.OfconfigTopoHandler;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.ofconfig.southbound.impl.utils.OfconfigHelper;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public abstract class AbstractHandleHelper {


    private final static Logger logger = LoggerFactory.getLogger(AbstractHandleHelper.class);

    protected MountPointService mountService;
    protected DataBroker dataBroker;

    protected MdsalUtils mdsalUtils = new MdsalUtils();
    private OfconfigHelper helper;

    public AbstractHandleHelper(MountPointService mountService, DataBroker dataBroker) {
        super();
        this.mountService = mountService;
        this.dataBroker = dataBroker;
        this.helper = new OfconfigHelper(mountService, dataBroker);
    }



    protected Node getLogicalSwitchTopoNodeByNodeId(String logicalSWnodeId) {

        NodeId nodeId = new NodeId(new Uri(logicalSWnodeId));
        NodeKey nodeKey = new NodeKey(nodeId);
        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();

        return mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, dataBroker);

    }

    protected Node getCapableSwitchTopoNodeByNodeId(String capableSWnodeId) {

        NodeId nodeId = new NodeId(new Uri(capableSWnodeId));
        NodeKey nodeKey = new NodeKey(nodeId);
        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();

        return mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, dataBroker);

    }


    protected Optional<CapableSwitch> getDeviceCapableSwitch(String netconfNodeId) {


        final Optional<MountPoint> capableSwichNodeOptional =
                mountService.getMountPoint(OfconfigConstants.NETCONF_TOPO_IID.child(Node.class,
                        new NodeKey(new NodeId(netconfNodeId))));

        MountPoint netconfMountPoint = capableSwichNodeOptional.get();


        final DataBroker capableSwichNodeBroker =
                netconfMountPoint.getService(DataBroker.class).get();

        ReadWriteTransaction deviceRwTx = capableSwichNodeBroker.newReadWriteTransaction();

        final InstanceIdentifier<CapableSwitch> capableSwitchId =
                InstanceIdentifier.builder(CapableSwitch.class).build();

        Optional<CapableSwitch> capableSwitchOptional = null;
        try {
            capableSwitchOptional =
                    deviceRwTx.read(LogicalDatastoreType.CONFIGURATION, capableSwitchId).get();
        } catch (Exception e) {
            logger.error("get capable switch info occur error,netconf topology id:{}",
                    netconfNodeId, e);
            throw new RuntimeException(e);
        }

        return capableSwitchOptional;
    }



    protected Future<RpcResult<Void>> buildNotFoundResult(String nodeId) {

        SettableFuture<RpcResult<Void>> resultFuture = SettableFuture.create();



        RpcResult<Void> rpcResult = RpcResultBuilder.<Void>failed()
                .withError(ErrorType.APPLICATION,
                        "No corresponding nodes are found in the topology,nodeId:" + nodeId)
                .build();

        resultFuture.set(rpcResult);
        return resultFuture;

    }



    protected DataBroker getMountPointDataBroker(String netconfNodeId) {

        final Optional<MountPoint> capableSwichNodeOptional =
                mountService.getMountPoint(OfconfigConstants.NETCONF_TOPO_IID.child(Node.class,
                        new NodeKey(new NodeId(netconfNodeId))));

        MountPoint netconfMountPoint = capableSwichNodeOptional.get();

        final DataBroker capableSwichNodeBroker =
                netconfMountPoint.getService(DataBroker.class).get();

        return capableSwichNodeBroker;
    }


    protected void createOfconfigNode(NodeId nodeId) throws Exception {
        Optional<OfconfigTopoHandler> handlerOptional =
                helper.getOfconfigInventoryTopoHandler(nodeId);

        if (handlerOptional.isPresent()) {



            NetconfNode netconfNode = helper.getNetconfNodeByNodeId(nodeId).get();

            handlerOptional.get().addOfconfigNode(nodeId, netconfNode, mountService, dataBroker);
        }
    }
}
