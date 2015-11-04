package org.opendaylight.ofconfig.southbound.impl.listener;

import java.util.ArrayList;
import java.util.List;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.ofconfig.southbound.impl.OfconfigConstants;
import org.opendaylight.ofconfig.southbound.impl.topology.OfconfigInventoryTopoHandler;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.Identifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class OfconfigListenerHelper {
    private ListenerRegistration<DataChangeListener> dclReg;
    private MountPointService mountService;
    private DataBroker dataBroker;

    private MdsalUtils mdsalUtils = new MdsalUtils();


    public OfconfigListenerHelper(MountPointService mountService, DataBroker dataBroker) {
        this.mountService = mountService;
        this.dataBroker = dataBroker;
    }



    public List<NodeId> getAllNetconfNodeIds() {

        List<NodeId> resultNodeIds = new ArrayList<>();

        InstanceIdentifier<Topology> path =
                InstanceIdentifier.create(NetworkTopology.class).child(Topology.class,
                        new TopologyKey(new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)));

        Topology netconfTopo = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, path, dataBroker);
        if (netconfTopo == null) {
            return resultNodeIds;
        }

        List<Node> netconfNodes = netconfTopo.getNode();

        resultNodeIds = Lists.transform(netconfNodes, new Function<Node, NodeId>() {
            @Override
            public NodeId apply(Node netconfNode) {
                return netconfNode.getNodeId();
            }

        });

        return resultNodeIds;
    }


    public NodeId getNodeId(final InstanceIdentifier<?> path) {
        for (InstanceIdentifier.PathArgument pathArgument : path.getPathArguments()) {
            if (pathArgument instanceof InstanceIdentifier.IdentifiableItem<?, ?>) {

                final Identifier key =
                        ((InstanceIdentifier.IdentifiableItem) pathArgument).getKey();
                if (key instanceof NodeKey) {
                    return ((NodeKey) key).getNodeId();
                }
            }
        }
        return null;
    }
    
    public Optional<NetconfNode> getNetconfNodeByNodeId(NodeId netconfNodeId){
        
        InstanceIdentifier<Node> path =
                InstanceIdentifier.create(NetworkTopology.class)
                        .child(Topology.class,
                                new TopologyKey(
                                        new TopologyId(OfconfigConstants.NETCONF_TOPOLOGY_ID)))
                        .child(Node.class, new NodeKey(netconfNodeId));

        NetconfNode nnode = mdsalUtils.read(LogicalDatastoreType.OPERATIONAL, path, dataBroker)
                .getAugmentation(NetconfNode.class);
        if (nnode != null) {
           return Optional.of(nnode);
            
        }
        return Optional.absent();
        
        
    }


    public Optional<OfconfigInventoryTopoHandler> getOfconfigInventoryTopoHandler(NodeId netconfNodeId) {

        Optional<NetconfNode> nodeOptional=getNetconfNodeByNodeId(netconfNodeId);
        
        if (nodeOptional.isPresent()) {
            List<String> capabilities = nodeOptional.get().getAvailableCapabilities().getAvailableCapability();

            if (Iterables.contains(capabilities, OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY)
                    && !Iterables.contains(capabilities, OfconfigConstants.ODL_CONFIG_CAPABILITY)) {

                return Optional.of(OfconfigInventoryTopoHandler
                        .getHandlerInstance(OfconfigConstants.OF_CONFIG_VERSION_12_CAPABILITY));
            }
        }

        return Optional.absent();
    }
}
