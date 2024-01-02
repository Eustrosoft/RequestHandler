/**
 * Copyright (c) 2024, Yadzuka & EustroSoft.org
 * This file is part of RequestHandler project.
 * See the LICENSE file at the project root for licensing information.
 */

package org.eustrosoft.core;

public interface ResponseBlock {
    String getS();

    String getR();

    Short getE();

    String getM();

    String getL();

    default QJson toJson() {
        QJson object = new QJson();
        object.addItem("s", getS());
        object.addItem("e", getE());
        object.addItem("r", getR());
        object.addItem("m", getM());
        object.addItem("l", getL());
        return object;
    }
}
