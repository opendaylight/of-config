package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.impl.rev150901;

import org.opendaylight.ofconfig.southbound.impl.OdlOfconfigApiServiceImpl;
import org.opendaylight.ofconfig.southbound.impl.OdlOfconfigVer12ApiServiceImpl;
import org.opendaylight.ofconfig.southbound.impl.OfconfigSouthboundImpl;

public class OfconfigSouthboundModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.impl.rev150901.AbstractOfconfigSouthboundModule {
    public OfconfigSouthboundModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public OfconfigSouthboundModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.impl.rev150901.OfconfigSouthboundModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
       final  OfconfigSouthboundImpl ofconfigSBImpl = new OfconfigSouthboundImpl();
       final OdlOfconfigApiServiceImpl ofconfigBaseApiImpl = new OdlOfconfigApiServiceImpl();
       final OdlOfconfigVer12ApiServiceImpl ofconfigv12ApiImpl = new OdlOfconfigVer12ApiServiceImpl();
        
        
        getBrokerDependency().registerProvider(ofconfigSBImpl);
        getBrokerDependency().registerProvider(ofconfigBaseApiImpl);
        getBrokerDependency().registerProvider(ofconfigv12ApiImpl);
        
        return new AutoCloseable() {
            
            @Override
            public void close() throws Exception {
                ofconfigSBImpl.close();
                ofconfigBaseApiImpl.close();
                ofconfigv12ApiImpl.close();
            }
        };
        
    }

}
