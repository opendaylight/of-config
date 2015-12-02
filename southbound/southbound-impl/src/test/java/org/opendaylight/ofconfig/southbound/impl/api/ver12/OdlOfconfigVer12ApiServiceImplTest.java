package org.opendaylight.ofconfig.southbound.impl.api.ver12;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.ofconfig.southbound.impl.OFconfigTestBase;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.KeyValue.KeyType;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.ofownedcertificatetype.PrivateKeyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_owned_cert.OwnedCertificate;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_owned_cert.OwnedCertificateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_owned_cert.OwnedCertificateKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yangtools.yang.common.RpcResult;

import com.google.common.collect.Lists;

public class OdlOfconfigVer12ApiServiceImplTest extends OFconfigTestBase{

    private OdlOfconfigVer12ApiServiceImpl odlOfconfigVer12ApiServiceImpl;
    
    private NodeId netconfNodeId = new NodeId("test-netconf-node");
    
    @Before
    public void setUp(){
        super.setUp();
        ProviderContext providerContext=mock(ProviderContext.class);
        when(providerContext.getSALService(DataBroker.class)).thenReturn(this.databroker);
        initMountService(netconfNodeId);
        when(providerContext.getSALService(MountPointService.class)).thenReturn(this.mountService);
        
        odlOfconfigVer12ApiServiceImpl = new OdlOfconfigVer12ApiServiceImpl();
        odlOfconfigVer12ApiServiceImpl.onSessionInitiated(providerContext);
        
    }

    @Test
    public void test_handle_OwnedCert() throws Exception {
        
        initNetConfTopo(netconfNodeId);
        initDataStore(netconfNodeId);
        initOfConfigCapableSwitchTopo(netconfNodeId);
        initOfConfigLogicalSwitchTopo();
        
        List<OwnedCertificate> ownedCertificates = Lists.newArrayList();
        
        PrivateKeyBuilder privateKeyBuilder = new PrivateKeyBuilder();
        privateKeyBuilder.setKeyType(KeyType.DSA).setKeyData("testPrivateKey");
        
        OwnedCertificateBuilder ownedCertificateBuilder = new OwnedCertificateBuilder();
        ownedCertificateBuilder.setResourceId(Uri.getDefaultInstance("test-owned-Certificate"))
        .setCertificate("test-Certificate").setKey(new OwnedCertificateKey(Uri.getDefaultInstance("test-owned-Certificate")))
        .setPrivateKey(privateKeyBuilder.build());
        
        
        ownedCertificates.add(ownedCertificateBuilder.build());
        HandleOwnedCertInputBuilder inputBuilder = new HandleOwnedCertInputBuilder();
        inputBuilder.setHandleMode(HandleMode.Put)
        .setTopoCapableSwitchNodeId(netconfNodeId.getValue()).setOwnedCertificate(ownedCertificates);
        
        
        RpcResult<Void> rpcResult = odlOfconfigVer12ApiServiceImpl.handleOwnedCert(inputBuilder.build()).get();
        
        assertTrue(rpcResult.isSuccessful());
        
        
        CapableSwitch deviceCapableSwitch = this.capableSwitchRef.get();
        
        assertEquals("test-Certificate", deviceCapableSwitch.getResources().getOwnedCertificate().get(0).getCertificate());
        
        
    }

    @Test
    public void test() {
        
    }
    
    
    
    
    
    

}
