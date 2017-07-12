/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.datasource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 * @author Giovanni Ferrari <giovanni.ferrari@techrain.it>
 */
public class Bzip2MemDataSource extends ReadOnlyMemDataSource {

    public Bzip2MemDataSource() {
        super();
    }
    
    public Bzip2MemDataSource(byte[] content, String filename) {
        super();
        Objects.requireNonNull(filename);       
        String zipped = filename.substring(0,filename.lastIndexOf("."));
        String format = zipped.substring(zipped.lastIndexOf(".")+1);
        putData(null, format, content);
    }

    protected String getCompressionExt() {
        return ".bz2";
    }

    protected InputStream getCompressedInputStream(InputStream is) throws IOException {
        return new BZip2CompressorInputStream(new BufferedInputStream(is));
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
