/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.cms;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class CMSDownloader {
    public final static String ZOID = "zoid";
    public final static String ZRID = "zrid";
    private static final int BUFFER_SIZE = 1024;

    private static boolean isParametersOk(CMSDownloadParameters parameters) {
        try {
            throwIfNull(parameters);
            throwIfNull(parameters.getOutput());
            throwIfNullOrEmpty(parameters.getZoid());
            throwIfNull(parameters.getConnection());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static void throwIfNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException(
                    "One of require parameters was null"
            );
        }
    }

    private static void throwIfNullOrEmpty(String str) {
        if (str == null || str.isEmpty()) {
            throw new NullPointerException(
                    "Required string was null or empty"
            );
        }
    }

    public void process(CMSDownloadParameters parameters) throws Exception {
        if (!isParametersOk(parameters)) {
            throw new Exception("Parameters was failed to process.");
        }
        OutputStream out = null;
        InputStream in = null;
        try {
            in = new DBFunctions(parameters.getConnection())
                    .getFileInputStream(parameters.getZoid());
            out = parameters.getOutput();
            byte[] buffer = new byte[BUFFER_SIZE];
            while (in.read(buffer) != -1) {
                out.write(buffer);
            }
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (Exception ex) {
                    // ex.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    // ex.printStackTrace();
                }
            }
        }
    }

    public static class CMSDownloadParameters {
        private OutputStream output;
        private Connection connection;
        private String zoid;

        public CMSDownloadParameters() {
        }

        public CMSDownloadParameters(OutputStream output, Connection connection, String zoid) {
            this.output = output;
            this.connection = connection;
            this.zoid = zoid;
        }

        public OutputStream getOutput() {
            return output;
        }

        public void setOutput(OutputStream output) {
            this.output = output;
        }

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public String getZoid() {
            return zoid;
        }

        public void setZoid(String zoid) {
            this.zoid = zoid;
        }
    }

    private static class DBFunctions {
        private final static String VIEW_BLOB = "FS.V_FBlob";
        private final static String ASCENDING = "ASC";
        private Connection connection;

        public DBFunctions(Connection poolConnection) {
            this.connection = poolConnection;
        }

        public static PreparedStatement getBlobDetails(Connection connection, String zoid) throws SQLException {
            return connection.prepareStatement(
                    Query.builder()
                            .select()
                            .all()
                            .from()
                            .add(VIEW_BLOB)
                            .where(String.format("%s = %s", ZOID, zoid))
                            .add(String.format("order by %s %s", ZRID, ASCENDING))
                            .buildWithSemicolon()
                            .toString()
            );
        }

        public InputStream getFileInputStream(String zoid) throws SQLException {
            PreparedStatement blobDetailsPS = getBlobDetails(connection, zoid);
            try {
                ResultSet resultSet = blobDetailsPS.executeQuery();
                Vector<InputStream> streams = new Vector<>();
                while (resultSet.next()) {
                    streams.add(resultSet.getBinaryStream("chunk"));
                }
                return new SequenceInputStream(streams.elements());
            } finally {
                blobDetailsPS.close();
            }
        }
    }

    static class Query {
        public final static String SELECT = "SELECT";
        public final static String FROM = "FROM";
        public final static String TABLE = "TABLE";
        public final static String WHERE = "WHERE";
        public final static String JOIN = "JOIN";
        public final static String LEFT_JOIN = "LEFT JOIN";
        public final static String RIGHT_JOIN = "RIGHT JOIN";

        public final static String SEMICOLON = ";";
        public final static String SPACE = " ";
        public final static String ON = "ON";
        public final static String IN = "IN";
        public final static String ALL = "*";
        public final static String AND = "AND";
        public final static String COMMA = ",";
        public final static String LEFT_BRACKET = "(";
        public final static String RIGHT_BRACKET = ")";
        public final static String EQ = "=";
        public final static String VARIABLE = "?";

        private StringBuilder query;

        private Query() {
            this.query = new StringBuilder();
        }

        public static Builder builder() {
            return new Builder();
        }

        public void addToQuery(String str) {
            this.query.append(str);
        }

        public StringBuilder getQuery() {
            return this.query;
        }

        @Override
        public String toString() {
            return query.toString();
        }

        static class Builder {
            private Query query;

            public Builder() {
                this.query = new Query();
            }

            public Builder select() {
                this.query.addToQuery(SELECT + SPACE);
                return this;
            }

            public Builder all() {
                this.query.addToQuery(ALL + SPACE);
                return this;
            }

            public Builder variable() {
                this.query.addToQuery(VARIABLE + SPACE);
                return this;
            }

            public Builder from() {
                this.query.addToQuery(FROM + SPACE);
                return this;
            }

            public Builder on() {
                this.query.addToQuery(ON + SPACE);
                return this;
            }

            public Builder in() {
                this.query.addToQuery(IN + SPACE);
                return this;
            }

            public Builder add(String str) {
                this.query.addToQuery(str + SPACE);
                return this;
            }

            public Builder where(String condition) {
                this.query.addToQuery(
                        WHERE + SPACE + LEFT_BRACKET + condition + RIGHT_BRACKET + SPACE
                );
                return this;
            }

            public Builder where(org.eustrosoft.core.db.Query query) {
                this.query.addToQuery(
                        WHERE + SPACE + LEFT_BRACKET + query + RIGHT_BRACKET + SPACE
                );
                return this;
            }

            public Builder object(Object obj) {
                this.query.addToQuery(obj.toString() + SPACE);
                return this;
            }

            public Builder join() {
                this.query.addToQuery(JOIN + SPACE);
                return this;
            }

            public Builder leftJoin() {
                this.query.addToQuery(LEFT_JOIN + SPACE);
                return this;
            }

            public Builder rightJoin() {
                this.query.addToQuery(RIGHT_JOIN + SPACE);
                return this;
            }

            public Builder comma() {
                this.query.addToQuery(COMMA + SPACE);
                return this;
            }

            public Builder leftBracket() {
                this.query.addToQuery(LEFT_BRACKET + SPACE);
                return this;
            }

            public Builder eq() {
                this.query.addToQuery(EQ + SPACE);
                return this;
            }

            public Builder rightBracket() {
                this.query.addToQuery(RIGHT_BRACKET + SPACE);
                return this;
            }

            public Builder and() {
                this.query.addToQuery(AND + SPACE);
                return this;
            }

            public Query buildWithSemicolon() {
                this.query.addToQuery(SEMICOLON);
                return this.query;
            }

            public Query build() {
                return this.query;
            }
        }
    }
}
