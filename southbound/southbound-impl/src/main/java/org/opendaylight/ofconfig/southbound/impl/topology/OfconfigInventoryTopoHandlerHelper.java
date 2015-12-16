/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.topology;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
class OfconfigInventoryTopoHandlerHelper {

    private static final Logger LOG =
            LoggerFactory.getLogger(OfconfigInventoryTopoHandlerHelper.class);

    private  MdsalUtils mdsalUtils= new MdsalUtils();



    protected Optional<CapableSwitch> getCapableSwitchCinfigureFromOfDevice(
            org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId netconfNodeId,
            ReadOnlyTransaction ofconfigNodeReadTx) {
        InstanceIdentifier<CapableSwitch> iid = InstanceIdentifier.create(CapableSwitch.class);

        Optional<CapableSwitch> capableSwitchConfig;

        try {

            capableSwitchConfig =
                    ofconfigNodeReadTx.read(LogicalDatastoreType.CONFIGURATION, iid).checkedGet();
            return capableSwitchConfig;
        } catch (ReadFailedException e) {
            throw new IllegalStateException("Unexpected error reading data from " + netconfNodeId,
                    e);
        }

    }

    protected List<String> getLogicSwitchNodeIdsFromTopo(String netconfNodeId,
            DataBroker dataBroker)
                    throws ReadFailedException, InterruptedException, ExecutionException {


        NodeKey nodeKey = new NodeKey(new NodeId(netconfNodeId));

        InstanceIdentifier<Node> nodeiid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();


        Node node = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, nodeiid, dataBroker);
        OfconfigCapableSwitchAugmentation capableSwNode =
                node.getAugmentation(OfconfigCapableSwitchAugmentation.class);



        final String logicalNodeIdPrefix =
                netconfNodeId+ ":" + capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch().getId();

       try{
           List<Switch> switches = capableSwNode.getOfconfigCapableSwitchAttributes().getCapableSwitch().getLogicalSwitches().getSwitch();


           return Lists.transform(switches, new Function<Switch, String>() {

            @Override
            public String apply(Switch input) {
                return logicalNodeIdPrefix+":"+input.getId().getValue();
            }});


       }catch(Exception e){
           return Lists.newArrayList();
       }









    }
}
