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
    private String reference;
    private String type;
    private Integer slvl;

    public static MsgParams fromJson(QJson qJson) {
        MsgParams params = new MsgParams();
        params.setId(qJson.getItemLong("id"));
        params.setStatus(qJson.getItemString("status"));
        params.setContent(qJson.getItemString("content"));
        params.setReference(qJson.getItemString("reference"));
        params.setType(qJson.getItemString("type"));
        try {
            params.setSlvl(qJson.getItemLong("slvl").intValue());
        } catch (Exception ex) {

        }
        return params;
    }
}
