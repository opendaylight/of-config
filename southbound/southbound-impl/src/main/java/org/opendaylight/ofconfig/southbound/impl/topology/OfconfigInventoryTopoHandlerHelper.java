/*
 * Copyright (c) 2015 ZTE, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.topology;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.CheckedFuture;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
class OfconfigInventoryTopoHandlerHelper {

    private static final Logger LOG =
            LoggerFactory.getLogger(OfconfigInventoryTopoHandlerHelper.class);



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

    protected List<String> getLogicSwitchNodeIdsFromInventory(String nodeIdstring,
            ReadWriteTransaction tx)
                    throws ReadFailedException, InterruptedException, ExecutionException {

        /*NodeId nodeId = new NodeId(nodeIdstring);
        NodeKey nodeKey = new NodeKey(nodeId);

        InstanceIdentifier<Node> iid =
                InstanceIdentifier.builder(Nodes.class).child(Node.class, nodeKey).build();


        CheckedFuture<Optional<Node>, ReadFailedException> readRsFuture =
                tx.read(LogicalDatastoreType.CONFIGURATION, iid);
        try {
            readRsFuture.checkedGet();
        } catch (ReadFailedException e) {
            LOG.warn(" query capable switch {} fail  from Inventory topology ", nodeIdstring, e);
            throw e;
        }

        if (readRsFuture.get().isPresent()) {

            Node cpswNode = readRsFuture.get().get();

            List<NodeId> nodeIds =
                    cpswNode.getAugmentation(OfconfigCapableSwitchNode.class).getManagedNodes();
            if (nodeIds != null && !nodeIds.isEmpty()) {

                return Lists.transform(nodeIds, new Function<NodeId, String>() {
                    @Override
                    public String apply(NodeId input) {
                        return input.getValue();
                    }
                });
            }

        }*/

        return Lists.newArrayList();



    }
}
