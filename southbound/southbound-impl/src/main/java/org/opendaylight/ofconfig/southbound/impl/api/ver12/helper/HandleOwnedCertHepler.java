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
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.OwnedCertificate;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.OwnedCertificateBuilder;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.capableswitchtype.resources.OwnedCertificateKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.api.rev150901.HandleOwnedCertInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class HandleOwnedCertHepler
        extends AbstractOfconfigVer12HandlerHelper<HandleOwnedCertInput> {

    private final static Logger logger = LoggerFactory.getLogger(HandleOwnedCertHepler.class);


    public HandleOwnedCertHepler(MountPointService mountService, DataBroker dataBroker) {
        super(mountService, dataBroker);

    }

    @Override
    public HandleMode getRequestHandleMode(HandleOwnedCertInput request) {
        return request.getHandleMode();
    }

    @Override
    String getNetconfigTopoNodeId(HandleOwnedCertInput request) {
        // TODO Auto-generated method stub
        return request.getTopoCapableSwitchNodeId();
    }

    @Override
    CapableSwitch mergeCapableSwitch(CapableSwitch capableSwitch, HandleOwnedCertInput request) {

        Resources resources = capableSwitch.getResources();
        if (resources == null) {
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources = capableSwitch.getResources();
        }

        List<OwnedCertificate> ownedCertificateList = resources.getOwnedCertificate();

        if (ownedCertificateList == null) {
            capableSwitch = buildCapableSwitchResourcesOwnedCertificateList(capableSwitch);
            ownedCertificateList = capableSwitch.getResources().getOwnedCertificate();
        }

        Map<Uri, OwnedCertificate> mergeMap = Maps.newHashMap();
        for (OwnedCertificate ownedCertificate : ownedCertificateList) {
            mergeMap.put(ownedCertificate.getResourceId(), ownedCertificate);
        }

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_owned_cert.OwnedCertificate paramCertificate : request
                .getOwnedCertificate()) {
            Uri paramUri = paramCertificate.getResourceId();
            if (mergeMap.containsKey(paramUri)) {
                continue;
            }

            OwnedCertificateBuilder builder = new OwnedCertificateBuilder();

            builder.setKey(new OwnedCertificateKey(paramCertificate.getKey().getResourceId()))
                    .setCertificate(paramCertificate.getCertificate())
                    .setResourceId(paramCertificate.getResourceId());


            ownedCertificateList.add(builder.build());

        }
        return capableSwitch;
    }



    @Override
    CapableSwitch deleteCapableSwitch(CapableSwitch capableSwitch, HandleOwnedCertInput request) {


        Resources resources = capableSwitch.getResources();
        CapableSwitch returnCapableSwitch = capableSwitch;
        if (resources == null) {
            return capableSwitch;
        }

        List<OwnedCertificate> ownedCertificateList = resources.getOwnedCertificate();

        if (ownedCertificateList == null) {
            return capableSwitch;
        }


        Map<Uri, OwnedCertificate> mergeMap = Maps.newHashMap();
        for (OwnedCertificate ownedCertificate : ownedCertificateList) {
            mergeMap.put(ownedCertificate.getResourceId(), ownedCertificate);
        }

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_owned_cert.OwnedCertificate paramCertificate : request
                .getOwnedCertificate()) {
            Uri paramUri = paramCertificate.getResourceId();
            mergeMap.remove(paramUri);
        }

        ownedCertificateList.clear();

        ownedCertificateList.addAll(mergeMap.values());


        return capableSwitch;
    }

    @Override
    CapableSwitch putCapableSwitch(CapableSwitch capableSwitch, HandleOwnedCertInput request) {

        Resources resources = capableSwitch.getResources();
        CapableSwitch returnCapableSwitch = capableSwitch;
        if (resources == null) {
            capableSwitch = buildCapableSwitchResources(capableSwitch);
            resources = capableSwitch.getResources();
        }

        List<OwnedCertificate> ownedCertificateList = resources.getOwnedCertificate();

        if (ownedCertificateList == null) {
            capableSwitch = buildCapableSwitchResourcesOwnedCertificateList(capableSwitch);
            ownedCertificateList = capableSwitch.getResources().getOwnedCertificate();
        }

        ownedCertificateList.clear();

        for (org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.ver12.
                api.types.rev150901.ofconfig_owned_cert.OwnedCertificate paramCertificate : request
                .getOwnedCertificate()) {

            OwnedCertificateBuilder builder = new OwnedCertificateBuilder();

            builder.setKey(new OwnedCertificateKey(paramCertificate.getKey().getResourceId()))
                    .setCertificate(paramCertificate.getCertificate())
                    .setResourceId(paramCertificate.getResourceId());


            ownedCertificateList.add(builder.build());

        }



        return capableSwitch;
    }


    private CapableSwitch buildCapableSwitchResourcesOwnedCertificateList(
            CapableSwitch capableSwitch) {

        CapableSwitchBuilder cpswBuilder = new CapableSwitchBuilder();
        cpswBuilder.setId(capableSwitch.getId()).setConfigVersion(capableSwitch.getConfigVersion())
                .setLogicalSwitches(capableSwitch.getLogicalSwitches());

        ResourcesBuilder resBuilder = new ResourcesBuilder();

        List<OwnedCertificate> ownedCertificateList = Lists.newArrayList();

        resBuilder.setOwnedCertificate(ownedCertificateList)
                .setExternalCertificate(capableSwitch.getResources().getExternalCertificate())
                .setFlowTable(capableSwitch.getResources().getFlowTable())
                .setPort(capableSwitch.getResources().getPort())
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
