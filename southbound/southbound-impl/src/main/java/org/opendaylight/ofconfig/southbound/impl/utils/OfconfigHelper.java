/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.utils;


import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.topology.OfconfigTopoHandler;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.netconf.node.connection.status.available.capabilities.AvailableCapability;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.Identifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class OfconfigHelper {

    private static final Logger LOG = LoggerFactory.getLogger(OfconfigHelper.class);

    private final MountPointService mountService;
    private final DataBroker dataBroker;

    private final MdsalUtils mdsalUtils = new MdsalUtils();


    public OfconfigHelper(MountPointService mountService, DataBroker dataBroker) {
        this.mountService = mountService;
        this.dataBroker = dataBroker;
    }



    public List<NodeId> getAllNetconfNodeIds() {

        List<NodeId> resultNodeIds = new ArrayList<>();

        InstanceIdentifier<Topology> path =
                InstanceIdentifier.create(NetworkTopology.class).child(Topology.class,
                        new TopologyKey(new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)));

        Topology netconfTopo = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, path, dataBroker);
        if (netconfTopo == null) {
            return resultNodeIds;
        }

        List<Node> netconfNodes = netconfTopo.getNode();

        resultNodeIds = Lists.transform(netconfNodes, netconfNode -> netconfNode.getNodeId());

        return resultNodeIds;
    }


    public NodeId getNodeId(final InstanceIdentifier<?> path) {
        for (InstanceIdentifier.PathArgument pathArgument : path.getPathArguments()) {
            if (pathArgument instanceof InstanceIdentifier.IdentifiableItem<?, ?>) {

                final Identifier key =
                        ((InstanceIdentifier.IdentifiableItem) pathArgument).getKey();
                if (key instanceof NodeKey) {
                    return ((NodeKey) key).getNodeId();
                }
            }
        }
        return null;
    }

    public Optional<NetconfNode> getNetconfNodeByNodeId(NodeId netconfNodeId) {

        InstanceIdentifier<Node> path =
                InstanceIdentifier.create(NetworkTopology.class)
                        .child(Topology.class,
                                new TopologyKey(
                                        new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)))
                        .child(Node.class, new NodeKey(netconfNodeId));

        NetconfNode nnode = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, path, dataBroker)
                .getAugmentation(NetconfNode.class);
        if (nnode != null) {
            return Optional.of(nnode);

        }
        return Optional.absent();


    }


    public Optional<OfconfigTopoHandler> getOfconfigInventoryTopoHandler(NodeId netconfNodeId) {

        Optional<NetconfNode> nodeOptional = getNetconfNodeByNodeId(netconfNodeId);

        if (nodeOptional.isPresent() && isOfconfigDeviceNode(nodeOptional.get())) {
            return Optional.of(OfconfigTopoHandler
                    .getHandlerInstance(OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY));
        }

        return Optional.absent();
    }

    public boolean isOfconfigDeviceNode(NetconfNode netconfigNode) {

        if (netconfigNode.getAvailableCapabilities() == null) {
            return false;
        }

        List<AvailableCapability> capabilities =
                netconfigNode.getAvailableCapabilities().getAvailableCapability();

        java.util.Optional<AvailableCapability> ifOfCOnfig = capabilities.stream()
                .filter(c -> OfconfigConstants.OF_CONFIG_CAPABILITIES.contains(c.getCapability()))
                .findFirst();

        return ifOfCOnfig.isPresent();
    }



    public void createOfconfigNode(NodeId nodeId) throws Exception {
        LOG.info("NETCONF Node: {} was created", nodeId.getValue());
        Optional<OfconfigTopoHandler> handlerOptional = getOfconfigInventoryTopoHandler(nodeId);

        if (handlerOptional.isPresent()) {
            LOG.debug(
                    "NETCONF Node: {} is of-config capable switch,add capable "
                    + "switch configuration to Inventory tolopogy",
                    nodeId.getValue());


            NetconfNode netconfNode = getNetconfNodeByNodeId(nodeId).get();

            handlerOptional.get().addOfconfigNode(nodeId, netconfNode, mountService, dataBroker);
        } else {
            LOG.info("NETCONF Node: {} isn't of-config capable switch", nodeId.getValue());

        }
    }

    public void updateOfconfigNode(NodeId nodeId) throws Exception {
        LOG.info("NETCONF Node: {} was updated", nodeId.getValue());
        createOfconfigNode(nodeId);
    }

    public void destroyOfconfigNode(NodeId nodeId) throws Exception {
        LOG.info("NETCONF Node: {} was deleted", nodeId.getValue());

        Optional<OfconfigTopoHandler> handlerOptional = getOfconfigInventoryTopoHandler(nodeId);


        if (handlerOptional.isPresent()) {
            LOG.debug(
                    "NETCONF Node: {} is of-config capable switch,add capable switch "
                    + "configuration to Inventory tolopogy",
                    nodeId.getValue());


            NetconfNode netconfNode = getNetconfNodeByNodeId(nodeId).get();

            handlerOptional.get().removeOfconfigNode(nodeId, dataBroker);
        } else {
            LOG.info("NETCONF Node: {} isn't of-config capable switch", nodeId.getValue());

        }

    }
}
