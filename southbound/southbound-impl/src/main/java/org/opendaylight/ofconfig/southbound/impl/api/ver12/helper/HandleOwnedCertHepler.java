/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.api.ver12.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.ResourcesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.OwnedCertificate;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.OwnedCertificateBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.OwnedCertificateKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInput;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError.ErrorType;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.SettableFuture;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class HandleOwnedCertHepler {

    private MountPointService mountService;
    private DataBroker dataBroker;
    
    private MdsalUtils mdsalUtils = new MdsalUtils();
    
    public HandleOwnedCertHepler(MountPointService mountService, DataBroker dataBroker) {
        super();
        this.mountService = mountService;
        this.dataBroker = dataBroker;
    }
    
    
    public Future<RpcResult<Void>> dispatchHandleRequest(HandleOwnedCertInput request){ 
        
        doMergeOperation(request.getTopoLogicalSwitchNodeId(),request.getOwnedCertificate());
        
        return null;
    }
    
    
    private Future<RpcResult<Void>> doMergeOperation(String capableSwitchNodeId, 
            List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_owned_cert.OwnedCertificate> list){
        
        
  
        NodeId nodeId = new NodeId(new Uri(capableSwitchNodeId));
        NodeKey nodeKey = new NodeKey(nodeId);
        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class,
                        new TopologyKey(OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID))
                .child(Node.class, nodeKey).build();
        
        Node  capableSwitchNode =  mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, iid, dataBroker);
        if(capableSwitchNode==null){
            
            SettableFuture<RpcResult<Void>> resultFuture = SettableFuture.create();
            
            RpcResult<Void> rpcResult =  RpcResultBuilder.<Void>failed().withError(ErrorType.APPLICATION, 
                    "No corresponding nodes are found in the topology,nodeId:"+capableSwitchNodeId).build();
            
            resultFuture.set(rpcResult);
            return resultFuture;
            
        }
        
        OfconfigCapableSwitchAugmentation   ofCapableSwitch =   capableSwitchNode.getAugmentation(OfconfigCapableSwitchAugmentation.class);
        
        String netconfNodeId = capableSwitchNodeId;
        // build request
        final Optional<MountPoint> capableSwichNodeOptional =
                mountService.getMountPoint(OfconfigConstants.NETCONF_TOPO_IID.child(Node.class,
                        new NodeKey(new NodeId(netconfNodeId))));
        
        MountPoint netconfMountPoint =  capableSwichNodeOptional.get();
        
        final DataBroker capableSwichNodeBroker =
                netconfMountPoint.getService(DataBroker.class).get();
        

        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        
        cpswBuilder.setId(ofCapableSwitch.getOfconfigCapableSwitchAttributes().getCapableSwitch().getId());
        
        ResourcesBuilder resBuilder = new ResourcesBuilder();
        
        List<OwnedCertificate> ownedCerts = new ArrayList<>();
        
        for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_owned_cert.OwnedCertificate ownedCert:list){
            OwnedCertificateBuilder certBuilder = new OwnedCertificateBuilder();
            certBuilder.setCertificate(ownedCert.getCertificate()).
                setKey(new OwnedCertificateKey(ownedCert.getKey().getResourceId()))
                .setPrivateKey(ownedCert.getPrivateKey()).setResourceId(ownedCert.getResourceId());
            ownedCerts.add(certBuilder.build());
        }
        
        
        resBuilder.setOwnedCertificate(ownedCerts);
        
        cpswBuilder.setResources(resBuilder.build());
        
        
        InstanceIdentifier<CapableSwitch> capableSwitchiid  =  InstanceIdentifier.builder(CapableSwitch.class).build();
        
        
        if(!mdsalUtils.merge(LogicalDatastoreType.CONFIGURATION, capableSwitchiid, cpswBuilder.build(), capableSwichNodeBroker)){
            SettableFuture<RpcResult<Void>> resultFuture = SettableFuture.create();
            
            RpcResult<Void> rpcResult =  RpcResultBuilder.<Void>failed().withError(ErrorType.APPLICATION, 
                    "No corresponding nodes are found in the topology,nodeId:"+capableSwitchNodeId).build();
            
            resultFuture.set(rpcResult);
            return resultFuture;
        
        }
        
        SettableFuture<RpcResult<Void>> resultFuture = SettableFuture.create();
        
        RpcResult<Void> rpcResult =  RpcResultBuilder.<Void>success().build();
        
        resultFuture.set(rpcResult);
        
        
        
        return resultFuture;
    }
    
    
    
}
