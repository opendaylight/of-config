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
import org.opendaylight.ofconfig.southbound.impl.api.HandlerDispatcher;
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

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class OdlOfconfigVer12ApiServiceImpl implements OdlOfconfigVer12ApiService, AutoCloseable {

    private final HandlerDispatcher handlerDispatcher = new HandlerDispatcher();
    private final MountPointService mountService;
    private final DataBroker dataBroker;

    public OdlOfconfigVer12ApiServiceImpl(DataBroker dataBroker, MountPointService mountService) {
        this.mountService = mountService;
        this.dataBroker = dataBroker;
    }

    @Override
    public void close() {

    }

    public void init() {
        this.handlerDispatcher.init(mountService, dataBroker);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901
     * .OdlOfconfigVer12ApiService#handleOwnedCert(org.opendaylight.yang.gen.v1.urn.opendaylight.
     * params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInput)
     */
    @Override
    public Future<RpcResult<Void>> handleOwnedCert(HandleOwnedCertInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input, HandleOwnedCertInput.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901
     * .OdlOfconfigVer12ApiService#handleFlowtable(org.opendaylight.yang.gen.v1.urn.opendaylight.
     * params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleFlowtableInput)
     */
    @Override
    public Future<RpcResult<Void>> handleFlowtable(HandleFlowtableInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input, HandleFlowtableInput.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901
     * .OdlOfconfigVer12ApiService#handleExtCert(org.opendaylight.yang.gen.v1.urn.opendaylight.
     * params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleExtCertInput)
     */
    @Override
    public Future<RpcResult<Void>> handleExtCert(HandleExtCertInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input, HandleExtCertInput.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901
     * .OdlOfconfigVer12ApiService#handleControllers(org.opendaylight.yang.gen.v1.urn.opendaylight.
     * params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInput)
     */
    @Override
    public Future<RpcResult<Void>> handleControllers(HandleControllersInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input, HandleControllersInput.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901
     * .OdlOfconfigVer12ApiService#handleTunnel(org.opendaylight.yang.gen.v1.urn.opendaylight.params
     * .xml.ns.yang.ofconfig.ver12.api.rev150901.HandleTunnelInput)
     */
    @Override
    public Future<RpcResult<Void>> handleTunnel(HandleTunnelInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input, HandleTunnelInput.class);
    }


    @Override
    public Future<RpcResult<Void>> handleQueueResource(HandleQueueResourceInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input,
                HandleQueueResourceInput.class);
    }


    @Override
    public Future<RpcResult<Void>> handleLogicSwitch(HandleLogicSwitchInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input, HandleLogicSwitchInput.class);
    }


    @Override
    public Future<RpcResult<Void>> handlePortResource(HandlePortResourceInput input) {
        return this.handlerDispatcher.dispatchToHandlerHelper(input, HandlePortResourceInput.class);
    }
}
