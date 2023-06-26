/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package domainapp.dom.impl;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.ComparisonChain;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Auditing;
import org.apache.isis.applib.annotation.CommandReification;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Publishing;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "myapp" )
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy= VersionStrategy.DATE_TIME, column ="version")
@javax.jdo.annotations.Unique(name="HelloWorldObject_name_UNQ", members = {"name"})
@DomainObject(auditing = Auditing.ENABLED)
@DomainObjectLayout()  // causes UI events to be triggered
public class HelloWorldObject implements Comparable<HelloWorldObject> {

    public HelloWorldObject(final String name) {
        this.name = name;
    }

    @javax.jdo.annotations.Column(allowsNull = "false", length = 40)
    @Property(editing = Editing.DISABLED)
    @Title(prepend = "Object: ")
    private String name;
    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    @javax.jdo.annotations.Column(allowsNull = "true", length = 4000)
    @Property(editing = Editing.ENABLED)
    private String notes;
    public String getNotes() { return notes; }
    public void setNotes(final String notes) { this.notes = notes; }


    @Action(semantics = SemanticsOf.IDEMPOTENT, command = CommandReification.ENABLED, publishing = Publishing.ENABLED)
    public HelloWorldObject updateName(
            @Parameter(maxLength = 40)
            @ParameterLayout(named = "Name")
            final String name) {
        setName(name);
        return this;
    }
    public String default0UpdateName() {
        return getName();
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void delete() {
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deleted", title));
        repositoryService.removeAndFlush(this);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(final HelloWorldObject other) {
        return ComparisonChain.start()
                .compare(this.getName(), other.getName())
                .result();
    }


    @javax.jdo.annotations.NotPersistent
    @javax.inject.Inject
    RepositoryService repositoryService;

    @javax.jdo.annotations.NotPersistent
    @javax.inject.Inject
    TitleService titleService;

    @javax.jdo.annotations.NotPersistent
    @javax.inject.Inject
    MessageService messageService;


}