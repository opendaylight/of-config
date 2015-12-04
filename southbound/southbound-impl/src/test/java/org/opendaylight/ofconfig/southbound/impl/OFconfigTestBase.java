/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.controller.sal.binding.test.AbstractDataServiceTest;
import org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12.CapableSwitchTopoNodeAddHelper;
import org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12.LogicalSwitchTopoNodeAddHelper;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.ofconfig.southbound.impl.utils.OfconfigHelper;
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

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CheckedFuture;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public abstract class OFconfigTestBase extends AbstractDataServiceTest{
    
    
private OfconfigSouthboundImpl ofconfigSouthboundImpl;
    
    protected DataBroker  databroker;
    
    protected MdsalUtils mdsalUtils;
    
    protected MountPointService mountService;
    
    protected OfconfigHelper ofconfigHelper =null;
    
    protected AtomicReference<CapableSwitch> capableSwitchRef;
    
   @Before
    public void setUp() {
        super.setUp();
         databroker =  this.testContext.getDataBroker();
        mountService =mock(MountPointService.class);
        
        ofconfigSouthboundImpl = new OfconfigSouthboundImpl();
        
        ofconfigSouthboundImpl.setDataBroker(databroker);
        ofconfigSouthboundImpl.setMountService(mountService);
        
        ofconfigSouthboundImpl.initTopoAndListener();
        
        mdsalUtils = new MdsalUtils();
        
        ofconfigHelper = new OfconfigHelper(mountService, databroker);
        
        capableSwitchRef = new AtomicReference<>();
        
    }
   
   
   
   protected void initNetConfTopo(NodeId netconfNodeId) {
       
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

   
   protected void initDataStore(NodeId netconfNodeId) {
       
       try {
           ofconfigHelper.destroyOfconfigNode(netconfNodeId);
       } catch (Exception e) {
           fail(e.getMessage());
       }
   }
   
   
   protected void initOfConfigCapableSwitchTopo(NodeId netconfNodeId) {
       
       
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
   
   
   protected void initOfConfigLogicalSwitchTopo(NodeId netconfNodeId) {
       
       LogicalSwitchTopoNodeAddHelper logicalSwitchTopoNodeAddHelper = new LogicalSwitchTopoNodeAddHelper();
       
       
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
   
   protected void initMountService(NodeId netconfNodeId) {
       try {

           final CapableSwitchBuilder capableSwitchBuilder = new CapableSwitchBuilder();
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
           
           Answer<Optional<CapableSwitch>> capableSwitchAnswer = new Answer<Optional<CapableSwitch>>(){

            @Override
            public Optional<CapableSwitch> answer(InvocationOnMock invocation) throws Throwable {
                return Optional.of(capableSwitchRef.get()==null?capableSwitchBuilder.build():capableSwitchRef.get());
            }
               
           };
           
           
           when(resultFuture.checkedGet()).thenAnswer(capableSwitchAnswer);
           
           when(resultFuture.get()).thenAnswer(capableSwitchAnswer);
      
           InstanceIdentifier<CapableSwitch> iid = InstanceIdentifier.create(CapableSwitch.class);

           ReadOnlyTransaction rtx = mock(ReadOnlyTransaction.class);
           when(rtx.read(LogicalDatastoreType.CONFIGURATION, iid)).thenReturn(resultFuture);
           
           
           WriteTransaction  wtx = mock(WriteTransaction.class);

           
           Mockito.doAnswer(new WriteTransactionAnswer(capableSwitchRef)).when(wtx).put(Matchers.eq(LogicalDatastoreType.CONFIGURATION),Matchers.eq(iid),Matchers.any(CapableSwitch.class),Matchers.eq(true));
           
           CheckedFuture checkedFuture = mock(CheckedFuture.class);
           
           when(wtx.submit()).thenReturn(checkedFuture);
           
           when(checkedFuture.checkedGet()).thenReturn(null);
           
           DataBroker mountDataBroker = mock(DataBroker.class);
           when(mountDataBroker.newReadOnlyTransaction()).thenReturn(rtx);
           
           when(mountDataBroker.newWriteOnlyTransaction()).thenReturn(wtx);
           
           

           MountPoint mountPoint = mock(MountPoint.class);
           when(mountPoint.getService(DataBroker.class)).thenReturn(Optional.of(mountDataBroker));



           when(mountService.getMountPoint(OfconfigConstants.NETCONF_TOPO_IID.child(Node.class,
                   new NodeKey(new NodeId(netconfNodeId))))).thenReturn(Optional.of(mountPoint));
           
           
           
       }catch(Exception e){
           throw new RuntimeException(e);
       }
       
   }
   
   
}
