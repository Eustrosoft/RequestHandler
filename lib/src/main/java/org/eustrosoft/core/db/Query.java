/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core.db;

import org.eustrosoft.core.constants.SQLConstants;

public class Query {
    private StringBuilder query;

    private Query() {
        this.query = new StringBuilder();
    }

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
        private Query query;

        public Builder() {
            this.query = new Query();
        }

        public Builder select() {
            this.query.addToQuery(SQLConstants.SELECT + SQLConstants.SPACE);
            return this;
        }

        public Builder all() {
            this.query.addToQuery(SQLConstants.ALL + SQLConstants.SPACE);
            return this;
        }

        public Builder variable() {
            this.query.addToQuery(SQLConstants.VARIABLE + SQLConstants.SPACE);
            return this;
        }

        public Builder from() {
            this.query.addToQuery(SQLConstants.FROM + SQLConstants.SPACE);
            return this;
        }

        public Builder on() {
            this.query.addToQuery(SQLConstants.ON + SQLConstants.SPACE);
            return this;
        }

        public Builder in() {
            this.query.addToQuery(SQLConstants.IN + SQLConstants.SPACE);
            return this;
        }

        public Builder add(String str) {
            this.query.addToQuery(str + SQLConstants.SPACE);
            return this;
        }

        public Builder add(Long num) {
            this.query.addToQuery(num + SQLConstants.SPACE);
            return this;
        }

        public Builder where(String condition) {
            this.query.addToQuery(
                    SQLConstants.WHERE + SQLConstants.SPACE + SQLConstants.LEFT_BRACKET + condition + SQLConstants.RIGHT_BRACKET + SQLConstants.SPACE
            );
            return this;
        }

        public Builder where(Query query) {
            this.query.addToQuery(
                    SQLConstants.WHERE + SQLConstants.SPACE + SQLConstants.LEFT_BRACKET + query + SQLConstants.RIGHT_BRACKET + SQLConstants.SPACE
            );
            return this;
        }

        public Builder object(Object obj) {
            this.query.addToQuery(obj.toString() + SQLConstants.SPACE);
            return this;
        }

        public Builder join() {
            this.query.addToQuery(SQLConstants.JOIN + SQLConstants.SPACE);
            return this;
        }

        public Builder leftJoin() {
            this.query.addToQuery(SQLConstants.LEFT_JOIN + SQLConstants.SPACE);
            return this;
        }

        public Builder rightJoin() {
            this.query.addToQuery(SQLConstants.RIGHT_JOIN + SQLConstants.SPACE);
            return this;
        }

        public Builder comma() {
            this.query.addToQuery(SQLConstants.COMMA + SQLConstants.SPACE);
            return this;
        }

        public Builder leftBracket() {
            this.query.addToQuery(SQLConstants.LEFT_BRACKET + SQLConstants.SPACE);
            return this;
        }

        public Builder eq() {
            this.query.addToQuery(SQLConstants.EQ + SQLConstants.SPACE);
            return this;
        }

        public Builder rightBracket() {
            this.query.addToQuery(SQLConstants.RIGHT_BRACKET + SQLConstants.SPACE);
            return this;
        }

        public Builder and() {
            this.query.addToQuery(SQLConstants.AND + SQLConstants.SPACE);
            return this;
        }

        public Query buildWithSemicolon() {
            this.query.addToQuery(SQLConstants.SEMICOLON);
            return this.query;
        }

        public Query build() {
            return this.query;
        }
    }
}
