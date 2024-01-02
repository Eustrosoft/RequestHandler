/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms.parameters;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CMSObjectUpdateParameters {
    private String name;
    private String description;

    public CMSObjectUpdateParameters(String description) {
        this.description = description;
    }
}
