/**
 * Copyright (c) 2023, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package com.eustrosoft.cms.dbdatasource.queries;

import static com.eustrosoft.core.constants.SQLConstants.*;

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

        public Builder add(Long num) {
            this.query.addToQuery(num + SPACE);
            return this;
        }

        public Builder where(String condition) {
            this.query.addToQuery(
                    WHERE + SPACE + LEFT_BRACKET + condition + RIGHT_BRACKET + SPACE
            );
            return this;
        }

        public Builder where(Query query) {
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
