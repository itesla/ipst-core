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
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class MemDataSource extends ReadOnlyMemDataSource implements DataSource{


    @Override
    public OutputStream newOutputStream(final String suffix, final String ext, boolean append) throws IOException {
        final Key key = new Key(suffix, ext);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (append) {
            byte[] ba = data.get(new Key(suffix, ext));
            if (ba != null) {
                os.write(ba, 0, ba.length);
            }
        }
        return new ObservableOutputStream(os, key.toString(), new AbstractDataSourceObserver() {
            @Override
            public void closed(String streamName) {
                data.put(key, os.toByteArray());
            }
        });
    }

    @Override
    public OutputStream newOutputStream(String fileName, boolean append) throws IOException {
        Objects.requireNonNull(fileName);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        if (append) {
            byte[] ba = data2.get(fileName);
            if (ba != null) {
                os.write(ba, 0, ba.length);
            }
        }
        return new ObservableOutputStream(os, fileName, new AbstractDataSourceObserver() {
            @Override
            public void closed(String streamName) {
                data2.put(fileName, os.toByteArray());
            }
        });
    }

    
}
