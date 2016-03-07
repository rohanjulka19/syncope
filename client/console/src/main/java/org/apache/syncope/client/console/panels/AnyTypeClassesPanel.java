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
package org.apache.syncope.client.console.panels;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.syncope.client.console.SyncopeConsoleSession;
import org.apache.syncope.client.console.commons.Constants;
import org.apache.syncope.client.console.commons.SearchableDataProvider;
import org.apache.syncope.client.console.commons.SortableDataProviderComparator;
import org.apache.syncope.client.console.wicket.extensions.markup.html.repeater.data.table.ActionColumn;
import org.apache.syncope.client.console.wicket.extensions.markup.html.repeater.data.table.BooleanPropertyColumn;
import org.apache.syncope.client.console.wicket.markup.html.bootstrap.dialog.BaseModal;
import org.apache.syncope.client.console.wicket.markup.html.form.ActionLink;
import org.apache.syncope.client.console.wicket.markup.html.form.ActionLinksPanel;
import org.apache.syncope.client.console.wizards.AbstractModalPanelBuilder;
import org.apache.syncope.client.console.wizards.AjaxWizard;
import org.apache.syncope.common.lib.to.AnyTypeClassTO;
import org.apache.syncope.common.lib.types.StandardEntitlement;
import org.apache.syncope.common.rest.api.service.AnyTypeClassService;
import org.apache.wicket.PageReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class AnyTypeClassesPanel extends AbstractTypesPanel<AnyTypeClassTO, AnyTypeClassesPanel.AnyTypeClassProvider> {

    private static final long serialVersionUID = -2356760296223908382L;

    public AnyTypeClassesPanel(final String id, final PageReference pageRef) {
        super(id, pageRef);
        disableCheckBoxes();

        this.addNewItemPanelBuilder(new AbstractModalPanelBuilder<AnyTypeClassTO>(
                BaseModal.CONTENT_ID, new AnyTypeClassTO(), pageRef) {

            private static final long serialVersionUID = -6388405037134399367L;

            @Override
            public ModalPanel<AnyTypeClassTO> build(final int index, final boolean edit) {
                final AnyTypeClassTO modelObject = newModelObject();
                return new AnyTypeClassModalPanel(modal, modelObject, pageRef) {

                    private static final long serialVersionUID = -6227956682141146095L;

                    @Override
                    public void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                        try {
                            if (getOriginalItem() == null || StringUtils.isBlank(getOriginalItem().getKey())) {
                                SyncopeConsoleSession.get().getService(AnyTypeClassService.class).create(modelObject);
                            } else {
                                SyncopeConsoleSession.get().getService(AnyTypeClassService.class).update(modelObject);
                            }
                            info(getString(Constants.OPERATION_SUCCEEDED));
                            modal.close(target);
                        } catch (Exception e) {
                            LOG.error("While creating or updating AnyTypeClassTO", e);
                            error(StringUtils.isBlank(e.getMessage()) ? e.getClass().getName() : e.getMessage());
                        }
                        SyncopeConsoleSession.get().getNotificationPanel().refresh(target);
                    }
                };
            }
        }, true);

        initResultTable();
        MetaDataRoleAuthorizationStrategy.authorize(addAjaxLink, ENABLE, StandardEntitlement.ANYTYPE_CREATE);
    }

    @Override
    protected AnyTypeClassesPanel.AnyTypeClassProvider dataProvider() {
        return new AnyTypeClassesPanel.AnyTypeClassProvider(rows);
    }

    @Override
    protected String paginatorRowsKey() {
        return Constants.PREF_ANYTYPE_PAGINATOR_ROWS;
    }

    @Override
    protected Collection<ActionLink.ActionType> getBulkActions() {
        return Collections.<ActionLink.ActionType>emptyList();
    }

    @Override
    protected List<IColumn<AnyTypeClassTO, String>> getColumns() {
        final List<IColumn<AnyTypeClassTO, String>> columns = new ArrayList<>();

        for (Field field : AnyTypeClassTO.class.getDeclaredFields()) {
            if (field != null && !Modifier.isStatic(field.getModifiers())) {
                final String fieldName = field.getName();
                if (field.getType().isArray()
                        || Collection.class.isAssignableFrom(field.getType())
                        || Map.class.isAssignableFrom(field.getType())) {

                    columns.add(new PropertyColumn<AnyTypeClassTO, String>(
                            new ResourceModel(field.getName()), field.getName()));
                } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                    columns.add(new BooleanPropertyColumn<AnyTypeClassTO>(
                            new ResourceModel(field.getName()), field.getName(), field.getName()));
                } else {
                    columns.add(new PropertyColumn<AnyTypeClassTO, String>(
                            new ResourceModel(field.getName()), field.getName(), field.getName()) {

                        private static final long serialVersionUID = -6902459669035442212L;

                        @Override
                        public String getCssClass() {
                            String css = super.getCssClass();
                            if ("key".equals(fieldName)) {
                                css = StringUtils.isBlank(css)
                                        ? "col-xs-1"
                                        : css + " col-xs-1";
                            }
                            return css;
                        }
                    });
                }
            }
        }

        columns.add(new ActionColumn<AnyTypeClassTO, String>(new ResourceModel("actions", "")) {

            private static final long serialVersionUID = 906457126287899096L;

            @Override
            public ActionLinksPanel<AnyTypeClassTO> getActions(
                    final String componentId, final IModel<AnyTypeClassTO> model) {

                ActionLinksPanel<AnyTypeClassTO> panel = ActionLinksPanel.<AnyTypeClassTO>builder().
                        add(new ActionLink<AnyTypeClassTO>() {

                            private static final long serialVersionUID = -3722207913631435501L;

                            @Override
                            public void onClick(final AjaxRequestTarget target, final AnyTypeClassTO ignore) {
                                send(AnyTypeClassesPanel.this, Broadcast.EXACT,
                                        new AjaxWizard.EditItemActionEvent<>(model.getObject(), target));
                            }
                        }, ActionLink.ActionType.EDIT, StandardEntitlement.ANYTYPECLASS_UPDATE).
                        add(new ActionLink<AnyTypeClassTO>() {

                            private static final long serialVersionUID = -3722207913631435501L;

                            @Override
                            public void onClick(final AjaxRequestTarget target, final AnyTypeClassTO ignore) {
                                try {
                                    SyncopeConsoleSession.get().
                                            getService(AnyTypeClassService.class).delete(model.getObject().getKey());
                                    info(getString(Constants.OPERATION_SUCCEEDED));
                                    target.add(container);
                                } catch (Exception e) {
                                    LOG.error("While deleting {}", model.getObject(), e);
                                    error(StringUtils.isBlank(e.getMessage())
                                            ? e.getClass().getName() : e.getMessage());
                                }
                                SyncopeConsoleSession.get().getNotificationPanel().refresh(target);
                            }
                        }, ActionLink.ActionType.DELETE, StandardEntitlement.ANYTYPECLASS_DELETE).
                        build(componentId);

                return panel;
            }

            @Override
            public ActionLinksPanel<AnyTypeClassTO> getHeader(final String componentId) {
                final ActionLinksPanel.Builder<AnyTypeClassTO> panel = ActionLinksPanel.builder();

                return panel.add(new ActionLink<AnyTypeClassTO>() {

                    private static final long serialVersionUID = -1140254463922516111L;

                    @Override
                    public void onClick(final AjaxRequestTarget target, final AnyTypeClassTO ignore) {
                        if (target != null) {
                            target.add(container);
                        }
                    }
                }, ActionLink.ActionType.RELOAD).build(componentId);
            }
        });

        return columns;
    }

    protected final class AnyTypeClassProvider extends SearchableDataProvider<AnyTypeClassTO> {

        private static final long serialVersionUID = -185944053385660794L;

        private final SortableDataProviderComparator<AnyTypeClassTO> comparator;

        private AnyTypeClassProvider(final int paginatorRows) {
            super(paginatorRows);
            comparator = new SortableDataProviderComparator<>(this);
        }

        @Override
        public Iterator<AnyTypeClassTO> iterator(final long first, final long count) {
            final List<AnyTypeClassTO> list = SyncopeConsoleSession.get().getService(AnyTypeClassService.class).list();
            Collections.sort(list, comparator);
            return list.subList((int) first, (int) first + (int) count).iterator();
        }

        @Override
        public long size() {
            return SyncopeConsoleSession.get().getService(AnyTypeClassService.class).list().size();
        }

        @Override
        public IModel<AnyTypeClassTO> model(final AnyTypeClassTO object) {
            return new CompoundPropertyModel<>(object);
        }
    }
}
