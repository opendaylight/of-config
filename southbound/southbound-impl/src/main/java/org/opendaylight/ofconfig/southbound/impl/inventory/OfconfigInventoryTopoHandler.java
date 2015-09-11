/*
 * Copyright (c) 2015 ZTE, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.inventory;

import java.util.HashMap;
import java.util.Map;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.inventory.impl.Ofconfig12InventoryTopoHandler;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.network.topology.topology.topology.types.TopologyOfconfig;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
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
public abstract class OfconfigInventoryTopoHandler {

    
    
    protected static final InstanceIdentifier<Topology> OFCONFIG_TOPO_IID =
            InstanceIdentifier
            .create(NetworkTopology.class)
            .child(Topology.class,
                   new TopologyKey(new TopologyId(TopologyOfconfig.QNAME.getLocalName())));
    

    private static final Logger LOG = LoggerFactory.getLogger(OfconfigInventoryTopoHandler.class);

   
    public static Map<String,OfconfigInventoryTopoHandler> capabilityToHandlers = new HashMap<>();
    static{
        capabilityToHandlers.put(OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY, new Ofconfig12InventoryTopoHandler());
        
    }
    
    protected MdsalUtils mdsalUtils = new MdsalUtils();
    
    
    public static OfconfigInventoryTopoHandler getHandlerInstance(String capability){
        return capabilityToHandlers.get(capability);
    }
    
   


    public void addOfconfigNodeToInventory(NodeId netconfNodeId,MountPointService mountService,DataBroker dataBroker) {


        final Optional<MountPoint> capableSwichNodeOptional = mountService.getMountPoint(
                OfconfigConstants.NETCONF_TOPO_IID.child(Node.class, new NodeKey(new NodeId(netconfNodeId))));

        final MountPoint capableSwichMountPoint = capableSwichNodeOptional.get();

        final DataBroker capableSwichNodeBroker =
                capableSwichMountPoint.getService(DataBroker.class).get();

        final ReadOnlyTransaction capableSwichNodeReadTx =
                capableSwichNodeBroker.newReadOnlyTransaction();

        final WriteTransaction invTopoWriteTx = dataBroker.newWriteOnlyTransaction();

        Node ofconfigNode = addCapableSwitchNodeAttributes(netconfNodeId,capableSwichNodeReadTx, invTopoWriteTx);

        addLogicalSwitchNodeAttributes(ofconfigNode, invTopoWriteTx);
        
        
        CheckedFuture<Void, TransactionCommitFailedException> future = invTopoWriteTx.submit();
        try {
            future.checkedGet();
        } catch (TransactionCommitFailedException e) {
            LOG.warn("{} of-config switch failed to merge Inventory topology ", netconfNodeId, e);
        }

        
        
    }



    protected abstract Node addCapableSwitchNodeAttributes(NodeId netconfNodeId,ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx);

    protected abstract void addLogicalSwitchNodeAttributes(Node ofconfigNode,
            WriteTransaction invTopoWriteTx);

}
