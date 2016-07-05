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
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.Resources;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.ResourcesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.OwnedCertificate;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.Port;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.PortBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.PortKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandlePortResourceInput;


/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class HandlePortResourceHelper
        extends AbstractOfconfigVer12HandlerHelper<HandlePortResourceInput> {

    public HandlePortResourceHelper(MountPointService mountService, DataBroker dataBroker) {
        super(mountService, dataBroker);
    }

    @Override
    public HandleMode getRequestHandleMode(HandlePortResourceInput request) {
        // TODO Auto-generated method stub
        return request.getHandleMode();
    }

    @Override
    String getNetconfigTopoNodeId(HandlePortResourceInput request) {
        // TODO Auto-generated method stub
        return request.getTopoCapableSwitchNodeId();
    }

    @Override
    CapableSwitch mergeCapableSwitch(CapableSwitch capableSwitch, HandlePortResourceInput request) {


        Resources resources = capableSwitch.getResources();
        if (resources == null) {
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources = capableSwitch.getResources();
        }



        List<Port> ports = resources.getPort();

        if (ports == null) {
            capableSwitch = buildCapableSwitchResourcesPortList(capableSwitch);
            ports = capableSwitch.getResources().getPort();
        }

        Map<String, Port> mergeMap = Maps.newHashMap();
        for (Port port : ports) {
            mergeMap.put(port.getName(), port);
        }

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_port_resource.Port paramPort : request
                .getPort()) {
            String paramName = paramPort.getName();
            if (mergeMap.containsKey(paramName)) {
                continue;
            }

            PortBuilder builder = new PortBuilder();

            builder.setConfiguration(paramPort.getConfiguration())
                    .setCurrentRate(paramPort.getCurrentRate()).setFeatures(paramPort.getFeatures())
                    .setKey(new PortKey(paramPort.getResourceId())).setMaxRate(paramPort.getMaxRate())
                    .setName(paramPort.getName()).setNumber(paramPort.getNumber())
                    .setRequestedNumber(paramPort.getRequestedNumber())
                    .setState(paramPort.getState()).setTunnelType(paramPort.getTunnelType());
            ports.add(builder.build());

        }
        return capableSwitch;

    }



    @Override
    CapableSwitch deleteCapableSwitch(CapableSwitch capableSwitch,
            HandlePortResourceInput request) {

        Resources resources = capableSwitch.getResources();
        if (resources == null) {
            return capableSwitch;
        }



        List<Port> ports = resources.getPort();

        if (ports == null) {
            return capableSwitch;
        }

        Map<String, Port> mergeMap = Maps.newHashMap();
        for (Port port : ports) {
            mergeMap.put(port.getName(), port);
        }

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_port_resource.Port paramPort : request
                .getPort()) {
            String paramName = paramPort.getName();

            mergeMap.remove(paramName);
        }

        ports.clear();


        ports.addAll(mergeMap.values());

        return capableSwitch;

    }

    @Override
    CapableSwitch putCapableSwitch(CapableSwitch capableSwitch, HandlePortResourceInput request) {
        Resources resources = capableSwitch.getResources();
        if (resources == null) {
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources = capableSwitch.getResources();
        }



        List<Port> ports = resources.getPort();

        if (ports == null) {
            capableSwitch = buildCapableSwitchResourcesPortList(capableSwitch);
            ports = capableSwitch.getResources().getPort();
        }


        ports.clear();

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_port_resource.Port paramPort : request
                .getPort()) {
            PortBuilder builder = new PortBuilder();

            builder.setConfiguration(paramPort.getConfiguration())
                    .setCurrentRate(paramPort.getCurrentRate()).setFeatures(paramPort.getFeatures())
                    .setKey(new PortKey(paramPort.getResourceId())).setMaxRate(paramPort.getMaxRate())
                    .setName(paramPort.getName()).setNumber(paramPort.getNumber())
                    .setRequestedNumber(paramPort.getRequestedNumber())
                    .setState(paramPort.getState()).setTunnelType(paramPort.getTunnelType());
            ports.add(builder.build());

        }
        return capableSwitch;
    }

    private CapableSwitch buildCapableSwitchResourcesPortList(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion())
                .setLogicalSwitches(capableSwitch.getLogicalSwitches());

        ResourcesBuilder resBuilder = new ResourcesBuilder();

        List<Port> portList = Lists.newArrayList();

        resBuilder.setOwnedCertificate(capableSwitch.getResources().getOwnedCertificate())
                .setExternalCertificate(capableSwitch.getResources().getExternalCertificate())
                .setFlowTable(capableSwitch.getResources().getFlowTable()).setPort(portList)
                .setQueue(capableSwitch.getResources().getQueue());

        cpswBuilder.setResources(resBuilder.build());

        return cpswBuilder.build();
    }


    private CapableSwitch buildCapableSwitchResources(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion())
                .setLogicalSwitches(capableSwitch.getLogicalSwitches());

        ResourcesBuilder resBuilder = new ResourcesBuilder();

        List<OwnedCertificate> ownedCertificateList = Lists.newArrayList();

        resBuilder.setOwnedCertificate(ownedCertificateList);

        cpswBuilder.setResources(resBuilder.build());

        return cpswBuilder.build();
    }

}
