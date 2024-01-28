package org.eustrosoft.spec.interfaces;

import org.eustrosoft.json.QJson;
import org.eustrosoft.json.exception.JsonException;

public interface JsonParsable<T> {
    T getDataFromJson(QJson qJson) throws JsonException;
}
