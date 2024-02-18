package org.eustrosoft.handlers.sql.dto;

import org.eustrosoft.spec.interfaces.JsonConvertible;
import org.eustrosoft.spec.response.BasicResponseBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_SQL;

public class SqlResponseBock<T extends JsonConvertible> extends BasicResponseBlock<T> {

    public SqlResponseBock(String request) {
        this.s = SUBSYSTEM_SQL;
        this.r = request;
    }
}
