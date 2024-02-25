package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

import java.util.List;

public class ScopesDTO implements JsonConvertible {
    private List<Long> zsid;

    public ScopesDTO(List<Long> zsid) {
        this.zsid = zsid;
    }

    public List<Long> getZsid() {
        return zsid;
    }

    public void setZsid(List<Long> zsid) {
        this.zsid = zsid;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(1),
                JsonUtil.AsEntry.getNumberCollection("zsid", zsid)
        );
    }
}
