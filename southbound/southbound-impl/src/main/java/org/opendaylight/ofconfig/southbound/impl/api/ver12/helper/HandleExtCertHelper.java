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
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitch;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitchBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.Resources;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.ResourcesBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.ExternalCertificate;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.ExternalCertificateBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.ExternalCertificateKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleExtCertInput;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class HandleExtCertHelper extends AbstractOfconfigVer12HandlerHelper<HandleExtCertInput> {

    public HandleExtCertHelper(MountPointService mountService, DataBroker dataBroker) {
        super(mountService, dataBroker);
    }

    @Override
    public HandleMode getRequestHandleMode(HandleExtCertInput request) {
        // TODO Auto-generated method stub
        return request.getHandleMode();
    }

    @Override
    String getNetconfigTopoNodeId(HandleExtCertInput request) {
        // TODO Auto-generated method stub
        return request.getTopoCapableSwitchNodeId();
    }

    @Override
    CapableSwitch mergeCapableSwitch(CapableSwitch capableSwitch, HandleExtCertInput request) {

        Resources resources = capableSwitch.getResources();
        if (resources == null) {
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources = capableSwitch.getResources();
        }

        List<ExternalCertificate> extCertificateList = resources.getExternalCertificate();

        if (extCertificateList == null) {
            capableSwitch = buildCapableSwitchResourcesExtCertificateList(capableSwitch);
            extCertificateList = capableSwitch.getResources().getExternalCertificate();
        }

        Map<Uri, ExternalCertificate> mergeMap = Maps.newHashMap();
        for (ExternalCertificate extCertificate : extCertificateList) {
            mergeMap.put(extCertificate.getResourceId(), extCertificate);
        }

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_ext_cert.ExternalCertificate paramCertificate : request
                .getExternalCertificate()) {
            Uri paramUri = paramCertificate.getResourceId();
            if (mergeMap.containsKey(paramUri)) {
                continue;
            }

            ExternalCertificateBuilder builder = new ExternalCertificateBuilder();

            builder.setKey(new ExternalCertificateKey(paramCertificate.getKey().getResourceId()))
                    .setCertificate(paramCertificate.getCertificate())
                    .setResourceId(paramCertificate.getResourceId());


            extCertificateList.add(builder.build());

        }
        return capableSwitch;
    }



    @Override
    CapableSwitch deleteCapableSwitch(CapableSwitch capableSwitch, HandleExtCertInput request) {

        Resources resources = capableSwitch.getResources();
        CapableSwitch returnCapableSwitch = capableSwitch;
        if (resources == null) {
            return capableSwitch;
        }

        List<ExternalCertificate> extCertificateList = resources.getExternalCertificate();

        if (extCertificateList == null) {
            return capableSwitch;
        }


        Map<Uri, ExternalCertificate> mergeMap = Maps.newHashMap();
        for (ExternalCertificate extCertificate : extCertificateList) {
            mergeMap.put(extCertificate.getResourceId(), extCertificate);
        }

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_ext_cert.ExternalCertificate paramCertificate : request
                .getExternalCertificate()) {
            Uri paramUri = paramCertificate.getResourceId();
            mergeMap.remove(paramUri);
        }

        extCertificateList.clear();

        extCertificateList.addAll(mergeMap.values());


        return capableSwitch;
    }

    @Override
    CapableSwitch putCapableSwitch(CapableSwitch capableSwitch, HandleExtCertInput request) {

        Resources resources = capableSwitch.getResources();
        CapableSwitch returnCapableSwitch = capableSwitch;
        if (resources == null) {
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources = capableSwitch.getResources();
        }

        List<ExternalCertificate> extCertificateList = resources.getExternalCertificate();

        if (extCertificateList == null) {
            capableSwitch = buildCapableSwitchResourcesExtCertificateList(capableSwitch);
            extCertificateList = capableSwitch.getResources().getExternalCertificate();
        }

        extCertificateList.clear();

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_ext_cert.ExternalCertificate paramCertificate : request
                .getExternalCertificate()) {

            ExternalCertificateBuilder builder = new ExternalCertificateBuilder();

            builder.setKey(new ExternalCertificateKey(paramCertificate.getKey().getResourceId()))
                    .setCertificate(paramCertificate.getCertificate())
                    .setResourceId(paramCertificate.getResourceId());


            extCertificateList.add(builder.build());

        }



        return capableSwitch;
    }


    private CapableSwitch buildCapableSwitchResources(CapableSwitch capableSwitch) {
        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion())
                .setLogicalSwitches(capableSwitch.getLogicalSwitches());

        ResourcesBuilder resBuilder = new ResourcesBuilder();

        List<ExternalCertificate> extCertificateList = Lists.newArrayList();

        resBuilder.setExternalCertificate(extCertificateList);


        cpswBuilder.setResources(resBuilder.build());

        return cpswBuilder.build();
    }

    private CapableSwitch buildCapableSwitchResourcesExtCertificateList(
            CapableSwitch capableSwitch) {

        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion())
                .setLogicalSwitches(capableSwitch.getLogicalSwitches());

        ResourcesBuilder resBuilder = new ResourcesBuilder();

        List<ExternalCertificate> extCertificateList = Lists.newArrayList();

        resBuilder.setOwnedCertificate(capableSwitch.getResources().getOwnedCertificate())
                .setExternalCertificate(extCertificateList)
                .setFlowTable(capableSwitch.getResources().getFlowTable())
                .setPort(capableSwitch.getResources().getPort())
                .setQueue(capableSwitch.getResources().getQueue());

        cpswBuilder.setResources(resBuilder.build());

        return cpswBuilder.build();
    }

}
