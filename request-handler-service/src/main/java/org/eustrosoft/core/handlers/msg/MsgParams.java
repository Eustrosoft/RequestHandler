package org.eustrosoft.core.handlers.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eustrosoft.core.tools.QJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MsgParams {
    private Long zoid;
    private Long zrid;
    private Long zver;
    private Long zsid;
    private String status;
    private String subject;
    private String content;
    private Long reference;
    private String type;
    private Short slvl;
    private List<String> statuses;

    public static MsgParams fromJson(QJson qJson) {
        MsgParams params = new MsgParams();
        params.setZoid(getLongOrNull(qJson, "zoid"));
        params.setZrid(getLongOrNull(qJson, "zrid"));
        params.setZver(getLongOrNull(qJson, "zver"));
        params.setZsid(qJson.getItemLong("zsid"));
        params.setStatus(qJson.getItemString("status"));
        params.setSubject(qJson.getItemString("subject"));
        params.setContent(qJson.getItemString("content"));
        params.setType(qJson.getItemString("type"));
        params.setStatuses(getStatuses(qJson.getItemQJson("statuses")));
        params.setReference(getLongOrNull(qJson, "reference"));
        params.setSlvl(getLongOrNull(qJson, "slvl") == null ?
                null : Objects.requireNonNull(getLongOrNull(qJson, "slvl")).shortValue()
        );
        return params;
    }

    private static List<String> getStatuses(QJson qJson) {
        if (qJson == null || qJson.size() == 0) {
            return Collections.emptyList();
        }
        List<String> statuses = new ArrayList<>();
        for (int i = 0; i < qJson.size(); i++) {
            statuses.add(qJson.getItemString(i));
        }
        return statuses;
    }

    private static Long getLongOrNull(QJson json, String name) {
        try {
            return json.getItemLong(name);
        } catch (Exception ex) {
            return null;
        }
    }
}
