/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ofconfig.southbound.impl;

import org.junit.Before;
import static org.mockito.Mockito.mock;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.sal.binding.test.AbstractDataServiceTest;

/**
 * @author rui hu  hu.rui2@zte.com.cn
 *
 */
public class OfconfigSouthboundImplTest extends AbstractDataServiceTest {
    
    OfconfigSouthboundImpl ofconfigSouthboundImpl;
    
    //@Before
    public void setUp(){
        super.setUp();
        DataBroker  databroker =  this.testContext.getDataBroker();
        MountPointService mountService =mock(MountPointService.class);
        
        ofconfigSouthboundImpl = new OfconfigSouthboundImpl();
        
        ofconfigSouthboundImpl.setDataBroker(databroker);
        ofconfigSouthboundImpl.setMountService(mountService);
        
        ofconfigSouthboundImpl.initTopoAndListener();
    }
    
   


}
