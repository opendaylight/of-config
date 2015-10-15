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
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleExtCertInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleFlowtableInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInput;
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
    
    private MdsalUtils mdsalUtils = new MdsalUtils();
    
    
    
    @Override
    public void close() throws Exception {
    }


    @Override
    public void onSessionInitiated(ProviderContext session) {
        this.mountService=session.getSALService(MountPointService.class);
        this.dataBroker=session.getSALService(DataBroker.class);
        
    }
    
    
    
    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleOwnedCert(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInput)
     */
    @Override
    public Future<RpcResult<Void>> handleOwnedCert(HandleOwnedCertInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleFlowtable(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleFlowtableInput)
     */
    @Override
    public Future<RpcResult<Void>> handleFlowtable(HandleFlowtableInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleExtCert(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleExtCertInput)
     */
    @Override
    public Future<RpcResult<Void>> handleExtCert(HandleExtCertInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleControllers(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInput)
     */
    @Override
    public Future<RpcResult<Void>> handleControllers(HandleControllersInput input) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.OdlOfconfigVer12ApiService#handleTunnel(org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleTunnelInput)
     */
    @Override
    public Future<RpcResult<Void>> handleTunnel(HandleTunnelInput input) {
        // TODO Auto-generated method stub
        return null;
    }


    

}
