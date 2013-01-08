/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.services;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.syncope.client.to.NotificationTO;

@Path("notifications")
public interface NotificationService {

    @GET
    @Path("{notificationId}")
//    @RequestMapping(method = RequestMethod.GET, value = "/read/{notificationId}")
    NotificationTO read(@PathParam("notificationId") final Long notificationId);

    @GET
//    @RequestMapping(method = RequestMethod.GET, value = "/list")
    List<NotificationTO> list();

    @POST
//    @RequestMapping(method = RequestMethod.POST, value = "/create")
    NotificationTO create(final NotificationTO notificationTO);

    @PUT
    @Path("{notificationId}")
//    @RequestMapping(method = RequestMethod.POST, value = "/update")
    NotificationTO update(@PathParam("notificationId") final Long notificationId, final NotificationTO notificationTO);

    @DELETE
    @Path("{notificationId}")
//    @RequestMapping(method = RequestMethod.GET, value = "/delete/{notificationId}")
    NotificationTO delete(@PathParam("notificationId") final Long notificationId);

}