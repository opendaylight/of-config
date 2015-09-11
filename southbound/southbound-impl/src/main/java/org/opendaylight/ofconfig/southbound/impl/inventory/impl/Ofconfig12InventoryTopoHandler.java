package org.opendaylight.ofconfig.southbound.impl.inventory.impl;

import java.util.List;

import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.inventory.OfconfigInventoryTopoHandler;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.OfconfigCapableSwitchAttributesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.OfconfigLogicalSwitchAttributesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.types.rev150901.DatapathIdType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.types.rev150901.OfConfigTypeVersion12;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig12.rev150901.capable._switch.node.attributes.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig12.rev150901.of.config.logical._switch.attributes.LogicalSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class Ofconfig12InventoryTopoHandler extends OfconfigInventoryTopoHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Ofconfig12InventoryTopoHandler.class);
    
    @Override
    protected Node addCapableSwitchNodeAttributes(NodeId netconfNodeId,ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {
       
        Optional<CapableSwitch> capableSwitchConfig = getCapableSwitchCinfigureFromOfDevice(netconfNodeId, ofconfigNodeReadTx);
        if(capableSwitchConfig.isPresent()){
            
            String nodeString = netconfNodeId.getValue() + ":"
                    + capableSwitchConfig.get().getId();
   
            NodeId nodeId = new NodeId(new Uri(nodeString));
            NodeKey nodeKey = new NodeKey(nodeId);
            InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                    .child(Topology.class,new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                    .child(Node.class,nodeKey)
                    .build();
            //remove old ofconfig-node
            
            //invTopoWriteTx.delete(LogicalDatastoreType.OPERATIONAL, iid);
            
            
            OfconfigCapableSwitchAugmentationBuilder ofconfigNodeBuilder= new OfconfigCapableSwitchAugmentationBuilder();
            
            
            
            OfconfigCapableSwitchAttributesBuilder attributesBuilder = new OfconfigCapableSwitchAttributesBuilder();
            
            CapableSwitchBuilder capableSwitchBuilder = new CapableSwitchBuilder();
            
            capableSwitchBuilder.setConfigVersion(capableSwitchConfig.get().getConfigVersion());
            capableSwitchBuilder.setId(capableSwitchConfig.get().getId());
            capableSwitchBuilder.setLogicalSwitches(capableSwitchConfig.get().getLogicalSwitches());
            capableSwitchBuilder.setResources(capableSwitchConfig.get().getResources());
            
            attributesBuilder.setCapableSwitch(capableSwitchBuilder.build());
            attributesBuilder.setOfconfigVersion(OfConfigTypeVersion12.class).setNetconfTopologyNodeId(netconfNodeId.getValue());
         
            ofconfigNodeBuilder.setOfconfigCapableSwitchAttributes(attributesBuilder.build());
            
            NodeBuilder nodeBuilder = new NodeBuilder();
            nodeBuilder.setNodeId(nodeId);
            nodeBuilder.setKey(nodeKey);
            nodeBuilder.addAugmentation(OfconfigCapableSwitchAugmentation.class,
                    ofconfigNodeBuilder.build());
            
            Node ofconfigNode = nodeBuilder.build();
            invTopoWriteTx.put(LogicalDatastoreType.OPERATIONAL, iid, ofconfigNode);
            
            return ofconfigNode;
        }else{
            
            throw new IllegalStateException(
                    "Unexpected error reading data from " + netconfNodeId);
            
        }
    }
    
    @Override
    protected void addLogicalSwitchNodeAttributes(NodeId netconfNodeId,ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {
        
        Optional<CapableSwitch> capableSwitchConfig = getCapableSwitchCinfigureFromOfDevice(netconfNodeId, ofconfigNodeReadTx);
        if(capableSwitchConfig.isPresent()){
            
            String nodeStringprefix = netconfNodeId.getValue() + ":"
                    + capableSwitchConfig.get().getId();
            
            List<Switch> swList =null;
            try{
                swList = capableSwitchConfig.get().getLogicalSwitches().getSwitch();
            }catch(Exception e){
                return;
            }
            
            for(Switch sw:swList){
                
                String nodeString = nodeStringprefix+":"+sw.getId().getValue();
                NodeId nodeId = new NodeId(new Uri(nodeString));
                NodeKey nodeKey = new NodeKey(nodeId);
                InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                        .child(Topology.class,new TopologyKey(OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID))
                        .child(Node.class,nodeKey)
                        .build();
                
               // invTopoWriteTx.delete(LogicalDatastoreType.OPERATIONAL, iid);
                
                
                OfconfigLogicalSwitchAugmentationBuilder logicSwitchBuilder = new OfconfigLogicalSwitchAugmentationBuilder();
                
                OfconfigLogicalSwitchAttributesBuilder attrBuilder = new OfconfigLogicalSwitchAttributesBuilder();
                
                LogicalSwitchBuilder logicSwBuilder = new LogicalSwitchBuilder();
                logicSwBuilder.setCapabilities(sw.getCapabilities()).setControllers(sw.getControllers())
                .setDatapathId(sw.getDatapathId()).setId(sw.getId()).setLostConnectionBehavior(sw.getLostConnectionBehavior())
                .setResources(sw.getResources());
                
                
                
                attrBuilder.setCapableSwitchId(capableSwitchConfig.get().getId())
                    .setDatapathId(new DatapathIdType(sw.getDatapathId().getValue())).setLogicalSwitch(logicSwBuilder.build())
                    .setNetconfTopologyNodeId(netconfNodeId.getValue()).setOfconfigVersion(OfConfigTypeVersion12.class);
                
               
                logicSwitchBuilder.setOfconfigLogicalSwitchAttributes(attrBuilder.build());
                
                
                NodeBuilder nodeBuilder = new NodeBuilder();
                nodeBuilder.setNodeId(nodeId);
                nodeBuilder.setKey(nodeKey);
                nodeBuilder.addAugmentation(OfconfigLogicalSwitchAugmentation.class,
                        logicSwitchBuilder.build());
                
                Node ofconfigNode = nodeBuilder.build();
                invTopoWriteTx.put(LogicalDatastoreType.OPERATIONAL, iid, ofconfigNode);
                
            }
            
            
            
        }
        
        
        
       

    }
    

    private Optional<CapableSwitch> getCapableSwitchCinfigureFromOfDevice(NodeId netconfNodeId,
            ReadOnlyTransaction ofconfigNodeReadTx) {
        InstanceIdentifier<CapableSwitch> iid = InstanceIdentifier
                .create(CapableSwitch.class);

        Optional<CapableSwitch> capableSwitchConfig;

        try {

            capableSwitchConfig = ofconfigNodeReadTx.read(
                    LogicalDatastoreType.CONFIGURATION, iid).checkedGet();
            return capableSwitchConfig;
        } catch (ReadFailedException e) {
            throw new IllegalStateException(
                    "Unexpected error reading data from " + netconfNodeId,
                    e);
        }
        
    }

    

}
