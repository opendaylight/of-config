/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.listener;

import java.util.Map.Entry;


import com.google.common.base.Optional;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.ofconfig.southbound.impl.topology.OfconfigTopoHandler;
import org.opendaylight.ofconfig.southbound.impl.utils.OfconfigHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeConnectionStatus.ConnectionStatus;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class NetconfTopoDataChangeListener implements DataChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfTopoDataChangeListener.class);


    private OfconfigHelper helper = null;



    public NetconfTopoDataChangeListener(MountPointService mountService, DataBroker dataBroker) {
        super();
        this.helper = new OfconfigHelper(mountService, dataBroker);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.opendaylight.controller.md.sal.binding.api.DataChangeListener#onDataChanged(org.
     * opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent)
     */
    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        // https://bugs.opendaylight.org/show_bug.cgi?id=5303
        // this log message could be very large
        LOG.trace("OnDataChange, change: {}", change);
        try {
            // create
            for (Entry<InstanceIdentifier<?>, DataObject> entry : change.getCreatedData()
                    .entrySet()) {
                if (entry.getKey().getTargetType() == NetconfNode.class) {
                    NodeId nodeId = helper.getNodeId(entry.getKey());

                    // To determine whether the equipment is support ofconfig
                    helper.createOfconfigNode(nodeId);
                }
            }
            // update
            for (Entry<InstanceIdentifier<?>, DataObject> entry : change.getUpdatedData()
                    .entrySet()) {
                if (entry.getKey().getTargetType() == NetconfNode.class) {
                    NodeId nodeId = helper.getNodeId(entry.getKey());

                    // To determine whether it is device ofconfig
                    Optional<OfconfigTopoHandler> handlerOptional =
                            helper.getOfconfigInventoryTopoHandler(nodeId);
                    if (handlerOptional.isPresent()) {

                        // We have a ofconfig device
                        NetconfNode nnode = (NetconfNode) entry.getValue();
                        ConnectionStatus csts = nnode.getConnectionStatus();

                        switch (csts) {
                            case Connected: {

                                if (helper.isOfconfigDeviceNode(nnode)) {
                                    LOG.info("ofconfig device: {} is fully connected",
                                            nodeId.getValue());
                                }
                                break;
                            }
                            case Connecting: {

                                if (helper.isOfconfigDeviceNode(nnode)) {

                                    LOG.info("ofconfig device: {} was disconnected",
                                            nodeId.getValue());
                                }
                                break;
                            }
                            case UnableToConnect: {

                                if (helper.isOfconfigDeviceNode(nnode)) {
                                    LOG.info("ofconfig device: {} connection failed",
                                            nodeId.getValue());
                                    helper.destroyOfconfigNode(nodeId);

                                }
                                break;
                            }
                            default:
                                break;
                            
                        }
                        
                        

                    }
                }
            }
        } catch (Exception e) {
            LOG.error("OnDataChange, change: {} fail", change, e);
        }
    }



}
