/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.afs.nio2;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class CentralDirectory {

    private Path dir;

    public CentralDirectory(Path dir) {
        this.dir = Objects.requireNonNull(dir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String encodePath(String path) throws IOException {
        return URLEncoder.encode(path, StandardCharsets.UTF_8.name());
    }

    public void add(String id, String path) {
        try {
            Files.write(dir.resolve(id), path.getBytes(StandardCharsets.UTF_8));
            Files.write(dir.resolve(encodePath(path)), id.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(String id) {
        String path = getPath(id);
        try {
            Files.delete(dir.resolve(id));
            Files.delete(dir.resolve(encodePath(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readContent(Path path) throws IOException {
        Path entry = dir.resolve(path);
        if (Files.exists(entry)) {
            return new String(Files.readAllBytes(entry), StandardCharsets.UTF_8);
        }
        return null;
    }

    public String getId(String path) {
        try {
            return readContent(dir.resolve(encodePath(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPath(String id) {
        try {
            return readContent(dir.resolve(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

