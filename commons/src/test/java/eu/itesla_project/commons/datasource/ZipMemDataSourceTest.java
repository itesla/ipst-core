package eu.itesla_project.commons.datasource;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Before;
import org.junit.Test;

import eu.itesla_project.commons.datasource.ReadOnlyMemDataSource;

public class ZipMemDataSourceTest {

    private byte[] data;

    @Before
    public void setUp() throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(bao));) {
            ZipEntry entry = new ZipEntry("one.xiidm");
            zip.putNextEntry(entry);
            zip.closeEntry();
            ZipEntry entry2 = new ZipEntry("two.xiidm");
            zip.putNextEntry(entry2);
            zip.closeEntry();
            zip.close();
            data = bao.toByteArray();
        }
    }

    @Test
    public void testFilename() {
        try {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(new ByteArrayInputStream(data), "data.zip");
            assertTrue(mem.exists("two.xiidm"));
        } catch (IOException e) {
        }
    }

    @Test
    public void testFormat() {
        try {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(new ByteArrayInputStream(data), "one", "zip", "xiidm");
            assertTrue(mem.exists(null, "xiidm"));
        } catch (IOException e) {
        }
    }
}
