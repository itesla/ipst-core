/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.datasource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Giovanni Ferrari <giovanni.ferrari@techrain.it>
 */
public class ReadOnlyMemDataSource implements ReadOnlyDataSource {

    static class Key {

        private String suffix;

        private String ext;

        Key(String suffix, String ext) {
            this.suffix = suffix;
            this.ext = ext;
        }

        @Override
        public int hashCode() {
            return Objects.hash(suffix, ext);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Key) {
                Key other = (Key) obj;
                return Objects.equals(suffix, other.suffix)
                        && Objects.equals(ext, other.ext);
            }
            return false;
        }

        @Override
        public String toString() {
            return Objects.toString(suffix) + "." + Objects.toString(ext);
        }

    }

    protected final Map<Key, byte[]> data = new HashMap<>();

    protected final Map<String, byte[]> data2 = new HashMap<>();

    public byte[] getData(String suffix, String ext) {
        return data.get(new Key(suffix, ext));
    }

    public byte[] getData(String fileName) {
        return data2.get(fileName);
    }

    public void putData(String suffix, String ext, byte[] data) {
        this.data.put(new Key(suffix, ext), data);
    }

    public void putData(String fileName, byte[] data) {
        this.data2.put(fileName, data);
    }

    @Override
    public String getBaseName() {
        return "";
    }

    @Override
    public boolean exists(String suffix, String ext) {
        return data.containsKey(new Key(suffix, ext));
    }

    @Override
    public boolean exists(String fileName) throws IOException {
        Objects.requireNonNull(fileName);
        return data2.containsKey(fileName);
    }

    @Override
    public InputStream newInputStream(String suffix, String ext) throws IOException {
        byte[] ba = data.get(new Key(suffix, ext));
        if (ba == null) {
            throw new IOException("*" + (suffix != null ? suffix : "") + "." + (ext != null ? ext : "") + " does not exist");
        }
        return new ByteArrayInputStream(ba);
    }

    @Override
    public InputStream newInputStream(String fileName) throws IOException {
        Objects.requireNonNull(fileName);
        byte[] ba = data2.get(fileName);
        if (ba == null) {
            throw new IOException(fileName + " does not exist");
        }
        return new ByteArrayInputStream(ba);
    }
}
