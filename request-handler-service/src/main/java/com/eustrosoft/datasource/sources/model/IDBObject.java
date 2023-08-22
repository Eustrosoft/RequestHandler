package com.eustrosoft.datasource.sources.model;

import java.util.Date;

public interface IDBObject {
    String getId();

    Date getCreated();

    Date getModified();
}
