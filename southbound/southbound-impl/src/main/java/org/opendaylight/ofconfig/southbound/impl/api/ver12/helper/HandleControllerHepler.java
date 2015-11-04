/*
 * Copyright (c) 2015 ZTE, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl.api.ver12.helper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.OFConfigId;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.Switch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.logical.switches.SwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.Controllers;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.ControllersBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.Controller;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.ControllerBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.oflogicalswitchtype.controllers.ControllerKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.topology.rev150901.OfconfigLogicalSwitchAugmentation;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleControllersInput;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class HandleControllerHepler
        extends AbstractOfconfigVer12HandlerHelper<HandleControllersInput> {

    private final static Logger logger = LoggerFactory.getLogger(HandleControllerHepler.class);


    public HandleControllerHepler(MountPointService mountService, DataBroker dataBroker) {
        super(mountService, dataBroker);
    }

    @Override
    public HandleMode getRequestHandleMode(HandleControllersInput request) {
        // TODO Auto-generated method stub
        return request.getHandleMode();
    }

    @Override
    String getNetconfigId(HandleControllersInput request) {

        String logicalSwitchNodeId = request.getTopoLogicalSwitchNodeId();
        return this.getNetConfTopoNodeIdByLogicalSwitchNodeId(logicalSwitchNodeId);
    }

    @Override
    CapableSwitch mergeCapableSwitchAndMergeObject(CapableSwitch capableSwitch,
            HandleControllersInput request) {

        Optional<Switch> switchOptional = getTargetSwitchOptional(capableSwitch, request);


        capableSwitch = buildControllerList(switchOptional, capableSwitch);

        switchOptional = getTargetSwitchOptional(capableSwitch, request);

        fillControllerList(switchOptional.get().getControllers().getController(),
                request.getController());

        return capableSwitch;



    }



    @Override
    CapableSwitch deleteCapableSwitchAndMergeObject(CapableSwitch capableSwitch,
            HandleControllersInput request) {

        Optional<Switch> switchOptional = getTargetSwitchOptional(capableSwitch, request);

        capableSwitch = buildControllerList(switchOptional, capableSwitch);

        switchOptional = getTargetSwitchOptional(capableSwitch, request);

        delControllerList(switchOptional.get().getControllers().getController(),
                request.getController());

        return capableSwitch;
    }



    @Override
    CapableSwitch putCapableSwitchAndMergeObject(CapableSwitch capableSwitch,
            HandleControllersInput request) {

        Optional<Switch> switchOptional = getTargetSwitchOptional(capableSwitch, request);

        capableSwitch = buildControllerList(switchOptional, capableSwitch);

        switchOptional = getTargetSwitchOptional(capableSwitch, request);

        putControllerList(switchOptional.get().getControllers().getController(),
                request.getController());


        return capableSwitch;
    }



    private CapableSwitch buildControllerList(Optional<Switch> switchOptional,
            CapableSwitch capableSwitch) {
        Controllers controllers = switchOptional.get().getControllers();
        List<Controller> controllerList = null;
        if (controllers == null) {
            CapableSwitchBuilder capableSwitchbuilder = buildCapableSwitchBuilder(capableSwitch);

            capableSwitchbuilder.getLogicalSwitches().getSwitch().remove(switchOptional.get());

            SwitchBuilder swBuilder = buildSwitchBuilder(switchOptional.get());

            ControllersBuilder controllerBuilder = new ControllersBuilder();
            controllerList = Lists.newArrayList();
            controllerBuilder.setController(controllerList);

            swBuilder.setControllers(controllerBuilder.build());

            capableSwitchbuilder.getLogicalSwitches().getSwitch().add(swBuilder.build());

            capableSwitch = capableSwitchbuilder.build();
        }

        return capableSwitch;
    }



    private Optional<Switch> getTargetSwitchOptional(CapableSwitch capableSwitch,
            HandleControllersInput request) {
        String logicalSwitchNodeId = request.getTopoLogicalSwitchNodeId();
        Node node = this.getLogicalSwitchTopoNodeByNodeId(logicalSwitchNodeId);

        final OfconfigLogicalSwitchAugmentation logicalSwitchNode =
                node.getAugmentation(OfconfigLogicalSwitchAugmentation.class);

        List<Switch> logicalswitchs = capableSwitch.getLogicalSwitches().getSwitch();


        Optional<Switch> switchOptional =
                Iterators.tryFind(logicalswitchs.iterator(), new Predicate<Switch>() {

                    @Override
                    public boolean apply(Switch input) {
                        // TODO Auto-generated method stub
                        return input.getId().getValue()
                                .equals(logicalSwitchNode.getOfconfigLogicalSwitchAttributes()
                                        .getLogicalSwitch().getId().getValue());
                    }

                });

        if (!switchOptional.isPresent()) {
            throw new RuntimeException(
                    "logical swith dosen't exist,logical switch topology node id:"
                            + logicalSwitchNodeId);
        }

        return switchOptional;
    }


    private void fillControllerList(List<Controller> controllerList,
            List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.Controller> controller) {

        Map<OFConfigId, Controller> mergeMap = Maps.newHashMap();
        for (Controller ctl : controllerList) {
            mergeMap.put(ctl.getId(), ctl);
        }


        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.Controller paramCtl : controller) {

            OFConfigId id = paramCtl.getId();
            if (mergeMap.containsKey(id)) {
                continue;
            }

            ControllerBuilder builder = new ControllerBuilder();
            builder.setId(paramCtl.getId()).setIpAddress(paramCtl.getIpAddress())
                    .setKey(new ControllerKey(paramCtl.getId()))
                    .setLocalIpAddress(paramCtl.getLocalIpAddress()).setPort(paramCtl.getPort())
                    .setProtocol(paramCtl.getProtocol()).setState(paramCtl.getState());
            mergeMap.put(id, builder.build());
        }

        controllerList.clear();
        controllerList.addAll(mergeMap.values());
    }

    private void putControllerList(List<Controller> controller,
            List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.Controller> controller2) {

        controller.clear();

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.Controller paramCtl : controller2) {
            ControllerBuilder builder = new ControllerBuilder();
            builder.setId(paramCtl.getId()).setIpAddress(paramCtl.getIpAddress())
                    .setKey(new ControllerKey(paramCtl.getId()))
                    .setLocalIpAddress(paramCtl.getLocalIpAddress()).setPort(paramCtl.getPort())
                    .setProtocol(paramCtl.getProtocol()).setState(paramCtl.getState());
            controller.add(builder.build());
        }



    }

    private void delControllerList(List<Controller> controllerList,
            List<org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.Controller> controller2) {


        Iterator<Controller> controllerIterator = controllerList.iterator();
        while (controllerIterator.hasNext()) {
            Controller ctl = controllerIterator.next();
            for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.types.rev150901.ofconfig_controller.Controller paramCtl : controller2) {
                if (paramCtl.getId() != null) {
                    if (ctl.getId().equals(paramCtl.getId())) {
                        controllerIterator.remove();
                        break;
                    } else if (paramCtl.getIpAddress() != null && paramCtl.getPort() != null) {
                        if (paramCtl.getIpAddress().equals(ctl.getIpAddress())
                                && paramCtl.getPort().equals(ctl.getPort())) {
                            controllerIterator.remove();
                            break;
                        }
                    }
                }
            }
        }

    }


    private SwitchBuilder buildSwitchBuilder(Switch switch1) {

        SwitchBuilder builder = new SwitchBuilder();
        builder.setCapabilities(switch1.getCapabilities()).setControllers(switch1.getControllers())
                .setDatapathId(switch1.getDatapathId()).setId(switch1.getId())
                .setKey(switch1.getKey())
                .setLostConnectionBehavior(switch1.getLostConnectionBehavior())
                .setResources(switch1.getResources());

        return builder;
    }


    private CapableSwitchBuilder buildCapableSwitchBuilder(CapableSwitch capableSwitch) {

        CapableSwitchBuilder builder = new CapableSwitchBuilder();
        builder.setConfigVersion(capableSwitch.getConfigVersion()).setId(capableSwitch.getId())
                .setLogicalSwitches(capableSwitch.getLogicalSwitches())
                .setResources(capableSwitch.getResources());
        return builder;
    }


}
