/*
 * Copyright (c) 2015 ZTE, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.ofconfig.southbound.impl.listener.NetconfTopoDataChangeListener;
import org.opendaylight.ofconfig.southbound.impl.topology.OfconfigInvTopoinitializer;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class OfconfigSouthboundImpl
        implements BindingAwareProvider, AutoCloseable{

    private static final Logger LOG = LoggerFactory.getLogger(OfconfigSouthboundImpl.class);

    private ListenerRegistration<DataChangeListener> netconfTopodclReg;
    private ListenerRegistration<DataChangeListener> ofconfigCapableSwitchTopodclReg;
    private ListenerRegistration<DataChangeListener> ofconfigLogicalSwitchTopodclReg;
    private MountPointService mountService;
    private DataBroker dataBroker;
    private BindingAwareBroker bindingBroker;


    private MdsalUtils mdsalUtils = new MdsalUtils();


    @Override
    public void onSessionInitiated(ProviderContext session) {

        LOG.info("Ofconfig Southbound Impl Session Initiated");

        // Get references to the data broker and mount service
        setMountService(session.getSALService(MountPointService.class));
        setDataBroker(session.getSALService(DataBroker.class));

        initTopoAndListener();


    }
    
    public void initTopoAndListener(){

        OfconfigInvTopoinitializer initializer = new OfconfigInvTopoinitializer();
        initializer.initializeOfconfigTopology(dataBroker,
                OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID, LogicalDatastoreType.OPERATIONAL);

        initializer.initializeOfconfigTopology(dataBroker,
                OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID, LogicalDatastoreType.OPERATIONAL);

        // Register ourselves as data change listener for changes on Netconf
        // nodes. Netconf nodes are accessed via "Netconf Topology" - a special
        // topology that is created by the system infrastructure. It contains
        // all Netconf nodes the Netconf connector knows about. NETCONF_TOPO_IID
        // is equivalent to the following URL:
        // .../restconf/operational/network-topology:network-topology/topology/topology-netconf
        if (dataBroker != null) {
            this.netconfTopodclReg =
                    dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL,
                            OfconfigConstants.NETCONF_TOPO_IID.child(Node.class),
                            new NetconfTopoDataChangeListener(mountService, dataBroker),
                            DataChangeScope.SUBTREE);
        }
    }



    @Override
    public void close() throws Exception {
        if (this.netconfTopodclReg != null) {
            this.netconfTopodclReg.close();
        }
        if (this.ofconfigCapableSwitchTopodclReg != null) {
            this.ofconfigCapableSwitchTopodclReg.close();
        }
        if (this.ofconfigLogicalSwitchTopodclReg != null) {
            this.ofconfigLogicalSwitchTopodclReg.close();
        }

    }

 

    public void setMountService(MountPointService mountService) {
        this.mountService = mountService;
    }



    public void setDataBroker(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    

}
