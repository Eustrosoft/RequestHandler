package org.eustrosoft.handlers.dic.dto;

import org.eustrosoft.core.json.exception.JsonException;
import org.eustrosoft.core.json.interfaces.JsonConvertible;
import org.eustrosoft.util.JsonUtil;

public class DicDto implements JsonConvertible {
    private String dic;
    private String value;
    private String code;
    private String descr;

    public DicDto(String dic, String value, String code, String descr) {
        this.dic = dic;
        this.value = value;
        this.code = code;
        this.descr = descr;
    }

    public String getDic() {
        return dic;
    }

    public void setDic(String dic) {
        this.dic = dic;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public String convertToString() throws JsonException {
        return JsonUtil.toJson(
                JsonUtil.getFormatString(4),
                JsonUtil.AsEntry.getStringParams("dic", dic),
                JsonUtil.AsEntry.getStringParams("value", value),
                JsonUtil.AsEntry.getStringParams("code", code),
                JsonUtil.AsEntry.getStringParams("descr", descr)
        );
    }
}
