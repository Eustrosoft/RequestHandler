package org.eustrosoft.dic.model;

import lombok.*;
import org.eustrosoft.core.constants.DBConstants;

import java.sql.ResultSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DIC extends DBObject {
    private String dic;
    private String code;
    private String value;
    private String descr;

    public DIC(Long id, Long zver, Long zrid,
               String dic, String code, String value, String descr) {
        super(id, zver, zrid);
        this.dic = dic;
        this.code = code;
        this.value = value;
        this.descr = descr;
    }

    @Override
    @SneakyThrows
    public void fillFromResultSet(ResultSet resultSet) {
        super.fillFromResultSet(resultSet);
        String dic = resultSet.getString(DBConstants.DIC);
        if (dic != null && !dic.isEmpty()) {
            setDic(dic.trim());
        }
        String code = resultSet.getString(DBConstants.CODE);
        if (code != null && !code.isEmpty()) {
            setCode(code.trim());
        }
        String value = resultSet.getString(DBConstants.VALUE);
        if (value != null && !value.isEmpty()) {
            setValue(value.trim());
        }
        String descr = resultSet.getString(DBConstants.DESCR);
        if (descr != null && !descr.isEmpty()) {
            setDescr(descr.trim());
        }
    }
}
