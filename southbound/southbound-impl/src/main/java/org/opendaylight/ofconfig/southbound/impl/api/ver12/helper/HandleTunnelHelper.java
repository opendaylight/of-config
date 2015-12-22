/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.api.ver12.helper;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.Resources;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.ResourcesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.Port;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.PortBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev150211.capableswitchtype.resources.PortKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleTunnelInput;



public class HandleTunnelHelper extends AbstractOfconfigVer12HandlerHelper<HandleTunnelInput> {

    public HandleTunnelHelper(MountPointService mountService, DataBroker dataBroker) {
        super(mountService, dataBroker);
    }

    @Override
    public HandleMode getRequestHandleMode(HandleTunnelInput request) {
        // TODO Auto-generated method stub
        return request.getHandleMode();
    }

    @Override
    String getNetconfigTopoNodeId(HandleTunnelInput request) {
        // TODO Auto-generated method stub
        return request.getTopoCapableSwitchNodeId();
    }

    @Override
    CapableSwitch mergeCapableSwitch(CapableSwitch capableSwitch, HandleTunnelInput request) {


        Resources resources = capableSwitch.getResources();
        if (resources == null) {
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources = capableSwitch.getResources();
        }

        List<Port> portList = resources.getPort();
        if (portList == null) {
            capableSwitch = buildCapableSwitchResourcesPortList(capableSwitch);
            portList = capableSwitch.getResources().getPort();
        }

        Map<String, Port> mergeMap = Maps.newHashMap();
        for (Port port : portList) {
            mergeMap.put(port.getName(), port);
        }

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12
                .api.types.rev150901.ofconfig_tunnel.Port paramPort : request
                .getPort()) {
            String paramName = paramPort.getName();
            if (mergeMap.containsKey(paramName)) {
                continue;
            }

            PortBuilder builder = new PortBuilder();

            builder.setConfiguration(paramPort.getConfiguration())
                    .setCurrentRate(paramPort.getCurrentRate()).setFeatures(paramPort.getFeatures())
                    .setKey(new PortKey(paramPort.getName())).setMaxRate(paramPort.getMaxRate())
                    .setName(paramPort.getName()).setNumber(paramPort.getNumber())
                    .setRequestedNumber(paramPort.getRequestedNumber())
                    .setState(paramPort.getState()).setTunnelType(paramPort.getTunnelType());

            portList.add(builder.build());

        }
        return capableSwitch;
    }



    @Override
    CapableSwitch deleteCapableSwitch(CapableSwitch capableSwitch, HandleTunnelInput request) {

        Resources resources = capableSwitch.getResources();
        if (resources == null) {
            return capableSwitch;
        }

        List<Port> portList = resources.getPort();
        if (portList == null) {
            return capableSwitch;
        }

        Map<String, Port> mergeMap = Maps.newHashMap();
        for (Port port : portList) {
            mergeMap.put(port.getName(), port);
        }

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_tunnel.Port paramPort : request
                .getPort()) {

            String name = paramPort.getName();

            mergeMap.remove(name);
        }

        portList.clear();

        portList.addAll(mergeMap.values());

        return capableSwitch;

    }

    @Override
    CapableSwitch putCapableSwitch(CapableSwitch capableSwitch, HandleTunnelInput request) {

        Resources resources = capableSwitch.getResources();
        if (resources == null) {
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources = capableSwitch.getResources();
        }

        List<Port> portList = resources.getPort();
        if (portList == null) {
            capableSwitch = buildCapableSwitchResourcesPortList(capableSwitch);
            portList = capableSwitch.getResources().getPort();
        }

        portList.clear();

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_tunnel.Port paramPort : request
                .getPort()) {

            PortBuilder builder = new PortBuilder();

            builder.setConfiguration(paramPort.getConfiguration())
                    .setCurrentRate(paramPort.getCurrentRate()).setFeatures(paramPort.getFeatures())
                    .setKey(new PortKey(paramPort.getName())).setMaxRate(paramPort.getMaxRate())
                    .setName(paramPort.getName()).setNumber(paramPort.getNumber())
                    .setRequestedNumber(paramPort.getRequestedNumber())
                    .setState(paramPort.getState()).setTunnelType(paramPort.getTunnelType());

            portList.add(builder.build());

        }

        return capableSwitch;

    }


    private CapableSwitch buildCapableSwitchResources(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion())
                .setLogicalSwitches(capableSwitch.getLogicalSwitches());

        ResourcesBuilder resBuilder = new ResourcesBuilder();

        List<Port> protList = Lists.newArrayList();

        resBuilder.setPort(protList);

        cpswBuilder.setResources(resBuilder.build());

        return cpswBuilder.build();
    }


    private CapableSwitch buildCapableSwitchResourcesPortList(CapableSwitch capableSwitch) {

        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion())
                .setLogicalSwitches(capableSwitch.getLogicalSwitches());

        ResourcesBuilder resBuilder = new ResourcesBuilder();

        List<Port> portList = Lists.newArrayList();

        resBuilder.setFlowTable(capableSwitch.getResources().getFlowTable())
                .setExternalCertificate(capableSwitch.getResources().getExternalCertificate())
                .setOwnedCertificate(capableSwitch.getResources().getOwnedCertificate())
                .setPort(portList).setQueue(capableSwitch.getResources().getQueue());

        cpswBuilder.setResources(resBuilder.build());

        return cpswBuilder.build();
    }



}
