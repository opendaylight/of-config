/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.topology;

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
import org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12.Ofconfig12InventoryTopoHandler;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
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
public abstract class OfconfigTopoHandler implements TransactionChainListener {



    private static final Logger LOG = LoggerFactory.getLogger(OfconfigTopoHandler.class);


    public static Map<String, OfconfigTopoHandler> capabilityToHandlers = new HashMap<>();

    static {
        capabilityToHandlers.put(OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY,
                new Ofconfig12InventoryTopoHandler());

    }


    private OfconfigInventoryTopoHandlerHelper helper = new OfconfigInventoryTopoHandlerHelper();

    public static OfconfigTopoHandler getHandlerInstance(String capability) {
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



                addCapableSwitchTopoNodeAttributes(netconfNodeId, capableSwitchConfig,invTopoWriteTx);
                addLogicalSwitchTopoNodeAttributes(netconfNodeId, capableSwitchConfig, invTopoWriteTx);


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



    public void removeOfconfigNode(NodeId netconfNodeId, DataBroker dataBroker)
            throws ReadFailedException, InterruptedException, ExecutionException,
            TransactionCommitFailedException {

        BindingTransactionChain chain = null;
        try {
            chain = dataBroker.createTransactionChain(this);
            final ReadWriteTransaction tx = chain.newReadWriteTransaction();

            List<String> logicSwitchNodeIds =
                    helper.getLogicSwitchNodeIdsFromTopo(netconfNodeId.getValue(), dataBroker);

            String netconfNodeIdValue = netconfNodeId.getValue();

            removeCapableSwitchTopoNodeAttributes(netconfNodeIdValue, tx);

            removeLogicSwitchTopoNodeAttributes(logicSwitchNodeIds, tx);

            CheckedFuture<Void, TransactionCommitFailedException> future = tx.submit();
            try {
                future.checkedGet();
            } catch (TransactionCommitFailedException e) {
                LOG.warn("{} of-config switch failed to remove Inventory/topology node", netconfNodeId, e);
                throw e;

            }

        } finally {
            if (chain != null) {
                chain.close();
            }
        }
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
            Optional<CapableSwitch> capableSwitchConfig,WriteTransaction invTopoWriteTx);

    protected abstract void addLogicalSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig,
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
