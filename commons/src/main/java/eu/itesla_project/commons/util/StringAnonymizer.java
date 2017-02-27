/**
 * Copyright (c) 2017, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.commons.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author Geoffroy Jamgotchian <geoffroy.jamgotchian at rte-france.com>
 */
public class StringAnonymizer {

    private static final char DEFAULT_SEPARATOR = ';';

    private final BiMap<String, String> mapping = HashBiMap.create();

    public static String getAlpha(int num) {
        StringBuilder result = new StringBuilder();
        while (num > 0) {
            num--;
            int remainder = num % 26;
            char digit = (char) (remainder + 97);
            result.insert(0, digit);
            num = (num - remainder) / 26;
        }
        return result.toString();
    }

    public String anonymize(String str) {
        if (str == null) {
            return null;
        }
        String str2 = mapping.get(str);
        if (str2 == null) {
            str2 = getAlpha(mapping.size() + 1).toUpperCase();
            mapping.put(str, str2);
        }
        return str2;
    }

    public String deanonymize(String str) {
        if (str == null) {
            return null;
        }
        String str2 = mapping.inverse().get(str);
        if (str2 == null) {
            throw new RuntimeException("Mapping not found for anonymized string '" + str + "'");
        }
        return str2;
    }

    public void readCsv(BufferedReader reader) {
        readCsv(reader, DEFAULT_SEPARATOR);
    }

    public void readCsv(BufferedReader reader, char separator) {
        try {
            String strSep = Character.toString(separator);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(strSep);
                mapping.put(tokens[0], tokens[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeCsv(BufferedWriter writer) {
        writeCsv(writer, DEFAULT_SEPARATOR);
    }

    public void writeCsv(BufferedWriter writer, char separator) {
        String strSep = Character.toString(separator);
        mapping.forEach((s, s2) -> {
            try {
                writer.write(s);
                writer.write(strSep);
                writer.write(s2);
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
