/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.iidm.export;

import eu.itesla_project.commons.ITeslaException;
import eu.itesla_project.iidm.datasource.*;
import eu.itesla_project.iidm.network.Network;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * A utility class to work with IIDM exporters.
 *
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class Exporters {

    private Exporters() {
    }

    /**
     * Get all supported export formats.
     */
    public static Collection<String> getFormats() {
        List<String> formats = new ArrayList<>();
        for (Exporter e : ServiceLoader.load(Exporter.class)) {
            formats.add(e.getFormat());
        }
        return formats;
    }

    /**
     * Get an exporter.
     *
     * @param format the export format
     * @return the exporter if one exists for the given format or
     * <code>null</code> otherwise
     */
    public static Exporter getExporter(String format) {
        if (format == null) {
            throw new IllegalArgumentException("format is null");
        }
        for (Exporter e : ServiceLoader.load(Exporter.class)) {
            if (format.equals(e.getFormat())) {
                return e;
            }
        }
        return null;
    }

    public static DataSource createDataSource(Path directory, String fileNameOrBaseName) {
        return DataSourceUtil.createDataSource(directory, fileNameOrBaseName);
    }

    public static DataSource createDataSource(Path file) {
        Objects.requireNonNull(file);
        if (Files.exists(file) && !Files.isRegularFile(file)) {
            throw new UncheckedIOException(new IOException("File " + file + " already exists and is not a regular file"));
        }
        Path absFile = file.toAbsolutePath();
        return createDataSource(absFile.getParent(), absFile.getFileName().toString());
    }

    /**
     * A convenient method to export a model to a given format.
     *
     * @param format the export format
     * @param network the model
     * @param parameters some properties to configure the export
     * @param file the network file
     */
    public static void export(String format, Network network, Properties parameters, Path file) {
        DataSource dataSource = createDataSource(file);
        export(format, network, parameters, dataSource);
    }


    /**
     * A convenient method to export a model to a given format.
     *
     * @param format the export format
     * @param network the model
     * @param parameters some properties to configure the export
     * @param dataSource data source
     */
    public static void export(String format, Network network, Properties parameters, DataSource dataSource) {
        Exporter exporter = getExporter(format);
        if (exporter == null) {
            throw new ITeslaException("Export format " + format + " not supported");
        }
        exporter.export(network, parameters, dataSource);
    }

    /**
     * A convenient method to export a model to a given format.
     *
     * @param format the export format
     * @param network the model
     * @param parameters some properties to configure the export
     * @param directory the output directory where files are generated
     * @param baseName a base name for all generated files
     */
    public static void export(String format, Network network, Properties parameters, String directory, String baseName) {
        export(format, network, parameters, new FileDataSource(Paths.get(directory), baseName));
    }

}
