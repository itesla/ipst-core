/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class FileIcon {

    private final String name;

    private final byte[] data;

    static byte[] toByteArray(InputStream is) {
        try {
            return ByteStreams.toByteArray(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileIcon(String name, InputStream is) {
        this(name, toByteArray(is));
    }

    public FileIcon(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
}
