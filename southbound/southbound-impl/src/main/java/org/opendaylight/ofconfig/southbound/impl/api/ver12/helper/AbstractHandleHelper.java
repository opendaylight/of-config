/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.api.ver12.helper;

import java.util.concurrent.Future;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.SettableFuture;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
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



/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public abstract class AbstractHandleHelper {


    protected MountPointService mountService;
    protected DataBroker dataBroker;

    protected MdsalUtils mdsalUtils = new MdsalUtils();

    public AbstractHandleHelper(MountPointService mountService, DataBroker dataBroker) {
        super();
        this.mountService = mountService;
        this.dataBroker = dataBroker;
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
}
