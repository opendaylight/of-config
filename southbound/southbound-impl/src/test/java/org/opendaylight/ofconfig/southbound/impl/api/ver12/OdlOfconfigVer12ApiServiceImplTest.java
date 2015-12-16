/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.api.ver12;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.ofconfig.southbound.impl.OFconfigTestBase;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpAddressBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.DatapathIdType;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.KeyValue.KeyType;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.OFConfigId;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.OFControllerType.Protocol;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofownedcertificatetype.PrivateKeyBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleExtCertInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleFlowtableInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleLogicSwitchInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandlePortResourceInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleQueueResourceInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleTunnelInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.Controller;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.ControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.ControllerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_ext_cert.ExternalCertificate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_ext_cert.ExternalCertificateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_ext_cert.ExternalCertificateKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_flowtable.FlowTable;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_flowtable.FlowTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_flowtable.FlowTableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_logic_switch.Switch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_logic_switch.SwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_owned_cert.OwnedCertificate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_owned_cert.OwnedCertificateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_owned_cert.OwnedCertificateKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_port_resource.Port;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_port_resource.PortBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_port_resource.PortKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_queue_resource.Queue;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_queue_resource.QueueBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_queue_resource.QueueKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.collect.Lists;

public class OdlOfconfigVer12ApiServiceImplTest extends OFconfigTestBase {

    private OdlOfconfigVer12ApiServiceImpl odlOfconfigVer12ApiServiceImpl;

    protected NodeId netconfNodeId = new NodeId("test-netconf-node");

    @Before
    public void setUp() {
        super.setUp();
        ProviderContext providerContext = mock(ProviderContext.class);
        when(providerContext.getSALService(DataBroker.class)).thenReturn(this.databroker);
        initMountService(netconfNodeId);
        when(providerContext.getSALService(MountPointService.class)).thenReturn(this.mountService);

        odlOfconfigVer12ApiServiceImpl = new OdlOfconfigVer12ApiServiceImpl();
        odlOfconfigVer12ApiServiceImpl.onSessionInitiated(providerContext);

    }

    @Test
    public void test_handle_OwnedCert() throws Exception {

        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(netconfNodeId);



        // put
        List<OwnedCertificate> ownedCertificates = Lists.newArrayList();

        PrivateKeyBuilder privateKeyBuilder = new PrivateKeyBuilder();
        privateKeyBuilder.setKeyType(KeyType.DSA).setKeyData("testPrivateKey");

        OwnedCertificateBuilder ownedCertificateBuilder = new OwnedCertificateBuilder();
        ownedCertificateBuilder.setResourceId(Uri.getDefaultInstance("test-owned-Certificate"))
                .setCertificate("test-Certificate")
                .setKey(new OwnedCertificateKey(Uri.getDefaultInstance("test-owned-Certificate")))
                .setPrivateKey(privateKeyBuilder.build());


        ownedCertificates.add(ownedCertificateBuilder.build());
        HandleOwnedCertInputBuilder inputBuilder = new HandleOwnedCertInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Put)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue())
                .setOwnedCertificate(ownedCertificates);


        RpcResult<Void> rpcResult =
                odlOfconfigVer12ApiServiceImpl.handleOwnedCert(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();

        assertEquals("test-Certificate",
                deviceCapableSwitch.getResources().getOwnedCertificate().get(0).getCertificate());


        NodeKey nodeKey = new NodeKey(netconfNodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        assertEquals("test-Certificate", capableSwNode.getOfconfigCapableSwitchAttributes()
                .getCapableSwitch().getResources().getOwnedCertificate().get(0).getCertificate());


        // merge
        ownedCertificates = Lists.newArrayList();

        privateKeyBuilder = new PrivateKeyBuilder();
        privateKeyBuilder.setKeyType(KeyType.DSA).setKeyData("testPrivateKey1");

        ownedCertificateBuilder = new OwnedCertificateBuilder();
        ownedCertificateBuilder.setResourceId(Uri.getDefaultInstance("test-owned-Certificate1"))
                .setCertificate("test-Certificate1")
                .setKey(new OwnedCertificateKey(Uri.getDefaultInstance("test-owned-Certificate1")))
                .setPrivateKey(privateKeyBuilder.build());

        ownedCertificates.add(ownedCertificateBuilder.build());

        inputBuilder = new HandleOwnedCertInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Merge)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue())
                .setOwnedCertificate(ownedCertificates);


        rpcResult = odlOfconfigVer12ApiServiceImpl.handleOwnedCert(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();

        assertEquals(2, deviceCapableSwitch.getResources().getOwnedCertificate().size());

        List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.OwnedCertificate> ownedCertificateList =
                deviceCapableSwitch.getResources().getOwnedCertificate();

        List<String> certificates = Lists.newArrayList(ownedCertificateList.get(0).getCertificate(),
                ownedCertificateList.get(1).getCertificate());

        assertTrue(certificates.contains("test-Certificate"));
        assertTrue(certificates.contains("test-Certificate1"));



        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);



        ownedCertificateList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getOwnedCertificate();

        certificates = Lists.newArrayList(ownedCertificateList.get(0).getCertificate(),
                ownedCertificateList.get(1).getCertificate());

        assertTrue(certificates.contains("test-Certificate"));
        assertTrue(certificates.contains("test-Certificate1"));

        // delete

        ownedCertificates = Lists.newArrayList();


        ownedCertificateBuilder = new OwnedCertificateBuilder();
        ownedCertificateBuilder.setResourceId(Uri.getDefaultInstance("test-owned-Certificate1"));

        ownedCertificates.add(ownedCertificateBuilder.build());

        inputBuilder = new HandleOwnedCertInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue())
                .setOwnedCertificate(ownedCertificates);


        rpcResult = odlOfconfigVer12ApiServiceImpl.handleOwnedCert(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();

        assertEquals(1, deviceCapableSwitch.getResources().getOwnedCertificate().size());

        assertEquals("test-Certificate",
                deviceCapableSwitch.getResources().getOwnedCertificate().get(0).getCertificate());

        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);



        ownedCertificateList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getOwnedCertificate();

        assertEquals(1, ownedCertificateList.size());

        certificates = Lists.newArrayList(ownedCertificateList.get(0).getCertificate());

        assertTrue(certificates.contains("test-Certificate"));
        assertTrue(!certificates.contains("test-Certificate1"));
    }

    @Test
    public void test_handle_Flowtable() throws Exception {

        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(netconfNodeId);

        // put
        List<FlowTable> flowTables = Lists.newArrayList();

        FlowTableBuilder flowTableBuilder = new FlowTableBuilder();
        flowTableBuilder.setKey(new FlowTableKey(Short.valueOf("1"))).setTableId(Short.valueOf("1"))
                .setMaxEntries(100l).setName("test-table")
                .setResourceId(Uri.getDefaultInstance("test-table"));

        flowTables.add(flowTableBuilder.build());
        HandleFlowtableInputBuilder inputBuilder = new HandleFlowtableInputBuilder();

        inputBuilder.setHandleMode(HandleMode.Put)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setFlowTable(flowTables);

        RpcResult<Void> rpcResult =
                odlOfconfigVer12ApiServiceImpl.handleFlowtable(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());

        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();

        assertEquals("test-table",
                deviceCapableSwitch.getResources().getFlowTable().get(0).getName());


        NodeKey nodeKey = new NodeKey(netconfNodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        assertEquals("test-table", capableSwNode.getOfconfigCapableSwitchAttributes()
                .getCapableSwitch().getResources().getFlowTable().get(0).getName());



        // merge
        flowTables = Lists.newArrayList();

        flowTableBuilder = new FlowTableBuilder();
        flowTableBuilder.setKey(new FlowTableKey(Short.valueOf("2"))).setTableId(Short.valueOf("2"))
                .setMaxEntries(100l).setName("test-table1")
                .setResourceId(Uri.getDefaultInstance("test-table1"));

        flowTables.add(flowTableBuilder.build());
        inputBuilder = new HandleFlowtableInputBuilder();

        inputBuilder.setHandleMode(HandleMode.Merge)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setFlowTable(flowTables);

        rpcResult = odlOfconfigVer12ApiServiceImpl.handleFlowtable(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());

        deviceCapableSwitch = this.capableSwitchRef.get();

        assertEquals(2, deviceCapableSwitch.getResources().getFlowTable().size());

        List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.FlowTable> flowTableList =
                deviceCapableSwitch.getResources().getFlowTable();

        List<String> certificates =
                Lists.newArrayList(flowTableList.get(0).getName(), flowTableList.get(1).getName());

        assertTrue(certificates.contains("test-table"));
        assertTrue(certificates.contains("test-table1"));


        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);



        flowTableList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getFlowTable();

        assertEquals(2, flowTableList.size());

        certificates =
                Lists.newArrayList(flowTableList.get(0).getName(), flowTableList.get(1).getName());

        assertTrue(certificates.contains("test-table"));
        assertTrue(certificates.contains("test-table1"));

        // delete
        flowTables = Lists.newArrayList();

        flowTableBuilder = new FlowTableBuilder();
        flowTableBuilder.setTableId(Short.valueOf("2"));

        flowTables.add(flowTableBuilder.build());
        inputBuilder = new HandleFlowtableInputBuilder();

        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setFlowTable(flowTables);

        rpcResult = odlOfconfigVer12ApiServiceImpl.handleFlowtable(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();


        assertEquals(1, deviceCapableSwitch.getResources().getFlowTable().size());
        assertEquals("test-table",
                deviceCapableSwitch.getResources().getFlowTable().get(0).getName());


        nodeKey = new NodeKey(netconfNodeId);



        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        assertEquals(1, capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getFlowTable().size());
        assertEquals("test-table", capableSwNode.getOfconfigCapableSwitchAttributes()
                .getCapableSwitch().getResources().getFlowTable().get(0).getName());

    }

    @Test
    public void test_handle_Controller() throws Exception {

        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(netconfNodeId);


        // put
        HandleControllersInputBuilder inputBuilder = new HandleControllersInputBuilder();

        List<Controller> controllers = Lists.newArrayList();

        ControllerBuilder ctllerBuilder = new ControllerBuilder();

        ctllerBuilder.setId(new OFConfigId("controller1"))
                .setIpAddress(IpAddressBuilder.getDefaultInstance(("127.0.0.1")))
                .setKey(new ControllerKey(new OFConfigId("controller1")))
                .setPort(new PortNumber(6630)).setProtocol(Protocol.Tcp);



        controllers.add(ctllerBuilder.build());

        String nodeIdString =
                netconfNodeId.getValue() + ":" + "test_capableSwitch" + ":" + "test_sw";
        inputBuilder.setHandleMode(HandleMode.Put).setTopoLogicalSwitchNodeId(nodeIdString)
                .setController(controllers);

        RpcResult<Void> rpcResult =
                odlOfconfigVer12ApiServiceImpl.handleControllers(inputBuilder.build()).get();


        assertTrue(rpcResult.isSuccessful());

        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();

        assertEquals(1, deviceCapableSwitch.getLogicalSwitches().getSwitch().get(0).getControllers()
                .getController().size());

        assertEquals("controller1", deviceCapableSwitch.getLogicalSwitches().getSwitch().get(0)
                .getControllers().getController().get(0).getId().getValue());


        NodeKey nodeKey = new NodeKey(netconfNodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);


        assertEquals(1, capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getLogicalSwitches().getSwitch().get(0).getControllers().getController().size());

        assertEquals("controller1",
                capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                        .getLogicalSwitches().getSwitch().get(0).getControllers().getController()
                        .get(0).getId().getValue());

        // merge

        inputBuilder = new HandleControllersInputBuilder();

        controllers = Lists.newArrayList();

        ctllerBuilder = new ControllerBuilder();

        ctllerBuilder.setId(new OFConfigId("controller2"))
                .setIpAddress(IpAddressBuilder.getDefaultInstance(("192.168.1.1")))
                .setKey(new ControllerKey(new OFConfigId("controller2")))
                .setPort(new PortNumber(6630)).setProtocol(Protocol.Tcp);

        controllers.add(ctllerBuilder.build());

        inputBuilder.setHandleMode(HandleMode.Merge).setTopoLogicalSwitchNodeId(nodeIdString)
                .setController(controllers);


        rpcResult = odlOfconfigVer12ApiServiceImpl.handleControllers(inputBuilder.build()).get();


        assertTrue(rpcResult.isSuccessful());

        deviceCapableSwitch = this.capableSwitchRef.get();

        List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.Controller> ctlList =
                deviceCapableSwitch.getLogicalSwitches().getSwitch().get(0).getControllers()
                        .getController();

        assertEquals(2, ctlList.size());

        List<String> controllerIds = Lists.newArrayList(ctlList.get(0).getId().getValue(),
                ctlList.get(1).getId().getValue());

        assertTrue(controllerIds.contains("controller1"));
        assertTrue(controllerIds.contains("controller2"));


        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        ctlList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getLogicalSwitches().getSwitch().get(0).getControllers().getController();


        assertEquals(2, ctlList.size());

        controllerIds = Lists.newArrayList(ctlList.get(0).getId().getValue(),
                ctlList.get(1).getId().getValue());

        assertTrue(controllerIds.contains("controller1"));
        assertTrue(controllerIds.contains("controller2"));

        // delete

        inputBuilder = new HandleControllersInputBuilder();

        controllers = Lists.newArrayList();

        ctllerBuilder = new ControllerBuilder();

        ctllerBuilder.setId(new OFConfigId("controller2"));

        controllers.add(ctllerBuilder.build());

        inputBuilder.setHandleMode(HandleMode.Delete).setTopoLogicalSwitchNodeId(nodeIdString)
                .setController(controllers);

        rpcResult = odlOfconfigVer12ApiServiceImpl.handleControllers(inputBuilder.build()).get();


        assertTrue(rpcResult.isSuccessful());

        deviceCapableSwitch = this.capableSwitchRef.get();

        assertEquals(1, deviceCapableSwitch.getLogicalSwitches().getSwitch().get(0).getControllers()
                .getController().size());

        assertEquals("controller1", deviceCapableSwitch.getLogicalSwitches().getSwitch().get(0)
                .getControllers().getController().get(0).getId().getValue());


        nodeKey = new NodeKey(netconfNodeId);

        iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);


        assertEquals(1, capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getLogicalSwitches().getSwitch().get(0).getControllers().getController().size());

        assertEquals("controller1",
                capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                        .getLogicalSwitches().getSwitch().get(0).getControllers().getController()
                        .get(0).getId().getValue());

    }


    @Test
    public void test_handle_ExtCert() throws Exception {

        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(netconfNodeId);


        // put
        List<ExternalCertificate> extCertificates = new ArrayList<>();

        ExternalCertificateBuilder extCertificateBuilder = new ExternalCertificateBuilder();

        extCertificateBuilder.setCertificate("extCertificate1")
                .setKey(new ExternalCertificateKey(Uri.getDefaultInstance("resource_1")))
                .setResourceId(Uri.getDefaultInstance("resource_1"));

        extCertificates.add(extCertificateBuilder.build());
        HandleExtCertInputBuilder inputBuilder = new HandleExtCertInputBuilder();

        inputBuilder.setHandleMode(HandleMode.Put)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue())
                .setExternalCertificate(extCertificates);


        RpcResult<Void> rpcResult =
                odlOfconfigVer12ApiServiceImpl.handleExtCert(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());

        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();

        List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.ExternalCertificate> extCertificateList =
                deviceCapableSwitch.getResources().getExternalCertificate();

        assertEquals(1, extCertificateList.size());

        assertEquals("resource_1", extCertificateList.get(0).getResourceId().getValue());


        NodeKey nodeKey = new NodeKey(netconfNodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        extCertificateList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getExternalCertificate();

        assertEquals(1, extCertificateList.size());

        assertEquals("resource_1", extCertificateList.get(0).getResourceId().getValue());

        // merge

        extCertificates = new ArrayList<>();

        extCertificateBuilder = new ExternalCertificateBuilder();

        extCertificateBuilder.setCertificate("extCertificate2")
                .setKey(new ExternalCertificateKey(Uri.getDefaultInstance("resource_2")))
                .setResourceId(Uri.getDefaultInstance("resource_2"));

        extCertificates.add(extCertificateBuilder.build());
        inputBuilder = new HandleExtCertInputBuilder();

        inputBuilder.setHandleMode(HandleMode.Merge)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue())
                .setExternalCertificate(extCertificates);

        rpcResult = odlOfconfigVer12ApiServiceImpl.handleExtCert(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());

        deviceCapableSwitch = this.capableSwitchRef.get();

        extCertificateList = deviceCapableSwitch.getResources().getExternalCertificate();

        assertEquals(2, extCertificateList.size());

        List<String> resourceStrings =
                newArrayList(extCertificateList.get(0).getResourceId().getValue(),
                        extCertificateList.get(1).getResourceId().getValue());

        assertTrue(resourceStrings.contains("resource_1"));
        assertTrue(resourceStrings.contains("resource_2"));

        // del

        extCertificates = new ArrayList<>();

        extCertificateBuilder = new ExternalCertificateBuilder();

        extCertificateBuilder.setResourceId(Uri.getDefaultInstance("resource_2"));

        extCertificates.add(extCertificateBuilder.build());
        inputBuilder = new HandleExtCertInputBuilder();

        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue())
                .setExternalCertificate(extCertificates);


        rpcResult = odlOfconfigVer12ApiServiceImpl.handleExtCert(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());

        deviceCapableSwitch = this.capableSwitchRef.get();

        extCertificateList = deviceCapableSwitch.getResources().getExternalCertificate();

        assertEquals(1, extCertificateList.size());

        assertEquals("resource_1", extCertificateList.get(0).getResourceId().getValue());


        nodeKey = new NodeKey(netconfNodeId);

        iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        extCertificateList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getExternalCertificate();

        assertEquals(1, extCertificateList.size());

        assertEquals("resource_1", extCertificateList.get(0).getResourceId().getValue());

    }

    @Test
    public void test_handle_logicSwitch() throws Exception {


        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(netconfNodeId);

        List<Switch> switchLists = newArrayList();

        SwitchBuilder swBuilder = new SwitchBuilder();

        swBuilder.setId(new OFConfigId("logicalSwitch1"))
                .setDatapathId(new DatapathIdType("00:00:7a:31:cd:91:04:40"));

        switchLists.add(swBuilder.build());

        HandleLogicSwitchInputBuilder inputBuilder = new HandleLogicSwitchInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Put)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setSwitch(switchLists);

        RpcResult<Void> rpcResult =
                odlOfconfigVer12ApiServiceImpl.handleLogicSwitch(inputBuilder.build()).get();



        assertTrue(rpcResult.isSuccessful());

        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();

        List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.Switch> switchList =
                deviceCapableSwitch.getLogicalSwitches().getSwitch();

        assertEquals(1, switchList.size());

        assertEquals("logicalSwitch1", switchList.get(0).getId().getValue());


        NodeKey nodeKey = new NodeKey(netconfNodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        switchList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getLogicalSwitches().getSwitch();

        assertEquals(1, switchList.size());

        assertEquals("logicalSwitch1", switchList.get(0).getId().getValue());

        // merge


        switchLists = newArrayList();

        swBuilder = new SwitchBuilder();

        swBuilder.setId(new OFConfigId("logicalSwitch2"))
                .setDatapathId(new DatapathIdType("00:00:7a:31:cd:92:04:40"));

        switchLists.add(swBuilder.build());

        inputBuilder = new HandleLogicSwitchInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Merge)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setSwitch(switchLists);

        rpcResult = odlOfconfigVer12ApiServiceImpl.handleLogicSwitch(inputBuilder.build()).get();



        assertTrue(rpcResult.isSuccessful());

        deviceCapableSwitch = this.capableSwitchRef.get();

        switchList = deviceCapableSwitch.getLogicalSwitches().getSwitch();

        assertEquals(2, switchList.size());


        List<String> logicalSwitchids = newArrayList(switchList.get(0).getId().getValue(),
                switchList.get(1).getId().getValue());

        assertTrue(logicalSwitchids.contains("logicalSwitch1"));
        assertTrue(logicalSwitchids.contains("logicalSwitch2"));


        nodeKey = new NodeKey(netconfNodeId);

        iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        switchList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getLogicalSwitches().getSwitch();

        assertEquals(2, switchList.size());


        logicalSwitchids = newArrayList(switchList.get(0).getId().getValue(),
                switchList.get(1).getId().getValue());

        assertTrue(logicalSwitchids.contains("logicalSwitch1"));
        assertTrue(logicalSwitchids.contains("logicalSwitch2"));

        // delete

        switchLists = newArrayList();

        swBuilder = new SwitchBuilder();

        swBuilder.setId(new OFConfigId("logicalSwitch2"));

        switchLists.add(swBuilder.build());

        inputBuilder = new HandleLogicSwitchInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setSwitch(switchLists);

        rpcResult = odlOfconfigVer12ApiServiceImpl.handleLogicSwitch(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();


        switchList = deviceCapableSwitch.getLogicalSwitches().getSwitch();

        assertEquals(1, switchList.size());

        assertEquals("logicalSwitch1", switchList.get(0).getId().getValue());



        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        switchList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getLogicalSwitches().getSwitch();

        assertEquals(1, switchList.size());

        assertEquals("logicalSwitch1", switchList.get(0).getId().getValue());



        switchLists = newArrayList();

        swBuilder = new SwitchBuilder();

        swBuilder.setDatapathId(new DatapathIdType("00:00:7a:31:cd:91:04:40"));

        switchLists.add(swBuilder.build());

        inputBuilder = new HandleLogicSwitchInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setSwitch(switchLists);

        rpcResult = odlOfconfigVer12ApiServiceImpl.handleLogicSwitch(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();


        switchList = deviceCapableSwitch.getLogicalSwitches().getSwitch();

        assertEquals(0, switchList.size());


        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        switchList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getLogicalSwitches().getSwitch();

        assertEquals(0, switchList.size());


    }

    @Test
    public void test_handle_port_resource() throws Exception {

        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(netconfNodeId);

        // put
        List<Port> ports = newArrayList();

        PortBuilder portBuilder = new PortBuilder();
        portBuilder.setKey(new PortKey("port_key1")).setName("port_key1");
        ports.add(portBuilder.build());

        HandlePortResourceInputBuilder inputBuilder = new HandlePortResourceInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Put)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setPort(ports);


        RpcResult<Void> rpcResult =
                odlOfconfigVer12ApiServiceImpl.handlePortResource(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();

        List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.Port> portList =
                deviceCapableSwitch.getResources().getPort();

        assertEquals(1, portList.size());

        assertEquals("port_key1", portList.get(0).getName());

        NodeKey nodeKey = new NodeKey(netconfNodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        portList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getPort();


        assertEquals(1, portList.size());

        assertEquals("port_key1", portList.get(0).getName());

        // merge


        ports = newArrayList();

        portBuilder = new PortBuilder();
        portBuilder.setKey(new PortKey("port_key2")).setName("port_key2");
        ports.add(portBuilder.build());

        inputBuilder = new HandlePortResourceInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Merge)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setPort(ports);


        rpcResult = odlOfconfigVer12ApiServiceImpl.handlePortResource(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();

        portList = deviceCapableSwitch.getResources().getPort();

        assertEquals(2, portList.size());

        List<String> portNames = newArrayList(portList.get(0).getName(), portList.get(1).getName());

        assertTrue(portNames.contains("port_key1"));
        assertTrue(portNames.contains("port_key2"));

        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        portList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getPort();

        assertEquals(2, portList.size());

        assertTrue(portNames.contains("port_key1"));
        assertTrue(portNames.contains("port_key2"));

        // delete

        ports = newArrayList();

        portBuilder = new PortBuilder();
        portBuilder.setName("port_key2");
        ports.add(portBuilder.build());

        inputBuilder = new HandlePortResourceInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setPort(ports);


        rpcResult = odlOfconfigVer12ApiServiceImpl.handlePortResource(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());

        deviceCapableSwitch = this.capableSwitchRef.get();

        portList = deviceCapableSwitch.getResources().getPort();

        assertEquals(1, portList.size());

        assertEquals("port_key1", portList.get(0).getName());

        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        portList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getPort();

        assertEquals(1, portList.size());

        assertEquals("port_key1", portList.get(0).getName());
    }

    @Test
    public void test_handle_queue_resource() throws Exception {

        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(netconfNodeId);

        // put

        List<Queue> queues = newArrayList();
        QueueBuilder queueBuilder = new QueueBuilder();
        queueBuilder.setKey(new QueueKey(Uri.getDefaultInstance("queue_1")))
                .setId(BigInteger.valueOf(1l)).setResourceId(Uri.getDefaultInstance("queue_1"));

        queues.add(queueBuilder.build());
        HandleQueueResourceInputBuilder inputBuilder = new HandleQueueResourceInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Put)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setQueue(queues);



        RpcResult<Void> rpcResult =
                odlOfconfigVer12ApiServiceImpl.handleQueueResource(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();

        List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.Queue> queueList =
                deviceCapableSwitch.getResources().getQueue();

        assertEquals(1, queueList.size());

        assertEquals(BigInteger.valueOf(1l), queueList.get(0).getId());

        NodeKey nodeKey = new NodeKey(netconfNodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        queueList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getQueue();


        assertEquals(1, queueList.size());

        assertEquals(BigInteger.valueOf(1l), queueList.get(0).getId());

        // merge


        queues = newArrayList();
        queueBuilder = new QueueBuilder();
        queueBuilder.setKey(new QueueKey(Uri.getDefaultInstance("queue_2")))
                .setId(BigInteger.valueOf(2l)).setResourceId(Uri.getDefaultInstance("queue_2"));

        queues.add(queueBuilder.build());
        inputBuilder = new HandleQueueResourceInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Merge)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setQueue(queues);



        rpcResult = odlOfconfigVer12ApiServiceImpl.handleQueueResource(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();

        queueList = deviceCapableSwitch.getResources().getQueue();

        assertEquals(2, queueList.size());

        List<BigInteger> ids = newArrayList(queueList.get(0).getId(), queueList.get(1).getId());

        assertTrue(ids.contains(BigInteger.valueOf(1l)));
        assertTrue(ids.contains(BigInteger.valueOf(2l)));



        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);

        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        queueList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getQueue();


        assertEquals(2, queueList.size());

        assertTrue(ids.contains(BigInteger.valueOf(1l)));
        assertTrue(ids.contains(BigInteger.valueOf(2l)));


        // delete

        queues = newArrayList();
        queueBuilder = new QueueBuilder();
        queueBuilder.setResourceId(Uri.getDefaultInstance("queue_2"));

        queues.add(queueBuilder.build());
        inputBuilder = new HandleQueueResourceInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setQueue(queues);



        rpcResult = odlOfconfigVer12ApiServiceImpl.handleQueueResource(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();

        queueList = deviceCapableSwitch.getResources().getQueue();

        assertEquals(1, queueList.size());

        assertEquals(BigInteger.valueOf(1l), queueList.get(0).getId());



        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        queueList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getQueue();

        assertEquals(1, queueList.size());

        assertEquals(BigInteger.valueOf(1l), queueList.get(0).getId());

        // delete 2

        queues = newArrayList();
        queueBuilder = new QueueBuilder();
        queueBuilder.setId(BigInteger.valueOf(1l));

        queues.add(queueBuilder.build());
        inputBuilder = new HandleQueueResourceInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setQueue(queues);



        rpcResult = odlOfconfigVer12ApiServiceImpl.handleQueueResource(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();

        queueList = deviceCapableSwitch.getResources().getQueue();

        assertEquals(0, queueList.size());



        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        queueList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getQueue();

        assertEquals(0, queueList.size());



    }

    @Test
    public void test_handle_tunnel() throws Exception {

        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo(netconfNodeId);

        // put
        List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_tunnel.Port> ports =
                newArrayList();

        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_tunnel.PortBuilder portBuilder =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_tunnel.PortBuilder();

        VxlanTunnelBuilder vxBuilder = new VxlanTunnelBuilder();

        org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.vxlan.tunnel.VxlanTunnelBuilder implBuilder =
                new org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.vxlan.tunnel.VxlanTunnelBuilder();

        implBuilder.setVni(1l);

        vxBuilder.setVxlanTunnel(implBuilder.build());

        portBuilder
                .setKey(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_tunnel.PortKey(
                        "port_1"))
                .setName("port_1").setTunnelType(vxBuilder.build());


        ports.add(portBuilder.build());

        HandleTunnelInputBuilder inputBuilder = new HandleTunnelInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Put)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setPort(ports);



        RpcResult<Void> rpcResult =
                odlOfconfigVer12ApiServiceImpl.handleTunnel(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());

        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();

        List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.Port> portList =
                deviceCapableSwitch.getResources().getPort();

        assertEquals(1, portList.size());

        assertEquals("port_1", portList.get(0).getName());
        assertTrue(portList.get(0)
                .getTunnelType() instanceof org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel);

        org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel vxLanTunnel =
                (org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel) portList
                        .get(0).getTunnelType();


        assertEquals(Long.valueOf(1l), vxLanTunnel.getVxlanTunnel().getVni());



        NodeKey nodeKey = new NodeKey(netconfNodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        portList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getPort();


        assertEquals(1, portList.size());

        assertEquals("port_1", portList.get(0).getName());
        assertTrue(portList.get(0)
                .getTunnelType() instanceof org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel);

        vxLanTunnel =
                (org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel) portList
                        .get(0).getTunnelType();


        assertEquals(Long.valueOf(1l), vxLanTunnel.getVxlanTunnel().getVni());

        // merge


        ports = newArrayList();

        portBuilder =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_tunnel.PortBuilder();

        vxBuilder = new VxlanTunnelBuilder();

        implBuilder =
                new org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.vxlan.tunnel.VxlanTunnelBuilder();

        implBuilder.setVni(2l);

        vxBuilder.setVxlanTunnel(implBuilder.build());

        portBuilder
                .setKey(new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_tunnel.PortKey(
                        "port_2"))
                .setName("port_2").setTunnelType(vxBuilder.build());


        ports.add(portBuilder.build());

        inputBuilder = new HandleTunnelInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Merge)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setPort(ports);



        rpcResult = odlOfconfigVer12ApiServiceImpl.handleTunnel(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());

        deviceCapableSwitch = this.capableSwitchRef.get();

        portList = deviceCapableSwitch.getResources().getPort();

        assertEquals(2, portList.size());


        List<String> portNames = newArrayList(portList.get(0).getName(), portList.get(1).getName());

        assertTrue(portNames.contains("port_1"));
        assertTrue(portNames.contains("port_2"));

        Long vni0 =
                ((org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel) portList
                        .get(0).getTunnelType()).getVxlanTunnel().getVni();

        Long vni1 =
                ((org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel) portList
                        .get(1).getTunnelType()).getVxlanTunnel().getVni();


        List<Long> vnis = newArrayList(vni0, vni1);

        assertTrue(vnis.contains(Long.valueOf(1l)));
        assertTrue(vnis.contains(Long.valueOf(2l)));



        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        portList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getPort();


        assertEquals(2, portList.size());

        vni0 = ((org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel) portList
                .get(0).getTunnelType()).getVxlanTunnel().getVni();

        vni1 = ((org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel) portList
                .get(1).getTunnelType()).getVxlanTunnel().getVni();


        vnis = newArrayList(vni0, vni1);

        assertTrue(vnis.contains(Long.valueOf(1l)));
        assertTrue(vnis.contains(Long.valueOf(2l)));


        // del

        ports = newArrayList();

        portBuilder =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_tunnel.PortBuilder();



        portBuilder.setName("port_2");


        ports.add(portBuilder.build());

        inputBuilder = new HandleTunnelInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Delete)
                .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setPort(ports);

        rpcResult = odlOfconfigVer12ApiServiceImpl.handleTunnel(inputBuilder.build()).get();

        assertTrue(rpcResult.isSuccessful());


        deviceCapableSwitch = this.capableSwitchRef.get();

        portList = deviceCapableSwitch.getResources().getPort();

        assertEquals(1, portList.size());

        assertEquals("port_1", portList.get(0).getName());
        assertTrue(portList.get(0)
                .getTunnelType() instanceof org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel);

        vxLanTunnel =
                (org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel) portList
                        .get(0).getTunnelType();


        assertEquals(Long.valueOf(1l), vxLanTunnel.getVxlanTunnel().getVni());



        node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, databroker);


        capableSwNode = node.getAugmentation(OfconfigCapableSwitchAugmentation.class);

        portList = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch()
                .getResources().getPort();


        assertEquals(1, portList.size());

        assertEquals("port_1", portList.get(0).getName());
        assertTrue(portList.get(0)
                .getTunnelType() instanceof org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel);

        vxLanTunnel =
                (org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofporttype.tunnel.type.VxlanTunnel) portList
                        .get(0).getTunnelType();


        assertEquals(Long.valueOf(1l), vxLanTunnel.getVxlanTunnel().getVni());



    }


}
