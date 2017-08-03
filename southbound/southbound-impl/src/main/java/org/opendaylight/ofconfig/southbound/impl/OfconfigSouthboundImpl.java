/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.listener.NetconfTopoDataChangeListener;
import org.opendaylight.ofconfig.southbound.impl.topology.OfconfigInvTopoinitializer;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class OfconfigSouthboundImpl implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(OfconfigSouthboundImpl.class);

    private ListenerRegistration<DataChangeListener> netconfTopodclReg;
    private final MountPointService mountService;
    private final DataBroker dataBroker;

    public OfconfigSouthboundImpl(DataBroker dataBroker, MountPointService mountService) {
        this.mountService = mountService;
        this.dataBroker = dataBroker;
    }

    public void initTopoAndListener() {

        OfconfigInvTopoinitializer initializer = new OfconfigInvTopoinitializer();
        initializer.initializeOfconfigTopology(dataBroker,
                OfconfigConstants.OFCONFIG_CAPABLE_TOPOLOGY_ID, LogicalDatastoreType.OPERATIONAL);

        initializer.initializeOfconfigTopology(dataBroker,
                OfconfigConstants.OFCONFIG_LOGICAL_TOPOLOGY_ID, LogicalDatastoreType.OPERATIONAL);

        this.netconfTopodclReg =
                dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL,
                        OfconfigConstants.NETCONF_TOPO_IID.child(Node.class),
                        new NetconfTopoDataChangeListener(mountService, dataBroker),
                        DataChangeScope.SUBTREE);

        LOG.info("Ofconfig Southbound Impl Session Initiated");
    }



    @Override
    public void close() throws Exception {
        if (this.netconfTopodclReg != null) {
            this.netconfTopodclReg.close();
        }

    }
}
