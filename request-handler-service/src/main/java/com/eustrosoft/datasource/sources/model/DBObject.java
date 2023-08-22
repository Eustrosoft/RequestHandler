package com.eustrosoft.datasource.sources.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBObject {
    private String id;
    private Date created;
    private Date modified;
}
