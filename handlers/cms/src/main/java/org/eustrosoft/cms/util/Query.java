package org.eustrosoft.cms.util;

public class Query {
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

    public static Query.Builder builder() {
        return new Query.Builder();
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

    public static class Builder {
        private Query query;

        public Builder() {
            this.query = new Query();
        }

        public Query.Builder select() {
            this.query.addToQuery(SELECT + SPACE);
            return this;
        }

        public Query.Builder all() {
            this.query.addToQuery(ALL + SPACE);
            return this;
        }

        public Query.Builder variable() {
            this.query.addToQuery(VARIABLE + SPACE);
            return this;
        }

        public Query.Builder from() {
            this.query.addToQuery(FROM + SPACE);
            return this;
        }

        public Query.Builder on() {
            this.query.addToQuery(ON + SPACE);
            return this;
        }

        public Query.Builder in() {
            this.query.addToQuery(IN + SPACE);
            return this;
        }

        public Query.Builder add(String str) {
            this.query.addToQuery(str + SPACE);
            return this;
        }

        public Query.Builder where(String condition) {
            this.query.addToQuery(
                    WHERE + SPACE + LEFT_BRACKET + condition + RIGHT_BRACKET + SPACE
            );
            return this;
        }

        public Query.Builder where(Query query) {
            this.query.addToQuery(
                    WHERE + SPACE + LEFT_BRACKET + query + RIGHT_BRACKET + SPACE
            );
            return this;
        }

        public Query.Builder object(Object obj) {
            this.query.addToQuery(obj.toString() + SPACE);
            return this;
        }

        public Query.Builder join() {
            this.query.addToQuery(JOIN + SPACE);
            return this;
        }

        public Query.Builder leftJoin() {
            this.query.addToQuery(LEFT_JOIN + SPACE);
            return this;
        }

        public Query.Builder rightJoin() {
            this.query.addToQuery(RIGHT_JOIN + SPACE);
            return this;
        }

        public Query.Builder comma() {
            this.query.addToQuery(COMMA + SPACE);
            return this;
        }

        public Query.Builder leftBracket() {
            this.query.addToQuery(LEFT_BRACKET + SPACE);
            return this;
        }

        public Query.Builder eq() {
            this.query.addToQuery(EQ + SPACE);
            return this;
        }

        public Query.Builder rightBracket() {
            this.query.addToQuery(RIGHT_BRACKET + SPACE);
            return this;
        }

        public Query.Builder and() {
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
