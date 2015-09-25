/*
 * Copyright (c) 2015 ZTE, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.md.sal.binding.api.BindingTransactionChain;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.controller.md.sal.common.api.data.TransactionChain;
import org.opendaylight.controller.md.sal.common.api.data.TransactionChainListener;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.inventory.impl.ofconfig12.Ofconfig12InventoryTopoHandler;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public abstract class OfconfigInventoryTopoHandler implements TransactionChainListener {



    private static final Logger LOG = LoggerFactory.getLogger(OfconfigInventoryTopoHandler.class);


    public static Map<String, OfconfigInventoryTopoHandler> capabilityToHandlers = new HashMap<>();

    static {
        capabilityToHandlers.put(OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY,
                new Ofconfig12InventoryTopoHandler());

    }


    private OfconfigInventoryTopoHandlerHelper helper = new OfconfigInventoryTopoHandlerHelper();

    public static OfconfigInventoryTopoHandler getHandlerInstance(String capability) {
        return capabilityToHandlers.get(capability);
    }



    public void addOfconfigNode(NodeId netconfNodeId, NetconfNode netconfNode,
            MountPointService mountService, DataBroker dataBroker)
                    throws TransactionCommitFailedException {


        final Optional<MountPoint> capableSwichNodeOptional =
                mountService.getMountPoint(OfconfigConstants.NETCONF_TOPO_IID.child(Node.class,
                        new NodeKey(new NodeId(netconfNodeId))));

        final MountPoint capableSwichMountPoint = capableSwichNodeOptional.get();

        final DataBroker capableSwichNodeBroker =
                capableSwichMountPoint.getService(DataBroker.class).get();

        final ReadOnlyTransaction capableSwichNodeReadTx =
                capableSwichNodeBroker.newReadOnlyTransaction();

        Optional<CapableSwitch> capableSwitchConfig =
                helper.getCapableSwitchCinfigureFromOfDevice(netconfNodeId, capableSwichNodeReadTx);

        if (capableSwitchConfig.isPresent()) {
            BindingTransactionChain chain = null;
            try {
                chain = dataBroker.createTransactionChain(this);
                final WriteTransaction invTopoWriteTx = chain.newWriteOnlyTransaction();



                addCapableSwitchTopoNodeAttributes(netconfNodeId, capableSwitchConfig,
                        capableSwichNodeReadTx, invTopoWriteTx);
                addCapableSwitchTopoNodeAttributes(netconfNodeId, capableSwitchConfig,
                        capableSwichNodeReadTx, invTopoWriteTx);

                addCapableSwitchInventoryNodeAttributes(netconfNodeId, netconfNode,
                        capableSwitchConfig, capableSwichNodeReadTx, invTopoWriteTx);
                addLogicalSwitchInventoryNodeAttributes(netconfNodeId, capableSwitchConfig,
                        capableSwichNodeReadTx, invTopoWriteTx);

                CheckedFuture<Void, TransactionCommitFailedException> future =
                        invTopoWriteTx.submit();
                try {
                    future.checkedGet();
                } catch (TransactionCommitFailedException e) {
                    LOG.warn("{} of-config switch failed to merge Inventory topology ", netconfNode,
                            e);
                    throw e;

                }
            } finally {
                if (chain != null) {
                    chain.close();
                }
            }
        } else {
            throw new IllegalStateException("Unexpected error reading data from " + netconfNode);
        }

    }



    public void removeOfconfigNodeFromInventory(NodeId nodeId, DataBroker dataBroker)
            throws ReadFailedException, InterruptedException, ExecutionException,
            TransactionCommitFailedException {

        BindingTransactionChain chain = null;
        try {
            chain = dataBroker.createTransactionChain(this);
            final ReadWriteTransaction tx = chain.newReadWriteTransaction();

            List<String> logicSwitchNodeIds =
                    helper.getLogicSwitchNodeIdsFromInventory(nodeId.getValue(), tx);

            String netconfNodeId = nodeId.getValue();

            removeCapableSwitchTopoNodeAttributes(netconfNodeId, tx);

            removeLogicSwitchTopoNodeAttributes(logicSwitchNodeIds, tx);

            removeCapableSwitchInventoryNodeAttributes(netconfNodeId, tx);

            removeLocgicalSwitchInventoryNodeAttributes(logicSwitchNodeIds, tx);
            CheckedFuture<Void, TransactionCommitFailedException> future = tx.submit();
            try {
                future.checkedGet();
            } catch (TransactionCommitFailedException e) {
                LOG.warn("{} of-config switch failed to remove Inventory/topology node", nodeId, e);
                throw e;

            }



        } finally {
            if (chain != null) {
                chain.close();
            }
        }



    }



    private void removeLocgicalSwitchInventoryNodeAttributes(List<String> logicSwitchNodeIds,
            ReadWriteTransaction tx) {
        for (String logicSwitchNodeId : logicSwitchNodeIds) {

            org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId invNodeId =
                    new org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId(
                            logicSwitchNodeId);
            org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey invNodeKey =
                    new org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey(
                            invNodeId);

            InstanceIdentifier<org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node> iid =
                    InstanceIdentifier
                            .builder(
                                    org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes.class)
                            .child(org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node.class,
                                    invNodeKey)
                            .build();

            tx.delete(LogicalDatastoreType.OPERATIONAL, iid);

        }

    }



    private void removeCapableSwitchInventoryNodeAttributes(String netconfNodeId,
            ReadWriteTransaction tx) {

        org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId invNodeId =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId(
                        netconfNodeId);
        org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey invNodeKey =
                new org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey(
                        invNodeId);

        InstanceIdentifier<org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node> iid =
                InstanceIdentifier
                        .builder(
                                org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes.class)
                        .child(org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node.class,
                                invNodeKey)
                        .build();

        tx.delete(LogicalDatastoreType.OPERATIONAL, iid);


    }



    private void removeLogicSwitchTopoNodeAttributes(List<String> logicSwitchNodeIds,
            WriteTransaction tx) {

        for (String logicSwitchNodeId : logicSwitchNodeIds) {
            NodeId nodeId = new NodeId(logicSwitchNodeId);
            NodeKey nodeKey = new NodeKey(nodeId);
            InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                    .child(Topology.class,
                            new TopologyKey(OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID))
                    .child(Node.class, nodeKey).build();

            tx.delete(LogicalDatastoreType.OPERATIONAL, iid);
        }

    }



    private void removeCapableSwitchTopoNodeAttributes(String netconfNodeId, WriteTransaction tx) {

        NodeKey nodeKey = new NodeKey(new NodeId(netconfNodeId));
        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();

        tx.delete(LogicalDatastoreType.OPERATIONAL, iid);

    }



    protected abstract void addCapableSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx);

    protected abstract void addLogicalSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx);

    protected abstract void addCapableSwitchInventoryNodeAttributes(NodeId netconfNodeId,
            NetconfNode netconfNode, Optional<CapableSwitch> capableSwitchConfig,
            ReadOnlyTransaction ofconfigNodeReadTx, WriteTransaction invTopoWriteTx);

    protected abstract void addLogicalSwitchInventoryNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx);


    @Override
    public void onTransactionChainFailed(TransactionChain<?, ?> chain,
            AsyncTransaction<?, ?> transaction, Throwable cause) {
        LOG.error("Broken chain {} in TxchainDomWrite, transaction {}, cause {}", chain,
                transaction.getIdentifier(), cause);
    }

    @Override
    public void onTransactionChainSuccessful(TransactionChain<?, ?> chain) {
        LOG.info("Chain {} closed successfully", chain);
    }



}
