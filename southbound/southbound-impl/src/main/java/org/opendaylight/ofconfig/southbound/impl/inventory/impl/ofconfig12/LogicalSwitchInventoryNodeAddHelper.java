/*
 * Copyright (c) 2015 ZTE, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.inventory.impl.ofconfig12;

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
import org.opendaylight.yang.gen.v1.urn.opendaylight.odl.ofconfig.node.inventory.rev150917.OfconfigLogicalSwitchNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.odl.ofconfig.node.inventory.rev150917.OfconfigLogicalSwitchNodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.odl.ofconfig.node.inventory.rev150917.nodes.node.OfconfigLogicalSwitchBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import com.google.common.base.Optional;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class LogicalSwitchInventoryNodeAddHelper {


    protected void addLogicalSwitchInventoryNodeAttributes(
            org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {

        String managedByofcSwNodeId =
                netconfNodeId.getValue() /*+ ":" + capableSwitchConfig.get().getId()*/;

        String nodeStringPrefix = managedByofcSwNodeId+ ":" + capableSwitchConfig.get().getId() + ":";

        if (capableSwitchConfig.get().getLogicalSwitches() != null) {
            List<Switch> swList = capableSwitchConfig.get().getLogicalSwitches().getSwitch();
            if (swList != null) {

                for (Switch logicSwitch : swList) {

                    NodeId nodeId =
                            new NodeId(new Uri(nodeStringPrefix + logicSwitch.getId().getValue()));
                    NodeKey nodeKey = new NodeKey(nodeId);

                    InstanceIdentifier<Node> iid = InstanceIdentifier.builder(Nodes.class)
                            .child(Node.class, nodeKey).build();

                    OfconfigLogicalSwitchNodeBuilder oflsNodeBuilder =
                            new OfconfigLogicalSwitchNodeBuilder();

                    OfconfigLogicalSwitchBuilder oflsBuilder = new OfconfigLogicalSwitchBuilder();
                    oflsBuilder.setLogicalSwitchDpid(logicSwitch.getDatapathId().getValue())
                            .setLogicalSwitchId(logicSwitch.getId().getValue())
                            .setManagedBy(new NodeId(managedByofcSwNodeId));
                    oflsNodeBuilder.setOfconfigLogicalSwitch(oflsBuilder.build());


                    NodeBuilder nodeBuilder = new NodeBuilder();
                    nodeBuilder.setId(nodeId);
                    nodeBuilder.setKey(nodeKey);
                    nodeBuilder.addAugmentation(OfconfigLogicalSwitchNode.class,
                            oflsNodeBuilder.build());

                    Node oflsInvNode = nodeBuilder.build();
                    invTopoWriteTx.put(LogicalDatastoreType.OPERATIONAL, iid, oflsInvNode);

                }


            }

        }
    }
}
