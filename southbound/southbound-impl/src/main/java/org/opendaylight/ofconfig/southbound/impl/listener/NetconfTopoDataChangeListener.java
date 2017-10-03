/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.listener;

import com.google.common.base.Optional;
import java.util.Collection;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.ofconfig.southbound.impl.topology.OfconfigTopoHandler;
import org.opendaylight.ofconfig.southbound.impl.utils.OfconfigHelper;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNodeConnectionStatus.ConnectionStatus;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class NetconfTopoDataChangeListener implements DataTreeChangeListener<NetconfNode> {

    private static final Logger LOG = LoggerFactory.getLogger(NetconfTopoDataChangeListener.class);


    private OfconfigHelper helper = null;



    public NetconfTopoDataChangeListener(MountPointService mountService, DataBroker dataBroker) {
        super();
        this.helper = new OfconfigHelper(mountService, dataBroker);
    }

    @Override
    public void onDataTreeChanged(Collection<DataTreeModification<NetconfNode>> changes) {
        for (DataTreeModification<NetconfNode> modification : changes) {
            try {
                DataObjectModification<NetconfNode> rootNode = modification.getRootNode();
                NodeId nodeId = helper.getNodeId(modification.getRootPath().getRootIdentifier());
                switch (rootNode.getModificationType()) {
                    case WRITE:
                    case SUBTREE_MODIFIED:
                        if (rootNode.getDataBefore() == null) {
                            // To determine whether the equipment is support ofconfig
                            helper.createOfconfigNode(nodeId);
                        } else {
                            // To determine whether it is device ofconfig
                            Optional<OfconfigTopoHandler> handlerOptional =
                                    helper.getOfconfigInventoryTopoHandler(nodeId);
                            if (handlerOptional.isPresent()) {
                                // We have a ofconfig device
                                NetconfNode nnode = rootNode.getDataAfter();
                                ConnectionStatus csts = nnode.getConnectionStatus();
                                switch (csts) {
                                    case Connected: {
                                        if (helper.isOfconfigDeviceNode(nnode)) {
                                            LOG.info("ofconfig device: {} is fully connected",
                                                    nodeId.getValue());
                                            helper.updateOfconfigNode(nodeId);
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
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                LOG.error("onDataTreeChanged, change: {} fail", modification, e);
            }
        }
    }
}
