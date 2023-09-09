package com.eustrosoft.core.handlers.msg;

import com.eustrosoft.core.tools.QJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MsgParams {
    private Long id;
    private String status;
    private String content;
    private Long reference;
    private String type;
    private Integer slvl;

    public static MsgParams fromJson(QJson qJson) {
        MsgParams params = new MsgParams();
        params.setId(qJson.getItemLong("id"));
        params.setStatus(qJson.getItemString("status"));
        params.setContent(qJson.getItemString("content"));
        params.setType(qJson.getItemString("type"));
        params.setReference(getLongOrNull(qJson, "reference"));
        params.setSlvl(getLongOrNull(qJson, "slvl") == null ? null : getLongOrNull(qJson, "slvl").intValue());
        return params;
    }

    private static Long getLongOrNull(QJson json, String name) {
        try {
            return json.getItemLong(name);
        } catch (Exception ex) {
            return null;
        }
    }
}
