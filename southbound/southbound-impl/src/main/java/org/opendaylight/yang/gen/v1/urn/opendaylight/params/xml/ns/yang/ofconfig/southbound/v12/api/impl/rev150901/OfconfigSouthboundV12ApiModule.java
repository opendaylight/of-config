/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.v12.api.impl.rev150901;

import org.opendaylight.ofconfig.southbound.impl.api.ver12.OdlOfconfigVer12ApiServiceImpl;

public class OfconfigSouthboundV12ApiModule extends
        org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.v12.api.impl.rev150901.AbstractOfconfigSouthboundV12ApiModule {
    public OfconfigSouthboundV12ApiModule(
            org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public OfconfigSouthboundV12ApiModule(
            org.opendaylight.controller.config.api.ModuleIdentifier identifier,
            org.opendaylight.controller.config.api.DependencyResolver dependencyResolver,
            org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.v12.api.impl.rev150901.OfconfigSouthboundV12ApiModule oldModule,
            java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        final OdlOfconfigVer12ApiServiceImpl ofconfigv12ApiImpl =
                new OdlOfconfigVer12ApiServiceImpl();

        getBrokerDependency().registerProvider(ofconfigv12ApiImpl);

        return new AutoCloseable() {

            @Override
            public void close() throws Exception {
                ofconfigv12ApiImpl.close();
            }
        };
    }

}
