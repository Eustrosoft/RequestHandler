/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.parameters;


public class CMSObjectUpdateParameters {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CMSObjectUpdateParameters(String description) {
        this.description = description;
    }

    public CMSObjectUpdateParameters(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
