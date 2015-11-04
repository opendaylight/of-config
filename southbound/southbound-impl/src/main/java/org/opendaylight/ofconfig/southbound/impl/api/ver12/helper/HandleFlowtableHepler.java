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
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.FlowTable;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.FlowTableBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.FlowTableKey;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.OwnedCertificateKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleFlowtableInput;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class HandleFlowtableHepler extends AbstractOfconfigVer12HandlerHelper<HandleFlowtableInput> {

    public HandleFlowtableHepler(MountPointService mountService, DataBroker dataBroker) {
        super(mountService, dataBroker);
    }

    @Override
    public HandleMode getRequestHandleMode(HandleFlowtableInput request) {
        return request.getHandleMode();
    }

    @Override
    String getNetconfigId(HandleFlowtableInput request) {
        return request.getTopoCapableSwitchNodeId();
    }

    @Override
    CapableSwitch mergeCapableSwitchAndMergeObject(CapableSwitch capableSwitch,
            HandleFlowtableInput request) {

        Resources resources =  capableSwitch.getResources();
        if(resources==null){
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources =  capableSwitch.getResources();
        }
        
       List<FlowTable> flowTables = resources.getFlowTable();
       if(flowTables==null){
           capableSwitch = buildCapableSwitchResourcesFlowTable(capableSwitch);
           flowTables=capableSwitch.getResources().getFlowTable();
       }
       
       Map<Uri, FlowTable> mergeMap = Maps.newHashMap();
       for (FlowTable flowTable : flowTables) {
           mergeMap.put(flowTable.getResourceId(), flowTable);
       }
       
       for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_flowtable.FlowTable paramFlowtable: request.getFlowTable()){
           Uri paramUri = paramFlowtable.getResourceId();
           if(mergeMap.containsKey(paramUri)){
               continue;
           }
           
           FlowTableBuilder builder = new FlowTableBuilder();
           
           builder.setKey(new FlowTableKey(paramFlowtable.getTableId()))
               .setMaxEntries(paramFlowtable.getMaxEntries()).setName(paramFlowtable.getName())
               .setResourceId(paramFlowtable.getResourceId()).setTableId(paramFlowtable.getTableId());
           
           
           flowTables.add(builder.build());
           
       }
       return capableSwitch;
    }

    
    @Override
    CapableSwitch deleteCapableSwitchAndMergeObject(CapableSwitch capableSwitch,
            HandleFlowtableInput request) {
        
        Resources resources =  capableSwitch.getResources();
        if(resources==null){
            return capableSwitch;
        }
        
       List<FlowTable> flowTables = resources.getFlowTable();
       if(flowTables==null){
           return capableSwitch;
       }
       
       Map<Uri, FlowTable> mergeMap = Maps.newHashMap();
       for (FlowTable flowTable : flowTables) {
           mergeMap.put(flowTable.getResourceId(), flowTable);
       }
       
       for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_flowtable.FlowTable paramFlowtable: request.getFlowTable()){
           Uri paramUri = paramFlowtable.getResourceId();
           mergeMap.remove(paramUri);
       }   
       
       flowTables.clear();
       
       flowTables.addAll(mergeMap.values());
        
        return capableSwitch;
    }

    @Override
    CapableSwitch putCapableSwitchAndMergeObject(CapableSwitch capableSwitch,
            HandleFlowtableInput request) {

        Resources resources =  capableSwitch.getResources();
        if(resources==null){
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources =  capableSwitch.getResources();
        }
        
       List<FlowTable> flowTables = resources.getFlowTable();
       if(flowTables==null){
           capableSwitch = buildCapableSwitchResourcesFlowTable(capableSwitch);
           flowTables=capableSwitch.getResources().getFlowTable();
       }
       
       flowTables.clear();
       
       for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_flowtable.FlowTable paramFlowtable: request.getFlowTable()){
         FlowTableBuilder builder = new FlowTableBuilder();
           
           builder.setKey(new FlowTableKey(paramFlowtable.getTableId()))
               .setMaxEntries(paramFlowtable.getMaxEntries()).setName(paramFlowtable.getName())
               .setResourceId(paramFlowtable.getResourceId()).setTableId(paramFlowtable.getTableId());
           
           
           flowTables.add(builder.build());
           
       }
       
        return capableSwitch;
    }
    
    
    private CapableSwitch buildCapableSwitchResources(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion()).setLogicalSwitches(capableSwitch.getLogicalSwitches());
        
        ResourcesBuilder resBuilder = new ResourcesBuilder();
        
         List<FlowTable> flowTableList=  Lists.newArrayList();
         
         resBuilder.setFlowTable(flowTableList);
        
        cpswBuilder.setResources(resBuilder.build());
        
        return cpswBuilder.build();
    }
    
    
    private CapableSwitch buildCapableSwitchResourcesFlowTable(CapableSwitch capableSwitch) {
       
        
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion()).setLogicalSwitches(capableSwitch.getLogicalSwitches());
        
        ResourcesBuilder resBuilder = new ResourcesBuilder();
        
         List<FlowTable> flowTableList=  Lists.newArrayList();
         
         resBuilder.setFlowTable(flowTableList).setExternalCertificate(capableSwitch.getResources().getExternalCertificate())
         .setOwnedCertificate(capableSwitch.getResources().getOwnedCertificate()).setPort(capableSwitch.getResources().getPort())
         .setQueue(capableSwitch.getResources().getQueue());
        
        cpswBuilder.setResources(resBuilder.build());
        
        return cpswBuilder.build();
    }


}
