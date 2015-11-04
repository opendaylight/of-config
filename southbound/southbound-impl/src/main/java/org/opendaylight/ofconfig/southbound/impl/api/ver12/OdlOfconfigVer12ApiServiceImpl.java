/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.api.ver12;

import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.ofconfig.southbound.impl.api.HandlerDispatcher;
import org.opendaylight.ofconfig.southbound.impl.api.ver12.helper.HandleOwnedCertHepler;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleExtCertInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleFlowtableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleLogicSwitchInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandlePortResourceInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleQueueResourceInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleTunnelInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class OdlOfconfigVer12ApiServiceImpl implements OdlOfconfigVer12ApiService,BindingAwareProvider, AutoCloseable {

    
    private static final Logger LOG = LoggerFactory.getLogger(OdlOfconfigVer12ApiServiceImpl.class);
    
    private MountPointService mountService;
    private DataBroker dataBroker;
    private HandlerDispatcher handlerDispatcher;
    
    private MdsalUtils mdsalUtils = new MdsalUtils();
    
    
    
    @Override
    public void close() throws Exception {
    }


    @Override
    public void onSessionInitiated(ProviderContext session) {
        this.mountService=session.getSALService(MountPointService.class);
        this.dataBroker=session.getSALService(DataBroker.class);
        this.handlerDispatcher = HandlerDispatcher.instance();
        this.handlerDispatcher.init(mountService, dataBroker);
        
        
    }
    
    
    
    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleOwnedCert(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInput)
     */
    @Override
    public Future<RpcResult<Void>> handleOwnedCert(HandleOwnedCertInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input);
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleFlowtable(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleFlowtableInput)
     */
    @Override
    public Future<RpcResult<Void>> handleFlowtable(HandleFlowtableInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input);
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleExtCert(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleExtCertInput)
     */
    @Override
    public Future<RpcResult<Void>> handleExtCert(HandleExtCertInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input);
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleControllers(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInput)
     */
    @Override
    public Future<RpcResult<Void>> handleControllers(HandleControllersInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input);
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleTunnel(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleTunnelInput)
     */
    @Override
    public Future<RpcResult<Void>> handleTunnel(HandleTunnelInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input);
    }


    @Override
    public Future<RpcResult<Void>> handleQueueResource(HandleQueueResourceInput input) {
        // TODO Auto-generated method stub
        return this.handlerDispatcher.dispatchToHandlerHelper(input);
    }


    @Override
    public Future<RpcResult<Void>> handleLogicSwitch(HandleLogicSwitchInput input) {
        // TODO Auto-generated method stub
        return this.handlerDispatcher.dispatchToHandlerHelper(input);
    }


    @Override
    public Future<RpcResult<Void>> handlePortResource(HandlePortResourceInput input) {
        // TODO Auto-generated method stub
        return this.handlerDispatcher.dispatchToHandlerHelper(input);
    }


    

}
