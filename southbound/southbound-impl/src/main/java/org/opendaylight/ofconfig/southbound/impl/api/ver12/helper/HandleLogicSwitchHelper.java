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
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.OFConfigId;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.LogicalSwitches;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.LogicalSwitchesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.ResourcesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.SwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.SwitchKey;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.ExternalCertificate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleLogicSwitchInput;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class HandleLogicSwitchHelper extends AbstractOfconfigVer12HandlerHelper<HandleLogicSwitchInput> {

    public HandleLogicSwitchHelper(MountPointService mountService, DataBroker dataBroker) {
        super(mountService, dataBroker);
    }

    @Override
    public HandleMode getRequestHandleMode(HandleLogicSwitchInput request) {
        // TODO Auto-generated method stub
        return request.getHandleMode();
    }

    @Override
    String getNetconfigTopoNodeId(HandleLogicSwitchInput request) {
        return request.getTopoCapableSwitchNodeId();
    }

    @Override
    CapableSwitch mergeCapableSwitch(CapableSwitch capableSwitch,
            HandleLogicSwitchInput request) {
        
        LogicalSwitches logicalSwitches =  capableSwitch.getLogicalSwitches();
        if(logicalSwitches==null){
            capableSwitch = buildCapableSwitchLogicalSwitches(capableSwitch);
            logicalSwitches =  capableSwitch.getLogicalSwitches();
        }
        
        List<Switch> switches = logicalSwitches.getSwitch();
        if(switches==null){
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            switches = capableSwitch.getLogicalSwitches().getSwitch();
        }
        
        
        Map<OFConfigId, Switch> mergeMap = Maps.newHashMap();
        for (Switch logicalSwitch : switches) {
            mergeMap.put(logicalSwitch.getId(), logicalSwitch);
        }
        
        for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_logic_switch.Switch paramSwitch: request.getSwitch()){
            OFConfigId id = paramSwitch.getId();
            if(mergeMap.containsKey(id)){
                continue;
            }
            
            SwitchBuilder builder = new SwitchBuilder();
            
            builder.setCapabilities(paramSwitch.getCapabilities()).setControllers(paramSwitch.getControllers())
                .setDatapathId(paramSwitch.getDatapathId()).setId(paramSwitch.getId()).setKey(new SwitchKey(paramSwitch.getId()))
                .setLostConnectionBehavior(paramSwitch.getLostConnectionBehavior()).setResources(paramSwitch.getResources());
            
            switches.add(builder.build());
            
        }
        return capableSwitch;
    }

    

    

    @Override
    CapableSwitch deleteCapableSwitch(CapableSwitch capableSwitch,
            HandleLogicSwitchInput request) {
        
        LogicalSwitches logicalSwitches =  capableSwitch.getLogicalSwitches();
        if(logicalSwitches==null){
            return capableSwitch;
        }
        
        List<Switch> switches = logicalSwitches.getSwitch();
        if(switches==null){
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            return capableSwitch;
        }
        
        Map<OFConfigId, Switch> mergeMap = Maps.newHashMap();
        for (Switch logicalSwitch : switches) {
            mergeMap.put(logicalSwitch.getId(), logicalSwitch);
        }
        
        for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_logic_switch.Switch paramSwitch: request.getSwitch()){
            OFConfigId id = paramSwitch.getId();
            mergeMap.remove(id);
        }
        
        switches.clear();
        
        switches.addAll(mergeMap.values());
        
        return capableSwitch;
    }

    @Override
    CapableSwitch putCapableSwitch(CapableSwitch capableSwitch,
            HandleLogicSwitchInput request) {
        
        LogicalSwitches logicalSwitches =  capableSwitch.getLogicalSwitches();
        if(logicalSwitches==null){
            capableSwitch = buildCapableSwitchLogicalSwitches(capableSwitch);
            logicalSwitches =  capableSwitch.getLogicalSwitches();
        }
        
        List<Switch> switches = logicalSwitches.getSwitch();
        if(switches==null){
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            switches = capableSwitch.getLogicalSwitches().getSwitch();
        }
        switches.clear();
        
        for(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_logic_switch.Switch paramSwitch: request.getSwitch()){
       
            
            SwitchBuilder builder = new SwitchBuilder();
            
            builder.setCapabilities(paramSwitch.getCapabilities()).setControllers(paramSwitch.getControllers())
                .setDatapathId(paramSwitch.getDatapathId()).setId(paramSwitch.getId()).setKey(new SwitchKey(paramSwitch.getId()))
                .setLostConnectionBehavior(paramSwitch.getLostConnectionBehavior()).setResources(paramSwitch.getResources());
            
            switches.add(builder.build());
            
        }
        
        
        
        return capableSwitch;
    }
    
    private CapableSwitch buildCapableSwitchLogicalSwitches(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion()).setResources(capableSwitch.getResources());
        
        LogicalSwitchesBuilder builder = new LogicalSwitchesBuilder();
        
        List<Switch> logicalSwitchList=  Lists.newArrayList();
        
        builder.setSwitch(logicalSwitchList);
        
        return capableSwitch;
    }
    
    
    private CapableSwitch buildCapableSwitchResources(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion()).setLogicalSwitches(capableSwitch.getLogicalSwitches());
        
        ResourcesBuilder resBuilder = new ResourcesBuilder();
        
        List<ExternalCertificate> extCertificateList=  Lists.newArrayList();
         
         resBuilder.setExternalCertificate(extCertificateList);

        
        cpswBuilder.setResources(resBuilder.build());
        
        return cpswBuilder.build();
    }
    
   
    

}
