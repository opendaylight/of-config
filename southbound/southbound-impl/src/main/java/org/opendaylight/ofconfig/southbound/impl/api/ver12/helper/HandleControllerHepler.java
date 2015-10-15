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
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.LogicalSwitchesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.SwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.ControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.ControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.ControllerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigCapableSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.Controller;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.yang.common.RpcResult;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class HandleControllerHepler extends AbstractHandleHelper{
    
    public HandleControllerHepler(MountPointService mountService, DataBroker dataBroker) {
        super(mountService,dataBroker);
    }
    
    
    public Future<RpcResult<Void>> dispatchHandleRequest(HandleControllersInput input){
        
        if(input.getHandleMode().equals(HandleMode.Merge)){
            return doMergeOperation(input.getTopoLogicalSwitchNodeId(),input.getController());
        }
        
        
        return null;
    }
    
    private Future<RpcResult<Void>> doMergeOperation(String logicalSwitchNodeId, List<Controller> controllerlist){
        
       
        Node  logicalSwitchNode =  getLogicalSwitchTopoNodeByNodeId(logicalSwitchNodeId);
        if(logicalSwitchNode==null){
            return buildNotFoundResult(logicalSwitchNodeId);
        }
        
        
        OfconfigLogicalSwitchAugmentation ofconfigLogicalSwitchNode = logicalSwitchNode.getAugmentation(OfconfigLogicalSwitchAugmentation.class);
        
       String capableSwitchNodeId = ofconfigLogicalSwitchNode.getOfconfigLogicalSwitchAttributes().getCapableSwitchId();
        
       Node capableSwitchNode = getCapableSwitchTopoNodeByNodeId(capableSwitchNodeId);
       
       if(capableSwitchNode==null){
           return buildNotFoundResult(capableSwitchNodeId);
       }
       
       
       DataBroker netconfMountDataBroker =  getMountPointDataBroker(capableSwitchNode.getNodeId().getValue());
       
       CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
       
       
       OfconfigCapableSwitchAugmentation   ofCapableSwitch =   capableSwitchNode.getAugmentation(OfconfigCapableSwitchAugmentation.class);
       
       cpswBuilder.setId(ofCapableSwitch.getOfconfigCapableSwitchAttributes().getCapableSwitch().getId());
       
       LogicalSwitchesBuilder logicalSwitchesBuilder = new LogicalSwitchesBuilder();
       
       List<Switch> switches = new ArrayList<>();
       
       SwitchBuilder swBuilder = new SwitchBuilder();
       
       ControllersBuilder ctrlsBuilder = new ControllersBuilder();
       
       List<org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.
           oflogicalswitchtype.controllers.Controller> controllers = new ArrayList<>();
       
       for(Controller controller : controllerlist){
           ControllerBuilder ctlBuilder = new ControllerBuilder();
           ctlBuilder.setId(controller.getId());
           ctlBuilder.setIpAddress(controller.getIpAddress());
           ctlBuilder.setKey(new ControllerKey(controller.getKey().getId()));
           ctlBuilder.setLocalIpAddress(controller.getLocalIpAddress());
           ctlBuilder.setPort(controller.getPort());
           ctlBuilder.setProtocol(controller.getProtocol());
           ctlBuilder.setState(controller.getState());
           
           controllers.add(ctlBuilder.build());
       }
       
       
       ctrlsBuilder.setController(controllers);
       
       swBuilder.setControllers(ctrlsBuilder.build());
       
       switches.add(swBuilder.build());
       
       logicalSwitchesBuilder.setSwitch(switches);
       
       
       cpswBuilder.setLogicalSwitches(logicalSwitchesBuilder.build());
        
        
        return null;
    }
}
