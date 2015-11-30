package org.opendaylight.ofconfig.southbound.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12.CapableSwitchTopoNodeAddHelper;
import org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12.LogicalSwitchTopoNodeAddHelper;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddressBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.PortNumber;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.DatapathIdType;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.OFConfigId;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.OFControllerType.Protocol;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.LogicalSwitchesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.SwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.SwitchKey;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.ControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.Controller;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.ControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.ControllerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.netconf.node.connection.status.AvailableCapabilitiesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.QueryLogicalSwitchNodeIdInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.QueryLogicalSwitchNodeIdOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.api.rev150901.SyncCapcableSwitchInputBuilder;
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
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CheckedFuture;

public class OdlOfconfigApiServiceImplTest extends OFconfigTestBase{
    
    
    private NodeId netconfNodeId = new NodeId("test-netconf-node");
    
    private OdlOfconfigApiServiceImpl odlOfconfigApiServiceImpl = null;

    @Before
    public void setUp(){
        super.setUp();
        
        ProviderContext providerContext=mock(ProviderContext.class);
        when(providerContext.getSALService(DataBroker.class)).thenReturn(this.databroker);
        initMountService();
        when(providerContext.getSALService(MountPointService.class)).thenReturn(this.mountService);
        
        odlOfconfigApiServiceImpl= new OdlOfconfigApiServiceImpl();
        odlOfconfigApiServiceImpl.onSessionInitiated(providerContext);
        
        initNetConfTopo();
        initOfConfigCapableSwitchTopo();
        initOfConfigLogicalSwitchTopo();
        
    }

   

   



    @After
    public void tearDown() throws Exception {}

    @Test
    public void test_sync_capcable_switch() {
        
        
        SyncCapcableSwitchInputBuilder builder = new SyncCapcableSwitchInputBuilder();
        builder.setNodeId("test-netconf-node");
        
        RpcResult result=null;
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
        
        
        String nodeStringprefix =
                netconfNodeId.getValue() + ":" + "ofconf-device"+":"+"test_sw";
        
        NodeId logicaSwNodeId = new NodeId(nodeStringprefix);
        NodeKey logicalNodeKey = new NodeKey(logicaSwNodeId);
        
        InstanceIdentifier<Node> logicaliid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID))
                .child(Node.class, logicalNodeKey).build();
        
        
        Node logicaLnode =  mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, logicaliid, databroker); 
        
        OfconfigLogicalSwitchAugmentation logicSwitchNode =  logicaLnode.getAugmentation(OfconfigLogicalSwitchAugmentation.class);
        
        
        assertEquals("test_ctl_new",logicSwitchNode.getOfconfigLogicalSwitchAttributes().getLogicalSwitch().getControllers().getController().get(0).getId().getValue());
        

        
    }
    
    @Test
    public void test_query_capcable_switch_nodeId_by_dpId() {
        
        
        QueryLogicalSwitchNodeIdInputBuilder builder = new QueryLogicalSwitchNodeIdInputBuilder();
        builder.setDatapathId("00:00:7a:31:cd:91:04:40");
        
        
        try {
            RpcResult<QueryLogicalSwitchNodeIdOutput> result=  odlOfconfigApiServiceImpl.queryLogicalSwitchNodeId(builder.build()).get();
            
            assertTrue(result.isSuccessful());
            
            String nodeId =
                    netconfNodeId.getValue() + ":" + "ofconf-device:test_sw";
            
            
            assertEquals(nodeId, result.getResult().getNodeId());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } 
        
        
        
    }
    
    
    
    
    
    private void initMountService() {
        try {

            CapableSwitchBuilder capableSwitchBuilder = new CapableSwitchBuilder();
            capableSwitchBuilder.setConfigVersion("1.4").setId("ofconf-device");
            
            ControllersBuilder ctlllerBuilder= new ControllersBuilder();
            
            List<Controller> ctllers = new ArrayList<>();
            
            ControllerBuilder builder = new ControllerBuilder();
            
            
            
            builder.setId(new OFConfigId("test_ctl_new")).
                setKey(new ControllerKey(new OFConfigId("test_ctl_new"))).
                setProtocol(Protocol.Tcp).setIpAddress(IpAddressBuilder.getDefaultInstance("127.0.0.1")).setPort(PortNumber.getDefaultInstance("6630"));
            
            ctllers.add(builder.build());
            
            ctlllerBuilder.setController(ctllers);
            
            
            LogicalSwitchesBuilder lsBuilder = new LogicalSwitchesBuilder();
            
            List<Switch> swlists = Lists.newArrayList();
            
            SwitchBuilder switchBuilder = new SwitchBuilder();
            switchBuilder.setId(new OFConfigId("test_sw")).setKey(new SwitchKey(new OFConfigId("test_sw"))).setControllers(ctlllerBuilder.build()).setDatapathId(new DatapathIdType("00:00:7a:31:cd:91:04:40"));
            
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
            
            
            
        }catch(Exception e){
            throw new RuntimeException(e);
        }
        
    }
    
 private void initNetConfTopo() {
        
        InstanceIdentifier<Topology> path =
                InstanceIdentifier.create(NetworkTopology.class)
                        .child(Topology.class,
                                new TopologyKey(
                                        new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)));
        
        
        NetconfNodeBuilder netconfNodeBuilder = new NetconfNodeBuilder();
        
        List<String> availableCapabilities = Lists.newArrayList();
        
        availableCapabilities.add(OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY);
        
        
        AvailableCapabilitiesBuilder availableCapabilitiesBuilder = new AvailableCapabilitiesBuilder();
        availableCapabilitiesBuilder.setAvailableCapability(availableCapabilities);
        
        netconfNodeBuilder.setAvailableCapabilities(availableCapabilitiesBuilder.build()).setKeepaliveDelay(100l);
        
        NodeBuilder nodeBuilder = new NodeBuilder();
        nodeBuilder.setKey(new NodeKey(netconfNodeId))
        .setNodeId(netconfNodeId).addAugmentation(NetconfNode.class, netconfNodeBuilder.build());
        
       
        
        
        TopologyBuilder topoBuilder = new TopologyBuilder();
        topoBuilder.setKey(new TopologyKey(new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)))
        .setTopologyId(new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)).setNode(Lists.newArrayList(nodeBuilder.build()));
        
        
        mdsalUtils.put(LogicalDatastoreType.OPERATIONAL, path, topoBuilder.build(), databroker);
    }
    
    
    private void initOfConfigCapableSwitchTopo() {
        
        CapableSwitchTopoNodeAddHelper capableSwitchTopoNodeAddHelper =new CapableSwitchTopoNodeAddHelper();
        
        CapableSwitchBuilder cswBuilder = new CapableSwitchBuilder();
        
        LogicalSwitchesBuilder lswBuilder = new LogicalSwitchesBuilder();
        
        List<Switch> swlist = new ArrayList<>();
        SwitchBuilder swBuilder = new SwitchBuilder();
        
        ControllersBuilder ctlllerBuilder= new ControllersBuilder();
        
        List<Controller> ctllers = new ArrayList<>();
        
        ControllerBuilder builder = new ControllerBuilder();
        
        
        
        builder.setId(new OFConfigId("test_ctl")).
            setKey(new ControllerKey(new OFConfigId("test_ctl"))).
            setProtocol(Protocol.Tcp).setIpAddress(IpAddressBuilder.getDefaultInstance("127.0.0.1")).setPort(PortNumber.getDefaultInstance("6630"));
        
        ctllers.add(builder.build());
        
        ctlllerBuilder.setController(ctllers);
        
        swBuilder.setId(new OFConfigId("test_sw")).setKey(new SwitchKey(new OFConfigId("test_sw"))).setControllers(ctlllerBuilder.build());
        
        swlist.add(swBuilder.build());
        
        lswBuilder.setSwitch(swlist);
        
        
        cswBuilder.setId("test_capableSwitch").setConfigVersion("12").setLogicalSwitches(lswBuilder.build());
        
        
        ReadWriteTransaction  tx= databroker.newReadWriteTransaction();
        capableSwitchTopoNodeAddHelper.addCapableSwitchTopoNodeAttributes(netconfNodeId, Optional.of(cswBuilder.build()), tx);
        
        try {
            tx.submit().checkedGet();
        } catch (TransactionCommitFailedException e1) {
           throw new RuntimeException(e1);
        }
    }
    
    private void initOfConfigLogicalSwitchTopo() {
        
        LogicalSwitchTopoNodeAddHelper logicalSwitchTopoNodeAddHelper = new LogicalSwitchTopoNodeAddHelper();
        
        NodeId netconfNodeId = new NodeId("test_switch");
        
        CapableSwitchBuilder cswBuilder = new CapableSwitchBuilder();
        
        LogicalSwitchesBuilder lswBuilder = new LogicalSwitchesBuilder();
        
        List<Switch> swlist = new ArrayList<>();
        SwitchBuilder swBuilder = new SwitchBuilder();
        
        ControllersBuilder ctlllerBuilder= new ControllersBuilder();
        
        List<Controller> ctllers = new ArrayList<>();
        
        ControllerBuilder builder = new ControllerBuilder();
        
        
        
        builder.setId(new OFConfigId("test_ctl")).
            setKey(new ControllerKey(new OFConfigId("test_ctl"))).
            setProtocol(Protocol.Tcp).setIpAddress(IpAddressBuilder.getDefaultInstance("127.0.0.1")).setPort(PortNumber.getDefaultInstance("6630"));
        
        ctllers.add(builder.build());
        
        ctlllerBuilder.setController(ctllers);
        
        swBuilder.setId(new OFConfigId("test_sw")).setKey(new SwitchKey(new OFConfigId("test_sw"))).setControllers(ctlllerBuilder.build()).setDatapathId(new DatapathIdType("00:00:7a:31:cd:91:04:40"));
        
        swlist.add(swBuilder.build());
        
        lswBuilder.setSwitch(swlist);
        
        
        cswBuilder.setId("test_capableSwitch").setConfigVersion("12").setLogicalSwitches(lswBuilder.build());
        
        
        ReadWriteTransaction  tx= databroker.newReadWriteTransaction();
        
        CapableSwitch cpsw =  cswBuilder.build();
        
        logicalSwitchTopoNodeAddHelper.addLogicalSwitchTopoNodeAttributes(netconfNodeId, Optional.of(cpsw), tx);
        
        
        try {
            tx.submit().checkedGet();
        } catch (TransactionCommitFailedException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        }
        
    }

}
