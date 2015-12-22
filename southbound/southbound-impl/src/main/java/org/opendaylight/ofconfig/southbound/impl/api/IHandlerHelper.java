/*
 * Copyright (c) 2015 ZTE, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.ofconfig.southbound.impl.api;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ofconfig.base.types.rev150901.HandleMode;
import org.opendaylight.yangtools.yang.common.RpcResult;

/**
 * @author rui hu hu.rui2@zte.com.cn
 *
 */
public interface IHandlerHelper<T> {


    HandleMode getRequestHandleMode(T request);

    Future<RpcResult<Void>> doMerge(T request);

    Future<RpcResult<Void>> doDelete(T request);

    Future<RpcResult<Void>> doPut(T request);

}
