/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.datasource;

import eu.itesla_project.commons.io.ForwardingInputStream;
import eu.itesla_project.commons.io.ForwardingOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Giovanni Ferrari <giovanni.ferrari@techrain.it>
 */
public class ZipMemDataSource extends ReadOnlyMemDataSource {

    public ZipMemDataSource() {
        super();
    }
    
    public ZipMemDataSource(byte[] content) {
        super();
        Objects.requireNonNull(content);
        InputStream is = new ByteArrayInputStream(content);
       
        try( ZipInputStream zipStream = new ZipInputStream(is)) {
            ZipEntry entry = zipStream.getNextEntry();
            if(entry != null)
            {
                String entryname = entry.getName();
                String format = entryname.substring(entryname.lastIndexOf(".")+1);
                putData(null, format, content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getCompressionExt() {
        return ".zip";
    }

    protected InputStream getCompressedInputStream(InputStream is) throws IOException {

        if (is != null) {
            ZipInputStream zipStream = new ZipInputStream(is);
            ZipEntry entry = zipStream.getNextEntry();
            if(entry != null)
            {
                InputStream fis = new ForwardingInputStream<InputStream>(zipStream) {
                    @Override
                    public void close() throws IOException {
                        zipStream.close();
                    }
                };
                return fis;
            }
        }
        return null;
    }

    @Override
    public InputStream newInputStream(String suffix, String ext) throws IOException {
        return getCompressedInputStream(super.newInputStream(suffix, ext));
    }

    @Override
    public InputStream newInputStream(String fileName) throws IOException {
        return getCompressedInputStream(super.newInputStream(fileName));
    }
}
