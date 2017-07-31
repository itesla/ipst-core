/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.datasource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

/**
 * @author Giovanni Ferrari <giovanni.ferrari@techrain.it>
 */
public class Bzip2MemDataSource extends ReadOnlyMemDataSource {

    public Bzip2MemDataSource(String baseName) {
        super(baseName);
    }

    public Bzip2MemDataSource(InputStream content, String filename) {
    	super(DataSourceUtil.getBaseName(filename));
        String zipped = filename.substring(0,filename.lastIndexOf("."));
        try{
            putData(zipped, content);
        }
        catch(IOException ie)
        {
            throw new RuntimeException(ie);
        }
    }

    protected String getCompressionExt() {
        return ".bz2";
    }

    protected InputStream getCompressedInputStream(InputStream is) throws IOException {
        return new BZip2CompressorInputStream(new BufferedInputStream(is));
    }

    @Override
    public InputStream newInputStream(String suffix, String ext) throws IOException {
    	return newInputStream(DataSourceUtil.getFileName(getBaseName(), suffix, ext));
    }

    @Override
    public InputStream newInputStream(String fileName) throws IOException {
        return getCompressedInputStream(super.newInputStream(fileName));
    }
}
