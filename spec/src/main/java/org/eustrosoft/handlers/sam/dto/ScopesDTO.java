package org.eustrosoft.handlers.sam.dto;

import org.eustrosoft.json.JsonUtil;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.spec.interfaces.JsonConvertible;

import java.util.List;

public class ScopesDTO implements JsonConvertible {
    private List<Number> zsid;

    public ScopesDTO(List<Number> zsid) {
        this.zsid = zsid;
    }

    public List<Number> getZsid() {
        return zsid;
    }

    public void setZsid(List<Number> zsid) {
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
