/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12;

import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OFconfigTestBase;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.netconf.node.connection.status.AvailableCapabilitiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.netconf.node.connection.status.available.capabilities.AvailableCapability;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.netconf.node.connection.status.available.capabilities.AvailableCapabilityBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;



/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class OfconfigTopoHandlerTest extends OFconfigTestBase {

    private NodeId netconfNodeId = new NodeId("test-netconf-node");
    private NodeId netconfNodeId2 = new NodeId("test-netconf-node2");

    @Before
    public void setUp() {
        super.setUp();
        // init NETCONF topo
        initNetConfTopo();
    }


    private void initNetConfTopo() {

        InstanceIdentifier<Topology> path =
                InstanceIdentifier.create(NetworkTopology.class).child(Topology.class,
                        new TopologyKey(new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)));


        NetconfNodeBuilder netconfNodeBuilder = new NetconfNodeBuilder();


        AvailableCapabilityBuilder builder = new AvailableCapabilityBuilder()
                .setCapability(OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY)
                .setCapabilityOrigin(AvailableCapability.CapabilityOrigin.UserDefined);

        List<AvailableCapability> availableCapabilities = Lists.newArrayList();

        availableCapabilities.add(builder.build());

        AvailableCapabilitiesBuilder availableCapabilitiesBuilder =
                new AvailableCapabilitiesBuilder();
        availableCapabilitiesBuilder.setAvailableCapability(availableCapabilities);

        netconfNodeBuilder.setAvailableCapabilities(availableCapabilitiesBuilder.build())
                .setKeepaliveDelay(100l);

        NodeBuilder nodeBuilder = new NodeBuilder();
        nodeBuilder.setKey(new NodeKey(netconfNodeId)).setNodeId(netconfNodeId)
                .addAugmentation(NetconfNode.class, netconfNodeBuilder.build());

        NetconfNodeBuilder netconfNodeBuilder2 = new NetconfNodeBuilder();
        netconfNodeBuilder2.setAvailableCapabilities(availableCapabilitiesBuilder.build())
                .setKeepaliveDelay(1000l);
        NodeBuilder nodeBuilder2 = new NodeBuilder();
        nodeBuilder2.setKey(new NodeKey(netconfNodeId2)).setNodeId(netconfNodeId2)
                .addAugmentation(NetconfNode.class, netconfNodeBuilder2.build());


        TopologyBuilder topoBuilder = new TopologyBuilder();
        topoBuilder.setKey(new TopologyKey(new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)))
                .setTopologyId(new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID))
                .setNode(Lists.newArrayList(nodeBuilder.build(), nodeBuilder2.build()));


        mdsalUtils.put(LogicalDatastoreType.OPERATIONAL, path, topoBuilder.build(), databroker);
    }


}
