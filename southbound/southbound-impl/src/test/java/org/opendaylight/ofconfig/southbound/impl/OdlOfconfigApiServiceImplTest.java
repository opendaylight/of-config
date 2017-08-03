/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.utils.OfconfigHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.QueryLogicalSwitchNodeIdInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.QueryLogicalSwitchNodeIdOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.SyncCapcableSwitchInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

public class OdlOfconfigApiServiceImplTest extends OFconfigTestBase {


    private final NodeId netconfNodeId = new NodeId("test-netconf-node");

    private OdlOfconfigApiServiceImpl odlOfconfigApiServiceImpl = null;


    @Override
    @Before
    public void setUp() {
        super.setUp();

        initMountService(netconfNodeId);

        odlOfconfigApiServiceImpl = new OdlOfconfigApiServiceImpl(this.databroker, this.mountService);

        ofconfigHelper = new OfconfigHelper(mountService, databroker);

    }



    @After
    public void tearDown() throws Exception {}

    @Test
    public void test_sync_capcable_switch() {

        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(new NodeId("test_switch"));

        SyncCapcableSwitchInputBuilder builder = new SyncCapcableSwitchInputBuilder();
        builder.setNodeId("test-netconf-node");

        RpcResult result = null;
        try {
            result = odlOfconfigApiServiceImpl.syncCapcableSwitch(builder.build()).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        assertTrue(result.isSuccessful());



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


        String nodeIdString = netconfNodeId.getValue() + ":" + "ofconf-device" + ":" + "test_sw";

        NodeId logicaSwNodeId = new NodeId(nodeIdString);
        NodeKey logicalNodeKey = new NodeKey(logicaSwNodeId);

        InstanceIdentifier<Node> logicaliid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID))
                .child(Node.class, logicalNodeKey).build();


        Node logicaLnode =
                mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, logicaliid, databroker);

        OfconfigLogicalSwitchAugmentation logicSwitchNode =
                logicaLnode.getAugmentation(OfconfigLogicalSwitchAugmentation.class);


        assertEquals("test_ctl_new", logicSwitchNode.getOfconfigLogicalSwitchAttributes()
                .getLogicalSwitch().getControllers().getController().get(0).getId().getValue());



    }



    @Test
    public void test_query_capcable_switch_nodeId_by_dpId() {
        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);

        initOfConfigLogicalSwitchTopo(new NodeId("test_switch"));


        QueryLogicalSwitchNodeIdInputBuilder builder = new QueryLogicalSwitchNodeIdInputBuilder();
        builder.setDatapathId("00:00:7a:31:cd:91:04:40");


        try {
            RpcResult<QueryLogicalSwitchNodeIdOutput> result =
                    odlOfconfigApiServiceImpl.queryLogicalSwitchNodeId(builder.build()).get();

            assertTrue(result.isSuccessful());

            String nodeId = "test_switch" + ":" + "test_capableSwitch:test_sw";


            assertEquals(nodeId, result.getResult().getNodeId());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }



    }



}
