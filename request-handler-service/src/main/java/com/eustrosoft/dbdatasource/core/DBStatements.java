/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.dbdatasource.core;

import com.eustrosoft.dbdatasource.queries.Query;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static com.eustrosoft.dbdatasource.constants.DBConstants.LVL_OTHER;
import static com.eustrosoft.dbdatasource.constants.DBConstants.LVL_ROOT;
import static com.eustrosoft.dbdatasource.constants.DBConstants.LVL_SCOPE;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ROOTS;
import static com.eustrosoft.dbdatasource.constants.DBConstants.SCOPES;
import static com.eustrosoft.dbdatasource.constants.DBConstants.SEPARATOR;
import static com.eustrosoft.dbdatasource.constants.DBConstants.UID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZOID;
import static com.eustrosoft.dbdatasource.constants.DBConstants.ZRID;

public final class DBStatements {
    @SneakyThrows
    public static PreparedStatement getViewStatementForPath(Connection connection, String path) {
        int pathLvl = getPathLvl(path);
        if (pathLvl == LVL_SCOPE) {
            return connection.prepareStatement(
                    Query.builder()
                            .select().all().from().add(SCOPES)
                            .buildWithSemicolon()
                            .getQuery().toString()
            );
        }
        if (pathLvl == LVL_ROOT) {
            String rootId = path.replaceAll(SEPARATOR, "");
            // select * from FS.V_FFile where ZSID = 1048576 and type = 'R';
            return connection.prepareStatement(
                    Query.builder()
                            .select().all().from().add(ROOTS)
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
                            .buildWithSemicolon()
                            .getQuery().toString()
            );
        } else if (pathLvl == LVL_OTHER) {
            if (path.lastIndexOf(SEPARATOR) == path.length() - 1) {
                path = path.substring(0, path.length() - 1);
            }
            //select FD.*, FF.* from FS.V_FDir AS FD left outer
            // join FS.V_FFile as FF on (FD.f_id = FF.zoid)
            // where fd.zoid = 1441804;

            String lastId = path.substring(path.lastIndexOf(SEPARATOR) + 1);
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
                            .buildWithSemicolon()
                            .getQuery().toString()
            );
        }
        throw new Exception("Can not find PreparedStatement for path.");
    }

    public static String getSelectForPath(String path) {
        String[] pathParts = getPathParts(path);
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
                getWhereForLvlAndName(pathParts, lvl)
        ).buildWithSemicolon().toString();
    }

    @SneakyThrows
    public static String[] getPathParts(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path was null.");
        }
        if (path.indexOf(SEPARATOR) == 0) {
            path = path.substring(1);
        }
        if (path.lastIndexOf(SEPARATOR) == path.length() - 1) {
            path = path.substring(0, path.length() - 1);
        }
        return path.trim().split(SEPARATOR);
    }

    @SneakyThrows
    public static PreparedStatement getFileDetails(Connection connection, String zoid) {
        return connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("name, mimetype, type")
                        .from()
                        .add("FS.V_FFile")
                        .where(String.format("%s = %s", ZOID, zoid))
                        .buildWithSemicolon()
                        .toString()
        );
    }

    @SneakyThrows
    public static PreparedStatement getBlobDetails(Connection connection, String zoid) {
        return connection.prepareStatement(
                Query.builder()
                        .select()
                        .all()
                        .from()
                        .add("FS.V_FBlob")
                        .where(String.format("%s = %s", ZOID, zoid))
                        .add(String.format("order by %s %s", ZRID, "ASC"))
                        .buildWithSemicolon()
                        .toString()
        );
    }

    @SneakyThrows
    public static PreparedStatement getBlobLength(Connection connection, String zoid) {
        return connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("sum(length(chunk))")
                        .from()
                        .add("FS.V_FBlob")
                        .where(String.format("%s = %s", ZOID, zoid))
                        .buildWithSemicolon()
                        .toString()
        );
    }

    @SneakyThrows
    public static int getPathLvl(String path) {
        if (path == null || path.isEmpty() || path.trim().isEmpty()) {
            throw new Exception("Path was null.");
        }
        String processedPath = path.trim().replace("..", "");
        if (processedPath.equals(SEPARATOR)) {
            return LVL_SCOPE;
        }
        processedPath = processedPath.substring(1);
        int nextSlash = processedPath.indexOf(SEPARATOR);
        if (nextSlash == -1) {
            return LVL_ROOT;
        }
        if (nextSlash > 0) {
            String afterSlash = processedPath.substring(nextSlash + 1);
            if (afterSlash == null || afterSlash.isEmpty()) {
                return LVL_ROOT;
            }
        }
        return LVL_OTHER;
    }

    private String getNameFromPath(String path, int level) {
        String firstLevelFromPath = getFirstLevelFromPath(path);
        if (level == 0) {
            return firstLevelFromPath;
        }
        return getNameFromPath(path.substring(firstLevelFromPath.length()), level - 1);
    }

    public static String getFirstLevelFromPath(String path) {
        String firSlashRem = path.substring(1);
        int nextSlash = firSlashRem.indexOf(SEPARATOR);
        if (nextSlash == -1) {
            return firSlashRem;
        } else {
            return firSlashRem.substring(0, nextSlash);
        }
    }

    public static String getLastLevelFromPath(String path) throws Exception {
        int lastSlash = path.lastIndexOf(SEPARATOR);
        if (lastSlash == -1) {
            throw new Exception("Illegal path.");
        } else {
            return path.substring(lastSlash + 1);
        }
    }

    private static String getWhereForLvlAndName(String[] partNames, int lvl) {
        StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for (int i = 0; i < lvl; i++) {
            if (i == 0) {
                builder.append(String.format("XS.name = '%s'", partNames[i]));
            } else if (i == 1) {
                builder.append(String.format("FF.ZSID = XS.id and FF.name = '%s'", partNames[i]));
            } else if (i == 2) {
                builder.append(String.format("FD0.ZOID = FF.ZOID and FD0.fname = '%s'", partNames[i]));
            } else {
                builder.append(String.format("FD%d.ZOID = FD%d.f_id and FD%d.fname ='%s'", i - 2, i - 3, i - 2, partNames[i]));
            }
            if (i != lvl - 1) {
                builder.append(" and ");
            }
        }
        return builder.toString();
    }

    @SneakyThrows
    public static PreparedStatement getChats(Connection connection, String uid) {
        return connection.prepareStatement(
                Query.builder()
                        .select()
                        .add("subject, obj_id, status")
                        .from()
                        .add("MSG.V_CChannel")
                        .where(String.format("%s = %s", UID, uid))
                        .buildWithSemicolon()
                        .toString()
        );
    }
}
