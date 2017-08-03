/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.util.concurrent.SettableFuture;
import java.util.List;
import java.util.concurrent.Future;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.ofconfig.southbound.impl.utils.OfconfigHelper;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.OdlOfconfigApiService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.QueryLogicalSwitchNodeIdInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.QueryLogicalSwitchNodeIdOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.QueryLogicalSwitchNodeIdOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.SyncCapcableSwitchInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentation;
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
public class OdlOfconfigApiServiceImpl implements OdlOfconfigApiService, AutoCloseable {


    private static final Logger LOG = LoggerFactory.getLogger(OdlOfconfigApiServiceImpl.class);

    private final DataBroker dataBroker;

    private final MdsalUtils mdsalUtils = new MdsalUtils();

    private final OfconfigHelper helper;

    public OdlOfconfigApiServiceImpl(DataBroker dataBroker, MountPointService mountPointService) {
        this.dataBroker = dataBroker;
        this.helper = new OfconfigHelper(mountPointService, dataBroker);
    }

    @Override
    public void close() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.
     * OdlOfconfigApiService#syncCapcableSwitch(org.opendaylight.yang.gen.v1.urn.opendaylight.params
     * .xml.ns.yang.ofconfig.base.api.rev150901.SyncCapcableSwitchInput)
     */
    @Override
    public Future<RpcResult<Void>> syncCapcableSwitch(SyncCapcableSwitchInput input) {
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

                helper.createOfconfigNode(new NodeId(netconfId));

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



    @Override
    public Future<RpcResult<QueryLogicalSwitchNodeIdOutput>> queryLogicalSwitchNodeId(
            final QueryLogicalSwitchNodeIdInput input) {

        SettableFuture<RpcResult<QueryLogicalSwitchNodeIdOutput>> resultFuture =
                SettableFuture.create();

        InstanceIdentifier<Topology> iid =
                InstanceIdentifier.builder(NetworkTopology.class)
                        .child(Topology.class,
                                new TopologyKey(OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID))
                .build();

        Topology logicalSwitchTopology =
                mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, dataBroker);

        if (logicalSwitchTopology == null) {

            RpcResult<QueryLogicalSwitchNodeIdOutput> result =
                    RpcResultBuilder.<QueryLogicalSwitchNodeIdOutput>failed()
                            .withError(ErrorType.APPLICATION,
                                    input.getDatapathId() + " logical switch node dosen't exist")
                            .build();
            resultFuture.set(result);
        }


        List<Node> logicalSwithNodes = logicalSwitchTopology.getNode();

        Optional<Node> logicalSwithNode =
                Iterators.tryFind(logicalSwithNodes.iterator(), node -> {

                    OfconfigLogicalSwitchAugmentation logcialSwitchNode =
                            node.getAugmentation(OfconfigLogicalSwitchAugmentation.class);

                    return logcialSwitchNode.getOfconfigLogicalSwitchAttributes()
                            .getDatapathId().getValue().equals(input.getDatapathId());
                });

        if (!logicalSwithNode.isPresent()) {

            RpcResult<QueryLogicalSwitchNodeIdOutput> result =
                    RpcResultBuilder.<QueryLogicalSwitchNodeIdOutput>failed()
                            .withError(ErrorType.APPLICATION,
                                    input.getDatapathId() + " logical switch node dosen't exist")
                            .build();
            resultFuture.set(result);
        }

        QueryLogicalSwitchNodeIdOutputBuilder resultBuilder =
                new QueryLogicalSwitchNodeIdOutputBuilder();
        resultBuilder.setNodeId(logicalSwithNode.get().getNodeId().getValue());

        RpcResult<QueryLogicalSwitchNodeIdOutput> result =
                RpcResultBuilder.success(resultBuilder.build()).build();

        resultFuture.set(result);

        return resultFuture;
    }



}
