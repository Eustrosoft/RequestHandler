package org.eustrosoft.handlers.msg.dto;

import org.eustrosoft.constants.DBConstants;
import org.eustrosoft.core.json.QJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MsgParams {
    private Long ZOID;
    private Long ZRID;
    private Long ZVER;
    private Long ZSID;
    private Short ZLVL;
    private String status;
    private String subject;
    private String content;
    private Long reference;
    private String type;
    private List<String> statuses;

    public static MsgParams fromJson(QJson qJson) {
        MsgParams params = new MsgParams();
        params.setZOID(qJson.getItemLong(DBConstants.ZOID));
        params.setZRID(qJson.getItemLong(DBConstants.ZRID));
        params.setZVER(qJson.getItemLong(DBConstants.ZVER));
        params.setZSID(qJson.getItemLong(DBConstants.ZSID));
        params.setStatus(qJson.getItemString("status"));
        params.setSubject(qJson.getItemString("subject"));
        params.setContent(qJson.getItemString("content"));
        params.setType(qJson.getItemString("type"));
        params.setStatuses(getStatuses(qJson.getItemQJson("statuses")));
        params.setReference(qJson.getItemLong("reference"));
        params.setZLVL(qJson.getItemLong(DBConstants.ZLVL) == null ?
                null : Objects.requireNonNull(qJson.getItemLong(DBConstants.ZLVL)).shortValue()
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

    public Long getZOID() {
        return ZOID;
    }

    public void setZOID(Long ZOID) {
        this.ZOID = ZOID;
    }

    public Long getZRID() {
        return ZRID;
    }

    public void setZRID(Long ZRID) {
        this.ZRID = ZRID;
    }

    public Long getZVER() {
        return ZVER;
    }

    public void setZVER(Long ZVER) {
        this.ZVER = ZVER;
    }

    public Long getZSID() {
        return ZSID;
    }

    public void setZSID(Long ZSID) {
        this.ZSID = ZSID;
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

    public Short getZLVL() {
        return ZLVL;
    }

    public void setZLVL(Short ZLVL) {
        this.ZLVL = ZLVL;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }
}
