/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.api.ver12.helper;

import java.util.List;
import java.util.Map;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.Resources;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.ResourcesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.OwnedCertificate;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.Queue;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.QueueBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.QueueKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleQueueResourceInput;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class HandleQueueResourceHepler extends AbstractOfconfigVer12HandlerHelper<HandleQueueResourceInput>{

    public HandleQueueResourceHepler(MountPointService mountService, DataBroker dataBroker) {
        super(mountService, dataBroker);
    }

    @Override
    public HandleMode getRequestHandleMode(HandleQueueResourceInput request) {
        return request.getHandleMode();
    }

    @Override
    String getNetconfigTopoNodeId(HandleQueueResourceInput request) {
        return request.getTopoCapableSwitchNodeId();
    }

    @Override
    CapableSwitch mergeCapableSwitch(CapableSwitch capableSwitch,
            HandleQueueResourceInput request) {
        Resources resources =  capableSwitch.getResources();
        if(resources==null){
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources =  capableSwitch.getResources();
        }
        
        List<Queue>  queues = resources.getQueue();
        
        if(queues==null){
            capableSwitch = buildCapableSwitchResourcesQueueList(capableSwitch);
            queues = capableSwitch.getResources().getQueue();
         }
        
        Map<Uri, Queue> mergeMap = Maps.newHashMap();
        for (Queue queue : queues) {
            mergeMap.put(queue.getResourceId(), queue);
        }
        
        for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_queue_resource.Queue paramQueue: request.getQueue()){
            Uri paramUri = paramQueue.getResourceId();
            if(mergeMap.containsKey(paramUri)){
                continue;
            }
            
            QueueBuilder builder = new QueueBuilder();
            
            builder.setId(paramQueue.getId())
            .setKey(new QueueKey(paramQueue.getResourceId())).setPort(paramQueue.getPort())
            .setProperties(paramQueue.getProperties()).setResourceId(paramQueue.getResourceId());
            
            
            queues.add(builder.build());
            
        }
         return capableSwitch;
        
    }

   

    @Override
    CapableSwitch deleteCapableSwitch(CapableSwitch capableSwitch,
            HandleQueueResourceInput request) {
      
        Resources resources =  capableSwitch.getResources();
        if(resources==null){
           return capableSwitch;
        }
        
        List<Queue>  queues = resources.getQueue();
        
        if(queues==null){
            return capableSwitch;
         }
        
        Map<Uri, Queue> mergeMap = Maps.newHashMap();
        for (Queue queue : queues) {
            mergeMap.put(queue.getResourceId(), queue);
        }
        
        for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_queue_resource.Queue paramQueue: request.getQueue()){
            Uri paramUri = paramQueue.getResourceId();
            mergeMap.remove(paramUri);
        }
        
        queues.clear();
        
        queues.addAll(mergeMap.values());
        
        
        return capableSwitch;
    }

    @Override
    CapableSwitch putCapableSwitch(CapableSwitch capableSwitch,
            HandleQueueResourceInput request) {
       
        Resources resources =  capableSwitch.getResources();
        if(resources==null){
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources =  capableSwitch.getResources();
        }
        
        List<Queue>  queues = resources.getQueue();
        
        if(queues==null){
            capableSwitch = buildCapableSwitchResourcesQueueList(capableSwitch);
            queues = capableSwitch.getResources().getQueue();
         }
        
        queues.clear();
        
        for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_queue_resource.Queue paramQueue: request.getQueue()){
             
                QueueBuilder builder = new QueueBuilder();
                
                builder.setId(paramQueue.getId())
                .setKey(new QueueKey(paramQueue.getResourceId())).setPort(paramQueue.getPort())
                .setProperties(paramQueue.getProperties()).setResourceId(paramQueue.getResourceId());
                
                
                queues.add(builder.build());
            
        }
        
        
        return capableSwitch;
    }
    
    private CapableSwitch buildCapableSwitchResourcesQueueList(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion()).setLogicalSwitches(capableSwitch.getLogicalSwitches());
        
        ResourcesBuilder resBuilder = new ResourcesBuilder();
        
        List<Queue>  queueList= Lists.newArrayList();
       
       resBuilder.setOwnedCertificate(capableSwitch.getResources().getOwnedCertificate())
       .setExternalCertificate(capableSwitch.getResources().getExternalCertificate())
       .setFlowTable(capableSwitch.getResources().getFlowTable())
       .setPort(capableSwitch.getResources().getPort())
       .setQueue(queueList);
        
        cpswBuilder.setResources(resBuilder.build());
        
        return cpswBuilder.build();
    }
    
    
    private CapableSwitch buildCapableSwitchResources(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion()).setLogicalSwitches(capableSwitch.getLogicalSwitches());
        
        ResourcesBuilder resBuilder = new ResourcesBuilder();
        
         List<OwnedCertificate> ownedCertificateList=  Lists.newArrayList();
         
         resBuilder.setOwnedCertificate(ownedCertificateList);
        
        cpswBuilder.setResources(resBuilder.build());
        
        return cpswBuilder.build();
    }

}
