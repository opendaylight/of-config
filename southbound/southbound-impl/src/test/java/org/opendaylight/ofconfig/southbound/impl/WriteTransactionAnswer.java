/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl;

import java.util.concurrent.atomic.AtomicReference;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.opendaylight.yang.gen.v1.urn.onf.config.yang.rev140601.CapableSwitch;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public class WriteTransactionAnswer implements Answer<CapableSwitch> {

    private AtomicReference<CapableSwitch> capableSwitchRef;


    public WriteTransactionAnswer(AtomicReference<CapableSwitch> capableSwitchRef) {
        super();
        this.capableSwitchRef = capableSwitchRef;
    }



    @Override
    public CapableSwitch answer(InvocationOnMock invocation) throws Throwable {

        CapableSwitch capableSwitch = (CapableSwitch) invocation.getArguments()[2];

        capableSwitchRef.set(capableSwitch);


        return capableSwitch;
    }

}
