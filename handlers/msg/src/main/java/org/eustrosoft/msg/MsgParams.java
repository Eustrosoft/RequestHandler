package org.eustrosoft.msg;

import org.eustrosoft.json.QJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    public Long getZoid() {
        return zoid;
    }

    public void setZoid(Long zoid) {
        this.zoid = zoid;
    }

    public Long getZrid() {
        return zrid;
    }

    public void setZrid(Long zrid) {
        this.zrid = zrid;
    }

    public Long getZver() {
        return zver;
    }

    public void setZver(Long zver) {
        this.zver = zver;
    }

    public Long getZsid() {
        return zsid;
    }

    public void setZsid(Long zsid) {
        this.zsid = zsid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getReference() {
        return reference;
    }

    public void setReference(Long reference) {
        this.reference = reference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Short getSlvl() {
        return slvl;
    }

    public void setSlvl(Short slvl) {
        this.slvl = slvl;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }
}
