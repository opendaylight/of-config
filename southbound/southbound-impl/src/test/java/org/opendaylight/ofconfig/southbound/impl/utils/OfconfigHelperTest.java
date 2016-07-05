/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CheckedFuture;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OFconfigTestBase;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddressBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.OFDatapathIdType;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.OFConfigIdType;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.OFControllerType.Protocol;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.LogicalSwitchesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.logical.switches.SwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.logical.switches.SwitchKey;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.oflogicalswitchtype.ControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.oflogicalswitchtype.controllers.Controller;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.oflogicalswitchtype.controllers.ControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.oflogicalswitchtype.controllers.ControllerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.netconf.node.connection.status.AvailableCapabilitiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;



/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class OfconfigHelperTest extends OFconfigTestBase {

    private NodeId netconfNodeId = new NodeId("test-netconf-node");
    private NodeId netconfNodeId2 = new NodeId("test-netconf-node2");

    private OfconfigHelper ofconfigHelper;


    @Before
    public void setUp() {
        super.setUp();
        // init NETCONF topo
        initNetConfTopo();

        ofconfigHelper = new OfconfigHelper(mountService, databroker);

    }

    private void initNetConfTopo() {

        InstanceIdentifier<Topology> path =
                InstanceIdentifier.create(NetworkTopology.class).child(Topology.class,
                        new TopologyKey(new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)));


        NetconfNodeBuilder netconfNodeBuilder = new NetconfNodeBuilder();

        List<String> availableCapabilities = Lists.newArrayList();

        availableCapabilities.add(OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY);


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


    @Test
    public void test_get_ofconf_device_nodeId_from_netconf_topo() {

        List<NodeId> nodeIds = ofconfigHelper.getAllNetconfNodeIds();

        assertEquals(2, nodeIds.size());

        for (NodeId nodeId : nodeIds) {
            if (!nodeId.equals(netconfNodeId) && !nodeId.equals(netconfNodeId2)) {
                fail();
            }
        }
    }

    @Test
    public void get_netconf_netconfNode_by_nodeId() {


        Optional<NetconfNode> netconfNodeOptional =
                ofconfigHelper.getNetconfNodeByNodeId(netconfNodeId);
        if (!netconfNodeOptional.isPresent()) {
            fail();
        }

        assertEquals(Long.valueOf(100), netconfNodeOptional.get().getKeepaliveDelay());

        netconfNodeOptional = ofconfigHelper.getNetconfNodeByNodeId(netconfNodeId2);
        if (!netconfNodeOptional.isPresent()) {
            fail();
        }


        assertEquals(Long.valueOf(1000), netconfNodeOptional.get().getKeepaliveDelay());

    }


    @Test
    public void test_create_and_remove_ofconfig_Node() {

        try {

            CapableSwitchBuilder capableSwitchBuilder = new CapableSwitchBuilder();
            capableSwitchBuilder.setConfigVersion("1.4").setId("ofconf-device");

            ControllersBuilder ctlllerBuilder = new ControllersBuilder();

            List<Controller> ctllers = new ArrayList<>();

            ControllerBuilder builder = new ControllerBuilder();



            builder.setId(new OFConfigIdType("test_ctl"))
                    .setKey(new ControllerKey(new OFConfigIdType("test_ctl"))).setProtocol(Protocol.Tcp)
                    .setIpAddress(IpAddressBuilder.getDefaultInstance("127.0.0.1"))
                    .setPort(PortNumber.getDefaultInstance("6630"));

            ctllers.add(builder.build());

            ctlllerBuilder.setController(ctllers);


            LogicalSwitchesBuilder lsBuilder = new LogicalSwitchesBuilder();

            List<Switch> swlists = Lists.newArrayList();

            SwitchBuilder switchBuilder = new SwitchBuilder();
            switchBuilder.setId(new OFConfigIdType("test_sw"))
                    .setKey(new SwitchKey(new OFConfigIdType("test_sw")))
                    .setControllers(ctlllerBuilder.build())
                    .setDatapathId(new OFDatapathIdType("00:00:7a:31:cd:91:04:40"));

            swlists.add(switchBuilder.build());

            lsBuilder.setSwitch(swlists);
            capableSwitchBuilder.setLogicalSwitches(lsBuilder.build());

            CheckedFuture resultFuture = mock(CheckedFuture.class);
            when(resultFuture.checkedGet()).thenReturn(Optional.of(capableSwitchBuilder.build()));

            InstanceIdentifier<CapableSwitch> iid = InstanceIdentifier.create(CapableSwitch.class);

            ReadOnlyTransaction rtx = mock(ReadOnlyTransaction.class);
            when(rtx.read(LogicalDatastoreType.CONFIGURATION, iid)).thenReturn(resultFuture);

            DataBroker mountDataBroker = mock(DataBroker.class);
            when(mountDataBroker.newReadOnlyTransaction()).thenReturn(rtx);

            MountPoint mountPoint = mock(MountPoint.class);
            when(mountPoint.getService(DataBroker.class)).thenReturn(Optional.of(mountDataBroker));



            when(mountService.getMountPoint(OfconfigConstants.NETCONF_TOPO_IID.child(Node.class,
                    new NodeKey(new NodeId(netconfNodeId))))).thenReturn(Optional.of(mountPoint));

            ofconfigHelper.createOfconfigNode(netconfNodeId);


            NodeKey nodeKey = new NodeKey(netconfNodeId);

            InstanceIdentifier<Node> nodeiid = InstanceIdentifier.builder(NetworkTopology.class)
                    .child(Topology.class,
                            new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                    .child(Node.class, nodeKey).build();


            Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, nodeiid, databroker);


            OfconfigCapableSwitchAugmentation capableSwNode =
                    node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

            assertEquals(netconfNodeId.getValue(),
                    capableSwNode.getOfconfigCapableSwitchAttributes().getNetconfTopologyNodeId());

            assertEquals("ofconf-device",
                    capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch().getId());


            String nodeStringprefix =
                    netconfNodeId.getValue() + ":" + "ofconf-device" + ":" + "test_sw";

            NodeId logicaSwNodeId = new NodeId(nodeStringprefix);
            NodeKey logicalNodeKey = new NodeKey(logicaSwNodeId);

            InstanceIdentifier<Node> logicaliid = InstanceIdentifier.builder(NetworkTopology.class)
                    .child(Topology.class,
                            new TopologyKey(OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID))
                    .child(Node.class, logicalNodeKey).build();


            Node logicaLnode =
                    mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, logicaliid, databroker);

            OfconfigLogicalSwitchAugmentation logicSwitchNode =
                    logicaLnode.getAugmentation(OfconfigLogicalSwitchAugmentation.class);


            assertEquals("test_ctl", logicSwitchNode.getOfconfigLogicalSwitchAttributes()
                    .getLogicalSwitch().getControllers().getController().get(0).getId().getValue());


            NodeKey logicalSwitchNodeKey = new NodeKey(netconfNodeId);



            ofconfigHelper.destroyOfconfigNode(netconfNodeId);

            node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, nodeiid, databroker);

            assertNull(node);

            node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, logicaliid, databroker);

            assertNull(node);


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }



}
