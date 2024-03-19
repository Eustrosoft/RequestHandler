package com.eustrosoft.core.tools;

import java.util.ArrayList;
import java.util.List;

public final class CollectionsUtils {

    public static <T> List<T> asList(T... objects) {
        List<T> lst = new ArrayList<>();
        for (T obj : objects) {
            if (obj != null) {
                lst.add(obj);
            }
        }
        return lst;
    }

    private CollectionsUtils() {

    }
}
