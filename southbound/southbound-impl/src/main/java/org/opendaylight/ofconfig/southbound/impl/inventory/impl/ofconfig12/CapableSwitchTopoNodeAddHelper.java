/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.inventory.impl.ofconfig12;

import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.OfconfigCapableSwitchAttributesBuilder;
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

import com.google.common.base.Optional;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class CapableSwitchTopoNodeAddHelper {

    
    void addCapableSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {


        String nodeString = netconfNodeId.getValue() /*+ ":" + capableSwitchConfig.get().getId()*/;

        NodeId nodeId = new NodeId(new Uri(nodeString));
        NodeKey nodeKey = new NodeKey(nodeId);
        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();
                // remove old ofconfig-node

        // invTopoWriteTx.delete(LogicalDatastoreType.OPERATIONAL, iid);


        OfconfigCapableSwitchAugmentationBuilder ofconfigNodeBuilder =
                new OfconfigCapableSwitchAugmentationBuilder();



        OfconfigCapableSwitchAttributesBuilder attributesBuilder =
                new OfconfigCapableSwitchAttributesBuilder();

        CapableSwitchBuilder capableSwitchBuilder = new CapableSwitchBuilder();

        capableSwitchBuilder.setConfigVersion(capableSwitchConfig.get().getConfigVersion());
        capableSwitchBuilder.setId(capableSwitchConfig.get().getId());
        capableSwitchBuilder.setLogicalSwitches(capableSwitchConfig.get().getLogicalSwitches());
        capableSwitchBuilder.setResources(capableSwitchConfig.get().getResources());

        attributesBuilder.setCapableSwitch(capableSwitchBuilder.build());
        attributesBuilder.setOfconfigVersion(OfConfigTypeVersion12.class)
                .setNetconfTopologyNodeId(netconfNodeId.getValue());

        ofconfigNodeBuilder.setOfconfigCapableSwitchAttributes(attributesBuilder.build());

        NodeBuilder nodeBuilder = new NodeBuilder();
        nodeBuilder.setNodeId(nodeId);
        nodeBuilder.setKey(nodeKey);
        nodeBuilder.addAugmentation(OfconfigCapableSwitchAugmentation.class,
                ofconfigNodeBuilder.build());

        Node ofconfigNode = nodeBuilder.build();
        invTopoWriteTx.put(LogicalDatastoreType.OPERATIONAL, iid, ofconfigNode);


        // add inventory node
    }
}
