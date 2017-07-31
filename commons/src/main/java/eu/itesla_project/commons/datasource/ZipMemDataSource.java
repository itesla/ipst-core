/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.commons.datasource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteStreams;

/**
 * @author Giovanni Ferrari <giovanni.ferrari@techrain.it>
 */
public class ZipMemDataSource extends ReadOnlyMemDataSource {

    public ZipMemDataSource(InputStream is, String basename) {
        super(basename);
        Objects.requireNonNull(is);

        try( ZipInputStream zipStream = new ZipInputStream(is)) {
            ZipEntry entry = zipStream.getNextEntry();
            while(entry != null)
            {
                String entryname = entry.getName();
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                ByteStreams.copy(zipStream, bao);
                putData(entryname, bao.toByteArray());
                bao.close();
                entry = zipStream.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getCompressionExt() {
        return ".zip";
    }

    @Override
    public InputStream newInputStream(String suffix, String ext) throws IOException {
        return super.newInputStream(DataSourceUtil.getFileName(getBaseName(), suffix, ext));
    }

    @Override
    public InputStream newInputStream(String fileName) throws IOException {
    	return super.newInputStream(fileName);
    }

}
