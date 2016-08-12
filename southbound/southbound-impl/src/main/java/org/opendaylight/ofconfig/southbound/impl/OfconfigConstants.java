/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class OfconfigConstants {


    public static final TopologyId OFCONFIG_CAPABLE_TOPOLOGY_ID =
            new TopologyId(new Uri("ofconfig-capable:1"));

    public static final TopologyId OFCONFIG_LOGICAL_TOPOLOGY_ID =
            new TopologyId(new Uri("ofconfig-logical:1"));

    public final static String OF_CONFIG_VERSION_12_CAPABILITY =
            "(urn:onf:config:yang?revision=2015-02-11)of-config";

    public final static String OF_CONFIG_VERSION_12_CAPABILITY_OVS =
            "(urn:onf:config:yang?revision=2015-02-11)of-config";

    public final static String ODL_CONFIG_CAPABILITY =
            "(urn:opendaylight:params:xml:ns:yang:ofconfig:southbound:impl?"
            + "revision=2015-09-01)ofconfig-southbound-impl";

    public final static String OFCONFIG_URI_PREFIX = "ofconfig";

    public static final InstanceIdentifier<Topology> NETCONF_TOPO_IID =
            InstanceIdentifier.create(NetworkTopology.class).child(Topology.class,
                    new TopologyKey(new TopologyId(TopologyNetconf.QNAME.getLocalName())));

    public static final InstanceIdentifier<Topology> OFCONFIG_CAPABLE_TOPO_IID =
            InstanceIdentifier.create(NetworkTopology.class).child(Topology.class,
                    new TopologyKey(OFCONFIG_CAPABLE_TOPOLOGY_ID));

    public static final InstanceIdentifier<Topology> OFCONFIG_LOGICAL_TOPO_IID =
            InstanceIdentifier.create(NetworkTopology.class).child(Topology.class,
                    new TopologyKey(OFCONFIG_LOGICAL_TOPOLOGY_ID));


    public static final String NETCONF_TOPOLOGY_ID = "topology-netconf";

}
