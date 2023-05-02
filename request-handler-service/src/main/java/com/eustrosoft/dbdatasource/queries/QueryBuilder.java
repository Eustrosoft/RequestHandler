package com.eustrosoft.dbdatasource.queries;

import static com.eustrosoft.dbdatasource.constants.DBConstants.*;

public class QueryBuilder {
    private StringBuilder query;

    public void addToQuery(String str) {
        this.query.append(str);
    }

    public StringBuilder getQuery() {
        return this.query;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return query.toString();
    }

    public static class Builder {
        private QueryBuilder queryBuilder;

        public Builder select() {
            this.queryBuilder.addToQuery(SELECT + SPACE);
            return this;
        }

        public Builder all() {
            this.queryBuilder.addToQuery(ALL + SPACE);
            return this;
        }

        public QueryBuilder variable() {
            this.queryBuilder.addToQuery(VARIABLE + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder from() {
            this.queryBuilder.addToQuery(FROM + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder on() {
            this.queryBuilder.addToQuery(ON + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder in() {
            this.queryBuilder.addToQuery(IN + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder string(String str) {
            this.queryBuilder.addToQuery(str + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder object(Object obj) {
            this.queryBuilder.addToQuery(obj.toString() + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder join() {
            this.queryBuilder.addToQuery(JOIN + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder leftJoin() {
            this.queryBuilder.addToQuery(LEFT_JOIN + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder rightJoin() {
            this.queryBuilder.addToQuery(RIGHT_JOIN + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder comma() {
            this.queryBuilder.addToQuery(COMMA + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder leftBracket() {
            this.queryBuilder.addToQuery(LEFT_BRACKET + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder rightBracket() {
            this.queryBuilder.addToQuery(RIGHT_BRACKET + SPACE);
            return this.queryBuilder;
        }

        public QueryBuilder build() {
            this.queryBuilder.addToQuery(SEMICOLON);
            return this.queryBuilder;
        }
    }
}
