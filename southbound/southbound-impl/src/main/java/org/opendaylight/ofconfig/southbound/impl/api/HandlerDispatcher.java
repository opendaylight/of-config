/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.api;

import java.util.Map;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandleControllerHepler;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandleExtCertHelper;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandleFlowtableHepler;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandleLogicSwitchHelper;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandleOwnedCertHepler;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandlePortResourceHelper;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandleQueueResourceHepler;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandleTunnelHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleExtCertInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleFlowtableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleLogicSwitchInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandlePortResourceInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleQueueResourceInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleTunnelInput;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.collect.Maps;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class HandlerDispatcher {

    
    private Map<Class,IHandlerHelper> requestToHandlers = Maps.newHashMap();
    
    private static class Holder {
        static final HandlerDispatcher INSTANCE = new HandlerDispatcher();
    }

    
    public static HandlerDispatcher instance() {
        return Holder.INSTANCE;
    }
    
    public void init(MountPointService mountService, DataBroker dataBroker){
        
        requestToHandlers.put(HandleControllersInput.class, new HandleControllerHepler(mountService,dataBroker));
        requestToHandlers.put(HandleOwnedCertInput.class, new HandleOwnedCertHepler(mountService,dataBroker));
        requestToHandlers.put(HandleFlowtableInput.class, new HandleFlowtableHepler(mountService, dataBroker));
        requestToHandlers.put(HandleExtCertInput.class, new HandleExtCertHelper(mountService, dataBroker));
        requestToHandlers.put(HandleTunnelInput.class, new HandleTunnelHelper(mountService, dataBroker));
        requestToHandlers.put(HandleQueueResourceInput.class, new HandleQueueResourceHepler(mountService, dataBroker));
        requestToHandlers.put(HandleLogicSwitchInput.class, new HandleLogicSwitchHelper(mountService,dataBroker));
        requestToHandlers.put(HandlePortResourceInput.class, new HandlePortResourceHelper(mountService,dataBroker));
        
        
    }
    
    
    public  <T> Future<RpcResult<Void>> dispatchToHandlerHelper(T request){
        
        IHandlerHelper helper =  requestToHandlers.get(request.getClass());
                
        HandleMode   handleMode =      helper.getRequestHandleMode(request);
        
        if(handleMode==HandleMode.Delete){
            return helper.doDelete(request);
        }else if(handleMode==HandleMode.Merge){
            return helper.doMerge(request);
        }else{
            return helper.doPut(request);
        }
        
    }
    
    
    
    
    
    
    
    
    
    
    
}
