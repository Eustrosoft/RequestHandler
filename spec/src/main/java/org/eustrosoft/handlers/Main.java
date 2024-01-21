package org.eustrosoft.handlers;

import org.eustrosoft.handlers.cms.dto.RequestBlock;
import org.eustrosoft.handlers.sam.dto.SamRequestBlockDTO;
import org.eustrosoft.handlers.sam.dto.data.UserIdDTO2;

import java.lang.reflect.Field;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IllegalAccessException {
        RequestBlock requestBlock = new SamRequestBlockDTO("userId", new UserIdDTO2("12"));
        System.out.println(requestBlock.getClass().getTypeName());
        Class<?> superclass = requestBlock.getClass().getSuperclass();
        Field[] declaredFields = superclass.getDeclaredFields();
        Map<String, Object> map = new HashMap<>();
        for (Field field : declaredFields) {
            System.out.println(field.getType().getSimpleName());
            if (field.getType().getSimpleName().equals(Object.class.getSimpleName())) {
                field.setAccessible(true);
                getObjectString(field.get(superclass));
            }
        }
    }

    private static Field[] getAllHierarchyFields(Class<?> clazz) {
        Map<String, Object> fieldMap = new HashMap<>();
        List<Field> fields = new ArrayList<>();
        Class<?> cz = clazz;
        try {
            while (true) {
                Field[] declaredFields = cz.getDeclaredFields();
                fields.addAll(Arrays.asList(declaredFields));
                cz = cz.getSuperclass();
            }
        } catch (NullPointerException ex) {
            // all classes searched
        }
        return fields.toArray(new Field[0]);
    }

    private static String getObjectString(Object object) throws IllegalAccessException {
        Map<String, Object> classFields = getClassFields(object.getClass());
    }

    private static Map<String, Object> getClassFields(Class<?> clazz) throws IllegalAccessException {
        Map<String, Object> fieldsMap = new HashMap<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            System.out.println(field.getType().getSimpleName());
            JsonIgnore annotation = field.getAnnotation(JsonIgnore.class);
            if (annotation != null) {
                continue;
            }
            if (field.getType().getSimpleName().equals(Object.class.getSimpleName())) {
                getClassFields(field.getClass());
            }

            if (Collection.class.isAssignableFrom(field.getClass())) {
                field.setAccessible(true);
                Collection o = (Collection) field.get(clazz);

                fieldsMap.put(field.getName(), collectionToString(o));
            }
        }
    }

    private static String collectionToString(Collection<?> collection) throws IllegalAccessException {
        if (collection == null || collection.isEmpty()) {
            return "[]";
        }
        Object[] objects = collection.toArray();
        Object object = objects[0];

        List<String> finalStrings = new ArrayList<>(12);
        for (Object ob : objects) {
            String finalString;
            if (object instanceof Number) {
                finalString = getNumberValue((Number) ob);
            } else if (object instanceof CharSequence) {
                finalString = String.format("\"%s\"", ob.toString());
            } else {
                finalString = getObjectString(ob);
            }
            finalStrings.add(finalString);
        }
        return String.format("[%s]", String.join(", ", finalStrings));
    }

    private static String getNumberValue(Number number) {
        if (number instanceof Long || number instanceof Integer || number instanceof Short) {
            return String.format("%d", number.longValue());
        }
        if (number instanceof Float || number instanceof Double) {
            return String.format("%f.5", number.doubleValue());
        }
        if (number instanceof Byte) {
            return String.format("%b", number.byteValue());
        }
    }
}
