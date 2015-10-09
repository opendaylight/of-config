package org.opendaylight.ofconfig.southbound.impl.listener;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.controller.sal.binding.test.AbstractDataServiceTest;
import org.opendaylight.ofconfig.southbound.impl.OfconfigSouthboundImpl;
import org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12.CapableSwitchTopoNodeAddHelper;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpAddressBuilder;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.PortNumber;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitchBuilder;
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
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;

import com.google.common.base.Optional;

public class OfconfigCapableSwitchTopoDataChangeListenerTest extends AbstractDataServiceTest {

    private OfconfigSouthboundImpl ofconfigSouthboundImpl;
    
    private DataBroker  databroker;
    
    @Before
    public void setUp() {
        super.setUp();
         databroker =  this.testContext.getDataBroker();
         MountPointService mountService =mock(MountPointService.class);
        
        ofconfigSouthboundImpl = new OfconfigSouthboundImpl();
        
        ofconfigSouthboundImpl.setDataBroker(databroker);
        ofconfigSouthboundImpl.setMountService(mountService);
        
        ofconfigSouthboundImpl.initTopoAndListener();
        
        test_create_capableSwitch_Topo_node();
    }



    //@Test
    public void test_create_capableSwitch_Topo_node() {
       
        CapableSwitchTopoNodeAddHelper capableSwitchTopoNodeAddHelper =new CapableSwitchTopoNodeAddHelper();
        
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
        
        swBuilder.setId(new OFConfigId("test_sw")).setKey(new SwitchKey(new OFConfigId("test_sw"))).setControllers(ctlllerBuilder.build());
        
        swlist.add(swBuilder.build());
        
        lswBuilder.setSwitch(swlist);
        
        
        cswBuilder.setId("test_capableSwitch").setConfigVersion("12").setLogicalSwitches(lswBuilder.build());
        
        
        ReadWriteTransaction  tx= databroker.newReadWriteTransaction();
        capableSwitchTopoNodeAddHelper.addCapableSwitchTopoNodeAttributes(netconfNodeId, Optional.of(cswBuilder.build()), tx);
        
        try {
            tx.submit().checkedGet();
        } catch (TransactionCommitFailedException e1) {
            e1.printStackTrace();
        }
        
        
    }
    
    
   // @Test
    public void test_update_capableSwitch_Topo_node() {
        
        CapableSwitchTopoNodeAddHelper capableSwitchTopoNodeAddHelper =new CapableSwitchTopoNodeAddHelper();
        
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
            setProtocol(Protocol.Tcp).setIpAddress(IpAddressBuilder.getDefaultInstance("127.0.0.2")).setPort(PortNumber.getDefaultInstance("6630"));
        
        ctllers.add(builder.build());
        
        ctlllerBuilder.setController(ctllers);
        
        swBuilder.setId(new OFConfigId("test_sw")).setKey(new SwitchKey(new OFConfigId("test_sw"))).setControllers(ctlllerBuilder.build());
        
        swlist.add(swBuilder.build());
        
        lswBuilder.setSwitch(swlist);
        
        
        cswBuilder.setId("test_capableSwitch").setConfigVersion("12").setLogicalSwitches(lswBuilder.build());
        
        
        CapableSwitchTopoNodeUpdateHelper helper = new CapableSwitchTopoNodeUpdateHelper();
        
     
        ReadWriteTransaction  tx= databroker.newReadWriteTransaction();
        helper.updateCapableSwitchTopoNodeAttributes(netconfNodeId, Optional.of(cswBuilder.build()), tx);
        
        try {
            tx.submit().checkedGet();
        } catch (TransactionCommitFailedException e1) {
            e1.printStackTrace();
        }
        
        
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
   @Test
    public void test_delete_capableSwitch_Topo_node() {
        
CapableSwitchTopoNodeAddHelper capableSwitchTopoNodeAddHelper =new CapableSwitchTopoNodeAddHelper();
        
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
            setProtocol(Protocol.Tcp).setIpAddress(IpAddressBuilder.getDefaultInstance("127.0.0.2")).setPort(PortNumber.getDefaultInstance("6630"));
        
        ctllers.add(builder.build());
        
        ctlllerBuilder.setController(ctllers);
        
        swBuilder.setId(new OFConfigId("test_sw")).setKey(new SwitchKey(new OFConfigId("test_sw"))).setControllers(ctlllerBuilder.build());
        
        swlist.add(swBuilder.build());
        
        lswBuilder.setSwitch(swlist);
        
        
        cswBuilder.setId("test_capableSwitch").setConfigVersion("12").setLogicalSwitches(lswBuilder.build());
        
        WriteTransaction  tx= databroker.newWriteOnlyTransaction();
        
        CapableSwitchTopoNodeDeleteHelper helper = new CapableSwitchTopoNodeDeleteHelper();
        
        helper.deleteCapableSwitchTopoNodeAttributes(netconfNodeId, Optional.of(cswBuilder.build()), tx);
        
        try {
            tx.submit().checkedGet();
        } catch (TransactionCommitFailedException e1) {
            e1.printStackTrace();
        }
        
        
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
