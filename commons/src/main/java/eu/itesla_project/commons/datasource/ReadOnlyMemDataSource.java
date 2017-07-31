/**
 * Copyright (c) 2016, All partners of the iTesla project (http://www.itesla-project.eu/consortium)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package eu.itesla_project.commons.datasource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

/**
 * @author Giovanni Ferrari <giovanni.ferrari@techrain.it>
 */
public class ReadOnlyMemDataSource implements ReadOnlyDataSource {

	protected final Map<String, byte[]> data = new HashMap<>();
	private final String baseName;

	public ReadOnlyMemDataSource() {
		this.baseName = "";
	}

	public ReadOnlyMemDataSource(String baseName) {
		this.baseName = Objects.requireNonNull(baseName);
	}

	public byte[] getData(String suffix, String ext) {
		return getData(DataSourceUtil.getFileName(baseName, suffix, ext));
	}

	public byte[] getData(String fileName) {
		return data.get(fileName);
	}

	public void putData(String fileName, InputStream data) throws IOException {
		putData(fileName, IOUtils.toByteArray(data));
	}

	public void putData(String fileName, byte[] data) {
		this.data.put(fileName, data);
	}

	@Override
	public String getBaseName() {
		return baseName;
	}

	@Override
	public boolean exists(String suffix, String ext) throws IOException {
		return exists(DataSourceUtil.getFileName(baseName, suffix, ext));
	}

	@Override
	public boolean exists(String fileName) throws IOException {
		Objects.requireNonNull(fileName);
		return data.containsKey(fileName);
	}

	@Override
	public InputStream newInputStream(String suffix, String ext) throws IOException {
		return newInputStream(DataSourceUtil.getFileName(baseName, suffix, ext));
	}

	@Override
	public InputStream newInputStream(String fileName) throws IOException {
		Objects.requireNonNull(fileName);
		byte[] ba = data.get(fileName);
		if (ba == null) {
			throw new IOException(fileName + " does not exist");
		}
		return new ByteArrayInputStream(ba);
	}
}
