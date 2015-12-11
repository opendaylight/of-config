package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.base.api.impl.rev150901;

import org.opendaylight.ofconfig.southbound.impl.OdlOfconfigApiServiceImpl;

public class OfconfigSouthboundBaseApiModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.base.api.impl.rev150901.AbstractOfconfigSouthboundBaseApiModule {
    public OfconfigSouthboundBaseApiModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public OfconfigSouthboundBaseApiModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.southbound.base.api.impl.rev150901.OfconfigSouthboundBaseApiModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        
        final OdlOfconfigApiServiceImpl ofconfigBaseApiImpl = new OdlOfconfigApiServiceImpl();
         
        getBrokerDependency().registerProvider(ofconfigBaseApiImpl);

         
         return new AutoCloseable() {
             
             @Override
             public void close() throws Exception {
                 ofconfigBaseApiImpl.close();

             }
         };
    }

}
