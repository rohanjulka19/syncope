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
package org.apache.syncope.client.console.panels.search;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.syncope.client.console.rest.GroupRestClient;
import org.apache.syncope.common.lib.to.GroupTO;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class AnyObjectSearchPanel extends AbstractSearchPanel {

    private static final long serialVersionUID = -1769527800450203738L;

    private final GroupRestClient groupRestClient = new GroupRestClient();

    public static class Builder extends AbstractSearchPanel.Builder<AnyObjectSearchPanel> {

        private static final long serialVersionUID = 6308997285778809578L;

        public Builder(final IModel<List<SearchClause>> model) {
            super(model);
        }

        @Override
        public AnyObjectSearchPanel build(final String id) {
            return new AnyObjectSearchPanel(id, AnyTypeKind.ANY_OBJECT, this);
        }
    }

    protected AnyObjectSearchPanel(final String id, final AnyTypeKind kind, final Builder builder) {
        super(id, kind, builder);
    }

    @Override
    protected void populate() {
        super.populate();

        this.types = new LoadableDetachableModel<List<SearchClause.Type>>() {

            private static final long serialVersionUID = 5275935387613157437L;

            @Override
            protected List<SearchClause.Type> load() {
                List<SearchClause.Type> result = new ArrayList<SearchClause.Type>();
                result.add(SearchClause.Type.ATTRIBUTE);
                result.add(SearchClause.Type.MEMBERSHIP);
                result.add(SearchClause.Type.RESOURCE);
                return result;
            }
        };

        this.groupNames = new LoadableDetachableModel<List<Pair<Long, String>>>() {

            private static final long serialVersionUID = 5275935387613157437L;

            @Override
            protected List<Pair<Long, String>> load() {
                List<GroupTO> groupTOs = groupRestClient.list("/",
                        -1, -1,
                        new SortParam<>("name", true),
                        null);

                List<Pair<Long, String>> result = new ArrayList<>(groupTOs.size());
                for (GroupTO group : groupTOs) {
                    result.add(Pair.of(group.getKey(), group.getName()));
                }

                return result;
            }
        };
    }
}
