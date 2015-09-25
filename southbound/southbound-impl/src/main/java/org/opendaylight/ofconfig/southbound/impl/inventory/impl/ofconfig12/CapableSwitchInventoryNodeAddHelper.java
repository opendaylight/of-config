/*
 * Copyright (c) 2015 ZTE, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.inventory.impl.ofconfig12;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.odl.ofconfig.node.inventory.rev150917.OfconfigCapableSwitchNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.odl.ofconfig.node.inventory.rev150917.OfconfigCapableSwitchNodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.types.rev150901.OfConfigTypeVersion12;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class CapableSwitchInventoryNodeAddHelper {


    protected void addCapableSwitchInventoryNodeAttributes(org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId netconfNodeId,NetconfNode netconfNode,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {

        String nodeString = netconfNodeId.getValue() /*+ ":" + capableSwitchConfig.get().getId()*/;
        
        NodeId nodeId = new NodeId(new Uri(nodeString));
        NodeKey nodeKey = new NodeKey(nodeId);

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(Nodes.class)
                .child(Node.class, nodeKey).build();
        
        
        OfconfigCapableSwitchNodeBuilder capableSwNodeBuilder = new OfconfigCapableSwitchNodeBuilder();
        
        String logicalSwitchnodeStringPrefix = nodeString+":"+ capableSwitchConfig.get().getId();
        
        List<NodeId> managedNodeIds = buildeManagedNodeIds(capableSwitchConfig,logicalSwitchnodeStringPrefix);
        
        OfconfigCapableSwitchNode ofcsNode =   capableSwNodeBuilder.setOfconfigVersion(OfConfigTypeVersion12.class).
            setIpAddress(netconfNode.getHost().getIpAddress()).setPort(netconfNode.getPort()).setManagedNodes(managedNodeIds).build();
        
        
        
        NodeBuilder nodeBuilder = new NodeBuilder();
        nodeBuilder.setId(nodeId);
        nodeBuilder.setKey(nodeKey);
        nodeBuilder.addAugmentation(OfconfigCapableSwitchNode.class,
                capableSwNodeBuilder.build());

        Node ofcsInvNode = nodeBuilder.build();
        invTopoWriteTx.put(LogicalDatastoreType.OPERATIONAL, iid, ofcsInvNode);
        


    }

    private List<NodeId> buildeManagedNodeIds(Optional<CapableSwitch> capableSwitchConfig,final String nodeIdPrefix) {
       
        List<NodeId> resultNodeIds = new ArrayList<>();
        
        if(capableSwitchConfig.get().getLogicalSwitches()!=null){
           List<Switch> swList =  capableSwitchConfig.get().getLogicalSwitches().getSwitch();
           if(swList!=null){
               
               resultNodeIds = Lists.transform(swList, new Function<Switch, NodeId>() {
                   @Override
                   public NodeId apply(Switch logicswitch) {
                       return new NodeId(nodeIdPrefix+logicswitch.getId().getValue());
                   }

               });
           }
           
        }
        return resultNodeIds;
    }



}
