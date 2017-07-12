/**
 * Copyright (c) 2016, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.datasource;

import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian@rte-france.com>
 */
public interface DataSourceUtil {

    OpenOption[] DEFAULT_OPEN_OPTIONS = { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING };
    OpenOption[] APPEND_OPEN_OPTIONS = { StandardOpenOption.APPEND };

    static String getFileName(String baseName, String suffix, String ext) {
        return baseName + (suffix != null ? suffix : "") + (ext != null ? "." + ext : "");
    }

    static OpenOption[] getOpenOptions(boolean append) {
        return append ? APPEND_OPEN_OPTIONS : DEFAULT_OPEN_OPTIONS;
    }

    static String getBaseName(Path file) {
        return getBaseName(file.getFileName().toString());
    }

    static String getBaseName(String fileName) {
        int pos = fileName.indexOf('.'); // find first dot in case of double extension (.xml.gz)
        return pos == -1 ? fileName : fileName.substring(0, pos);
    }

    static DataSource createDataSource(Path directory, String fileNameOrBaseName, DataSourceObserver observer) {
        Objects.requireNonNull(directory);
        Objects.requireNonNull(fileNameOrBaseName);

        if (fileNameOrBaseName.endsWith(".zip")) {
            return new ZipFileDataSource(directory, getBaseName(fileNameOrBaseName.substring(0, fileNameOrBaseName.length() - 4)), observer);
        } else if (fileNameOrBaseName.endsWith(".gz")) {
            return new GzFileDataSource(directory, getBaseName(fileNameOrBaseName.substring(0, fileNameOrBaseName.length() - 3)), observer);
        } else if (fileNameOrBaseName.endsWith(".bz2")) {
            return new Bzip2FileDataSource(directory, getBaseName(fileNameOrBaseName.substring(0, fileNameOrBaseName.length() - 4)), observer);
        } else {
            return new FileDataSource(directory, getBaseName(fileNameOrBaseName), observer);
        }
    }
    
    static ReadOnlyMemDataSource createMemDataSource(byte[] data, String extension, String format) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(extension);
        ReadOnlyMemDataSource mem = null;
        if (extension.equalsIgnoreCase("zip")) {
            mem = new ZipMemDataSource();
        } else 
        if (extension.equalsIgnoreCase("gz")) {
            mem = new GzMemDataSource();
        } else if (extension.equalsIgnoreCase("bz2")) {
           mem = new Bzip2MemDataSource();
        } else {
            mem = new MemDataSource();
        }
        mem.putData(null, format, data);
        return mem;
    }
    
    static ReadOnlyMemDataSource createMemDataSource(byte[] data, String filename) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(filename);
        ReadOnlyMemDataSource mem = null;
        if (filename.endsWith(".zip")) {
            mem = new ZipMemDataSource(data);
        } else 
        if (filename.endsWith(".gz")) {
            mem = new GzMemDataSource(data, filename);
        } else if (filename.endsWith(".bz2")) {
           mem = new Bzip2MemDataSource(data, filename);
        } else {
            mem = new ReadOnlyMemDataSource();
            String format = filename.substring(filename.lastIndexOf(".")+1);
            mem.putData(null, format, data);
        }
        return mem;
    }
    
}
