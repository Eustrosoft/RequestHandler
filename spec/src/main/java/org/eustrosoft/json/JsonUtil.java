package org.eustrosoft.json;

import org.eustrosoft.date.DateTimeZone;
import org.eustrosoft.json.exception.JsonException;
import org.eustrosoft.json.exception.JsonNameException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.eustrosoft.json.Constants.JSON_DELIM;
import static org.eustrosoft.json.Constants.JSON_FORMAT_MASSIVE;
import static org.eustrosoft.json.Constants.JSON_FORMAT_PARAMS;
import static org.eustrosoft.json.Constants.JSON_FORMAT_STRING;
import static org.eustrosoft.json.Constants.JSON_NULL;
import static org.eustrosoft.json.JsonUtil.ParamUtil.*;
import static org.eustrosoft.reflection.Types.BIG_DECIMAL;
import static org.eustrosoft.reflection.Types.DOUBLE;
import static org.eustrosoft.reflection.Types.FLOAT;
import static org.eustrosoft.reflection.Types.INTEGER;
import static org.eustrosoft.reflection.Types.LONG;
import static org.eustrosoft.reflection.Types.SHORT;

public final class JsonUtil {

    private JsonUtil() {

    }

    public static String getFormatString(int count) {
        if (count < 1)
            return "{}";
        return "{" +
                IntStream.range(0, count).boxed().map(i -> "%s").collect(Collectors.joining(", ")) +
                "}";
    }

    public static String toJson(String format, Object... params) {
        return String.format(format, params);
    }

    public static String toJsonFormatted(Object... params) {
        return String.format(getFormatString(params.length), params);
    }

    /**
     * Class, helps to represent one value to a set of params
     * without outer brackets, but with inner brackets like massive
     */
    public static final class AsEntry {

        private AsEntry() {

        }

        public static String getDateParams(String name, DateTimeZone val) throws JsonException {
            checkName(name);
            return String.format(JSON_FORMAT_PARAMS, getString(name), getDate(val));
        }

        public static String getStringCollection(String name, Collection<String> val) throws JsonException {
            checkName(name);
            return String.format(JSON_FORMAT_PARAMS, getString(name), getCollectionString(val));
        }

        public static String getNumberCollection(String name, Collection<Number> val) throws JsonException {
            checkName(name);
            return String.format(JSON_FORMAT_PARAMS, getString(name), getCollectionNumber(val));
        }

        public static String getStringParams(String name, String val) throws JsonException {
            checkName(name);
            return String.format(JSON_FORMAT_PARAMS, getString(name), getString(val));
        }

        public static String getRawParams(String name, String val) throws JsonException {
            checkName(name);
            return String.format(JSON_FORMAT_PARAMS, getString(name), val);
        }

        public static String getNumberParams(String name, Number val) throws JsonException {
            checkName(name);
            String numberValue = JSON_NULL;
            String className = val.getClass().getName();
            if (className.equals(INTEGER)) {
                numberValue = getInteger(val.intValue());
            }
            if (className.equals(SHORT)) {
                numberValue = getShort(val.shortValue());
            }
            if (className.equals(LONG)) {
                numberValue = getLong(val.longValue());
            }
            if (className.equals(BIG_DECIMAL)) {
                numberValue = getBigDecimal((BigDecimal) val);
            }
            if (className.equals(FLOAT)) {
                numberValue = getFloat(val.floatValue());
            }
            if (className.equals(DOUBLE)) {
                numberValue = getDouble(val.doubleValue());
            }
            return String.format(JSON_FORMAT_PARAMS, getString(name), numberValue);
        }

        private static void checkName(String name) throws JsonException {
            if (name == null) {
                throw new JsonNameException();
            }
        }
    }

    /**
     * Class for processing params in Json
     * Helps to avoid nulls and process value with right format: with quotes or not
     */
    public static final class ParamUtil {

        private ParamUtil() {

        }

        public static <T extends Enum<?>> String getEnum(T value) {
            return getProcessedValue(value.name(), ParamUtil::quoteValue);
        }

        public static String getString(CharSequence value) {
            return getProcessedValue(value, ParamUtil::quoteValue);
        }

        public static String getCharacter(Character value) {
            return getProcessedValue(value, ParamUtil::quoteValue);
        }

        public static String getDate(DateTimeZone value) {
            return getProcessedValue(value.getStringDate(), ParamUtil::quoteValue);
        }

        public static String getCollectionString(Collection<String> collection) {
            return getProcessedValue(collection, ParamUtil::collectionStringValue);
        }

        public static String getCollectionNumber(Collection<Number> collection) {
            return getProcessedValue(collection, ParamUtil::collectionStringValue);
        }

        public static String getShort(Short value) {
            return getProcessedValue(value, Object::toString);
        }

        public static String getInteger(Integer value) {
            return getProcessedValue(value, Object::toString);
        }

        public static String getLong(Long value) {
            return getProcessedValue(value, Object::toString);
        }

        public static String getDouble(Double value) {
            return getProcessedValue(value, Object::toString);
        }

        public static String getFloat(Float value) {
            return getProcessedValue(value, Object::toString);
        }

        public static String getBigDecimal(BigDecimal value) {
            return getProcessedValue(value, Object::toString);
        }

        public static String getBoolean(Boolean value) {
            return getProcessedValue(value, Object::toString);
        }

        public static String quoteValue(Object value) {
            return String.format(JSON_FORMAT_STRING, value);
        }

        public static String collectionStringValue(Object collection) {
            Collection<String> coll = (Collection<String>) collection;
            String massive = coll.stream().map(ParamUtil::quoteValue).collect(Collectors.joining(JSON_DELIM));
            return toJson(JSON_FORMAT_MASSIVE, massive);
        }

        public static String collectionNumberValue(Object collection) {
            Collection<Number> coll = (Collection<Number>) collection;
            String collect = coll.stream().map(Object::toString).collect(Collectors.joining(JSON_DELIM));
            return toJson(JSON_FORMAT_MASSIVE, collect);
        }

        private static String getProcessedValue(Object value, Function<Object, String> process) {
            if (Objects.isNull(value)) {
                return Constants.JSON_NULL;
            }
            return process.apply(value);
        }
    }
}
