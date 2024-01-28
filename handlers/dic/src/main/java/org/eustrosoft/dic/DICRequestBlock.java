/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.dic;

import org.eustrosoft.json.QJson;
import org.eustrosoft.spec.interfaces.JsonData;
import org.eustrosoft.spec.request.TISRequestBlock;

import static org.eustrosoft.spec.Constants.SUBSYSTEM_DIC;

public class DICRequestBlock<T extends JsonData> extends TISRequestBlock<T> {
    private String id;
    private String dic;

    public DICRequestBlock(String request, QJson qJson) {
        super(SUBSYSTEM_DIC, request, qJson);
        parseQJson(qJson);
    }

    private void parseQJson(QJson qJson) {
        if (qJson == null) {
            throw new NullPointerException("QJson was null");
        }
        setDic(qJson.getItemString("dic"));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = dic;
    }
}
