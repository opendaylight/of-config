package org.opendaylight.ofconfig.southbound.impl.topology.impl.ofconfig12;

import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.ofconfig.southbound.impl.topology.OfconfigTopoHandler;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class Ofconfig12InventoryTopoHandler extends OfconfigTopoHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Ofconfig12InventoryTopoHandler.class);

    private CapableSwitchTopoNodeAddHelper capableSwitchTopoNodeAddHelper =
            new CapableSwitchTopoNodeAddHelper();

    private LogicalSwitchTopoNodeAddHelper logicalSwitchTopoNodeAddHelper =
            new LogicalSwitchTopoNodeAddHelper();


    @Override
    protected void addCapableSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig,
            WriteTransaction invTopoWriteTx) {

        capableSwitchTopoNodeAddHelper.addCapableSwitchTopoNodeAttributes(netconfNodeId,
                capableSwitchConfig, invTopoWriteTx);
    }

    @Override
    protected void addLogicalSwitchTopoNodeAttributes(NodeId netconfNodeId,
            Optional<CapableSwitch> capableSwitchConfig,
            WriteTransaction invTopoWriteTx) {


        logicalSwitchTopoNodeAddHelper.addLogicalSwitchTopoNodeAttributes(netconfNodeId,
                capableSwitchConfig, invTopoWriteTx);


    }

   

   



}
