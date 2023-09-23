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
    private Long zoid;
    private Long zrid;
    private String status;
    private String subject;
    private String content;
    private Long reference;
    private String type;
    private Integer slvl;

    public static MsgParams fromJson(QJson qJson) {
        MsgParams params = new MsgParams();
        params.setZoid(getLongOrNull(qJson, "zoid"));
        params.setZrid(getLongOrNull(qJson, "zrid"));
        params.setStatus(qJson.getItemString("status"));
        params.setSubject(qJson.getItemString("subject"));
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
