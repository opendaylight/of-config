package org.opendaylight.ofconfig.southbound.impl.inventory.impl.ofconfig12;

import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.ofconfig.southbound.impl.inventory.OfconfigInventoryTopoHandler;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class Ofconfig12InventoryTopoHandler extends OfconfigInventoryTopoHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Ofconfig12InventoryTopoHandler.class);

    private CapableSwitchTopoNodeAddHelper capableSwitchTopoNodeAddHelper =
            new CapableSwitchTopoNodeAddHelper();

    private LogicalSwitchTopoNodeAddHelper logicalSwitchTopoNodeAddHelper =
            new LogicalSwitchTopoNodeAddHelper();

    private CapableSwitchInventoryNodeAddHelper capableSwitchInventoryNodeAddHelper =
            new CapableSwitchInventoryNodeAddHelper();
    private LogicalSwitchInventoryNodeAddHelper logicalSwitchInventoryNodeAddHelper =
            new LogicalSwitchInventoryNodeAddHelper();

    @Override
    protected void addCapableSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {

        capableSwitchTopoNodeAddHelper.addCapableSwitchTopoNodeAttributes(netconfNodeId,
                capableSwitchConfig, ofconfigNodeReadTx, invTopoWriteTx);
    }

    @Override
    protected void addLogicalSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {


        logicalSwitchTopoNodeAddHelper.addLogicalSwitchTopoNodeAttributes(netconfNodeId,
                capableSwitchConfig, ofconfigNodeReadTx, invTopoWriteTx);


    }

    @Override
    protected void addCapableSwitchInventoryNodeAttributes(NodeId netconfNodeId,NetconfNode netconfNode,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {

        capableSwitchInventoryNodeAddHelper.addCapableSwitchInventoryNodeAttributes(netconfNodeId,netconfNode,
                capableSwitchConfig, ofconfigNodeReadTx, invTopoWriteTx);



    }

    @Override
    protected void addLogicalSwitchInventoryNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig, ReadOnlyTransaction ofconfigNodeReadTx,
            WriteTransaction invTopoWriteTx) {
        logicalSwitchInventoryNodeAddHelper.addLogicalSwitchInventoryNodeAttributes(netconfNodeId,
                capableSwitchConfig, ofconfigNodeReadTx, invTopoWriteTx);

    }



}
