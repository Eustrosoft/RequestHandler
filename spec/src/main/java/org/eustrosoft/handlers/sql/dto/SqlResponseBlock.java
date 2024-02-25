package org.eustrosoft.handlers.sql.dto;

import org.eustrosoft.core.response.basic.BasicResponseBlock;
import org.eustrosoft.core.response.basic.ListStringResponseData;

import static org.eustrosoft.constants.Constants.SUBSYSTEM_SQL;

public class SqlResponseBlock extends BasicResponseBlock<ListStringResponseData> {

    public SqlResponseBlock(String request) {
        this.s = SUBSYSTEM_SQL;
        this.r = request;
    }
}
