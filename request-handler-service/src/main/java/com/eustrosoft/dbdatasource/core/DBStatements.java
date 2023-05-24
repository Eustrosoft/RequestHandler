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

public final class DBStatements {
    @SneakyThrows
    public static PreparedStatement getStatementForPath(Connection connection, String path) {
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
            String rootId = path.replaceAll("/", "");
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
            if (path.lastIndexOf('/') == path.length() - 1) {
                path = path.substring(0, path.length() - 1);
            }
            //select FD.*, FF.* from FS.V_FDir AS FD left outer
            // join FS.V_FFile as FF on (FD.f_id = FF.zoid)
            // where fd.zoid = 1441804;

            String lastId = path.substring(path.lastIndexOf('/') + 1);
            return connection.prepareStatement(
                    Query.builder()
                            .select()
                            .add("FD.*, FF.*")
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

    @SneakyThrows
    private static int getPathLvl(String path) {
        if (path == null || path.isEmpty() || path.trim().isEmpty()) {
            throw new Exception("Path was null.");
        }
        String processedPath = path.trim().replace("..", "");
        if (processedPath.equals("/")) {
            return LVL_SCOPE;
        }
        processedPath = processedPath.substring(1);
        int nextSlash = processedPath.indexOf('/');
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
}
