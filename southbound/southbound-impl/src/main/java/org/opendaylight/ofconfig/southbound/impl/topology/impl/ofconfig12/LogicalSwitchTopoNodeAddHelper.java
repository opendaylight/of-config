/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12;

import java.util.List;

import com.google.common.base.Optional;

import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.node.OfconfigLogicalSwitchAttributesBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.types.rev150901.DatapathIdType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.types.rev150901.OfConfigTypeVersion12;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig12.rev150901.of.config.logical._switch.attributes.LogicalSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;


/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class LogicalSwitchTopoNodeAddHelper {


    public void addLogicalSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, WriteTransaction invTopoWriteTx) {

        String nodeStringprefix =
                netconfNodeId.getValue() + ":" + capableSwitchConfig.get().getId();

        List<Switch> swList = null;
        try {
            swList = capableSwitchConfig.get().getLogicalSwitches().getSwitch();
        } catch (Exception e) {
            return;
        }

        for (Switch sw : swList) {

            String nodeString = nodeStringprefix + ":" + sw.getId().getValue();
            NodeId nodeId = new NodeId(new Uri(nodeString));
            NodeKey nodeKey = new NodeKey(nodeId);
            final InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                    .child(Topology.class,
                            new TopologyKey(OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID))
                    .child(Node.class, nodeKey).build();

            OfconfigLogicalSwitchAugmentationBuilder logicSwitchBuilder =
                    new OfconfigLogicalSwitchAugmentationBuilder();

            OfconfigLogicalSwitchAttributesBuilder attrBuilder =
                    new OfconfigLogicalSwitchAttributesBuilder();

            LogicalSwitchBuilder logicSwBuilder = new LogicalSwitchBuilder();
            logicSwBuilder.setCapabilities(sw.getCapabilities()).setControllers(sw.getControllers())
                    .setDatapathId(sw.getDatapathId()).setId(sw.getId())
                    .setLostConnectionBehavior(sw.getLostConnectionBehavior())
                    .setResources(sw.getResources());



            attrBuilder.setCapableSwitchId(capableSwitchConfig.get().getId())
                    .setDatapathId(new DatapathIdType(sw.getDatapathId().getValue()))
                    .setLogicalSwitch(logicSwBuilder.build())
                    .setNetconfTopologyNodeId(netconfNodeId.getValue())
                    .setOfconfigVersion(OfConfigTypeVersion12.class);


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
