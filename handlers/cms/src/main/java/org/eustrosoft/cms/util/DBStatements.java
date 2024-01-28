package org.eustrosoft.cms.util;

import org.eustrosoft.cms.CMSDownloader.Query;
import org.eustrosoft.cms.constants.Constants;
import org.eustrosoft.constants.DBConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBStatements {
    public static PreparedStatement getViewStatementForPath(Connection connection, String path) throws Exception {
        int pathLvl = FileUtils.getPathLvl(path);
        if (pathLvl == DBConstants.LVL_SCOPE) {
            return connection.prepareStatement(
                    Query.builder()
                            .select().all().from().add(Constants.SCOPES)
                            .add(" ORDER BY name ASC")
                            .buildWithSemicolon()
                            .getQuery().toString()
            );
        }
        if (pathLvl == DBConstants.LVL_ROOT) {
            String rootId = path.replaceAll(DBConstants.SEPARATOR, "");
            // select * from FS.V_FFile where ZSID = 1048576 and type = 'R';
            return connection.prepareStatement(
                    Query.builder()
                            .select().all().from().add(Constants.ROOTS)
                            .where(
                                    Query.builder()
                                            .add("ZSID")
                                            .eq()
                                            .add(rootId)
                                            .and()
                                            .add("type")
                                            .eq()
                                            .add("'R'")
                                            .build()
                            )
                            .add(" ORDER BY name ASC")
                            .buildWithSemicolon()
                            .getQuery().toString()
            );
        } else if (pathLvl == DBConstants.LVL_OTHER) {
            if (path.lastIndexOf(DBConstants.SEPARATOR) == path.length() - 1) {
                path = path.substring(0, path.length() - 1);
            }
            //select FD.*, FF.* from FS.V_FDir AS FD left outer
            // join FS.V_FFile as FF on (FD.f_id = FF.zoid)
            // where fd.zoid = 1441804;

            String lastId = path.substring(path.lastIndexOf(DBConstants.SEPARATOR) + 1);
            return connection.prepareStatement(
                    Query.builder()
                            .select()
                            .add("FD.ZOID, FF.ZSID, FF.ZLVL, FD.f_id, COALESCE(FD.mimetype, FF.mimetype) " +
                                    "mimetype, COALESCE(FD.descr, FF.descr) descr, FD.fname, FF.name, FF.type")
                            .from()
                            .add("FS.V_FDir AS FD")
                            .add("left outer join")
                            .add("FS.V_FFile as FF")
                            .on()
                            .leftBracket()
                            .add("FD.f_id = FF.zoid")
                            .rightBracket()
                            .where(
                                    Query.builder()
                                            .add("FD.zoid")
                                            .eq()
                                            .add(lastId)
                                            .build()
                            )
                            .add(" ORDER BY FD.fname ASC")
                            .buildWithSemicolon()
                            .getQuery().toString()
            );
        }
        throw new Exception("Can not find PreparedStatement for path.");
    }

    public static String getSelectForPath(String path) {
        String[] pathParts = FileUtils.getPathParts(path);
        int lvl = pathParts.length;
        Query.Builder builder = Query.builder();
        builder.select();
        for (int i = 0; i < lvl; i++) {
            if (i == 0) {
                builder.add("XS.id, XS.name");
            } else if (i == 1) {
                builder.add("FF.ZOID, FF.name");
            } else {
                builder.add(String.format("FD%d.f_id, FD%d.fname", i - 2, i - 2));
            }
            if (i != lvl - 1) {
                builder.comma();
            }
        }
        builder.from();
        for (int i = 0; i < lvl; i++) {
            if (i == 0) {
                builder.add("SAM.V_Scope XS");
            } else if (i == 1) {
                builder.add("FS.V_FFile FF");
            } else {
                builder.add(String.format("FS.V_FDir FD%d", i - 2));
            }
            if (i != lvl - 1) {
                builder.comma();
            }
        }
        return builder.where(
                FileUtils.getWhereForLvlAndName(pathParts, lvl)
        ).buildWithSemicolon().toString();
    }

    public static PreparedStatement getFileDetails(Connection connection, Long zoid) throws SQLException {
        return connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("name, mimetype, type")
                        .from()
                        .add("FS.V_FFile")
                        .where(String.format("%s = %s", DBConstants.ZOID, zoid))
                        .buildWithSemicolon()
                        .toString()
        );
    }

    public static PreparedStatement getBlobDetails(Connection connection, String zoid) throws SQLException {
        return connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FBlob")
                        .where(String.format("%s = %s", DBConstants.ZOID, zoid))
                        .add(String.format("order by %s %s", DBConstants.ZRID, "ASC"))
                        .buildWithSemicolon()
                        .toString()
        );
    }

    public static PreparedStatement getBlobLength(Connection connection, Long zoid) throws SQLException {
        return connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("sum(length(chunk))")
                        .from()
                        .add("FS.V_FBlob")
                        .where(String.format("%s = %s", DBConstants.ZOID, zoid))
                        .buildWithSemicolon()
                        .toString()
        );
    }
}
