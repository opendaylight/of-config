package org.opendaylight.ofconfig.southbound.impl.inventory.impl;

import java.util.ArrayList;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigSwitchAugmentationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.LogicalSwitch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.LogicalSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.LogicalSwitchKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.OfconfigCapableSwitchAttributesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.logical._switch.OfconfigLogicalSwitchAttributes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.logical._switch.OfconfigLogicalSwitchAttributesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.types.rev150901.DatapathIdType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.types.rev150901.OfConfigTypeVersion12;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig12.rev150901.capable._switch.node.attributes.CapableSwitchBuilder;
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
            
            String nodeString = netconfNodeId.getValue() + "_"
                    + capableSwitchConfig.get().getId();
   
            NodeId nodeId = new NodeId(new Uri(nodeString));
            NodeKey nodeKey = new NodeKey(nodeId);
            InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                    .child(Topology.class,new TopologyKey(OfconfigConstants.OFCONFIG_TOPOLOGY_ID))
                    .child(Node.class,nodeKey)
                    .build();
            //remove old ofconfig-node
            
            invTopoWriteTx.delete(LogicalDatastoreType.OPERATIONAL, iid);
            
            OfconfigSwitchAugmentationBuilder ofconfigNodeBuilder = new OfconfigSwitchAugmentationBuilder();
            
            OfconfigCapableSwitchAttributesBuilder attributesBuilder = new OfconfigCapableSwitchAttributesBuilder();
            
            CapableSwitchBuilder capableSwitchBuilder = new CapableSwitchBuilder();
            
            capableSwitchBuilder.setConfigVersion(capableSwitchConfig.get().getConfigVersion());
            capableSwitchBuilder.setId(capableSwitchConfig.get().getId());
            capableSwitchBuilder.setLogicalSwitches(capableSwitchConfig.get().getLogicalSwitches());
            capableSwitchBuilder.setResources(capableSwitchConfig.get().getResources());
            
            attributesBuilder.setCapableSwitch(capableSwitchBuilder.build());
            
            List<LogicalSwitch> logicalSwitchList = new ArrayList<>();
            if(capableSwitchConfig.get().getLogicalSwitches()!=null){
                for(Switch logicalswitch:capableSwitchConfig.get().getLogicalSwitches().getSwitch()){
                    LogicalSwitchBuilder logicalSwitchBuilder = new LogicalSwitchBuilder();
                    
                    OfconfigLogicalSwitchAttributesBuilder attrBuilder = new OfconfigLogicalSwitchAttributesBuilder();
                    
                    org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig12.rev150901.of.config.logical._switch.attributes.LogicalSwitchBuilder swBuilder = 
                            new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig12.rev150901.of.config.logical._switch.attributes.LogicalSwitchBuilder();
                    
                    swBuilder.setCapabilities(logicalswitch.getCapabilities());
                    swBuilder.setControllers(logicalswitch.getControllers());
                    swBuilder.setDatapathId(logicalswitch.getDatapathId());
                    swBuilder.setId(logicalswitch.getId());
                    swBuilder.setLostConnectionBehavior(logicalswitch.getLostConnectionBehavior());
                    swBuilder.setResources(logicalswitch.getResources());
                    
                    attrBuilder.setLogicalSwitch(swBuilder.build());
                    
                    logicalSwitchBuilder.setDatapathId(new DatapathIdType(logicalswitch.getDatapathId().toString())).
                    setKey(new LogicalSwitchKey(new DatapathIdType(logicalswitch.getDatapathId().toString()))).setOfconfigLogicalSwitchAttributes(attrBuilder.build());
                    
                    logicalSwitchList.add(logicalSwitchBuilder.build());
                }
            }
            
            
        
            
            
            ofconfigNodeBuilder.setOfconfigVersion(OfConfigTypeVersion12.class).
                setOfconfigCapableSwitchAttributes(attributesBuilder.build()).setLogicalSwitch(logicalSwitchList);
            
            NodeBuilder nodeBuilder = new NodeBuilder();
            nodeBuilder.setNodeId(nodeId);
            nodeBuilder.setKey(nodeKey);
            nodeBuilder.addAugmentation(OfconfigSwitchAugmentation.class,
                    ofconfigNodeBuilder.build());
            
            invTopoWriteTx.put(LogicalDatastoreType.OPERATIONAL, iid, nodeBuilder.build());
        }else{
            
            throw new IllegalStateException(
                    "Unexpected error reading data from " + netconfNodeId);
            
        }
        
        
        
        
        return null;
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

    @Override
    protected void addLogicalSwitchNodeAttributes(Node ofconfigNode,
            WriteTransaction invTopoWriteTx) {
        // TODO Auto-generated method stub

    }

}
