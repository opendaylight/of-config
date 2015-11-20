/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl;

import static org.mockito.Mockito.mock;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.sal.binding.test.AbstractDataServiceTest;
import org.opendaylight.ofconfig.southbound.impl.utils.MdsalUtils;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public abstract class OFconfigTestBase extends AbstractDataServiceTest{
    
    
private OfconfigSouthboundImpl ofconfigSouthboundImpl;
    
    protected DataBroker  databroker;
    
    protected MdsalUtils mdsalUtils;
    
   @Before
    public void setUp() {
        super.setUp();
         databroker =  this.testContext.getDataBroker();
         MountPointService mountService =mock(MountPointService.class);
        
        ofconfigSouthboundImpl = new OfconfigSouthboundImpl();
        
        ofconfigSouthboundImpl.setDataBroker(databroker);
        ofconfigSouthboundImpl.setMountService(mountService);
        
        ofconfigSouthboundImpl.initTopoAndListener();
        
        mdsalUtils = new MdsalUtils();
        
    }

}
