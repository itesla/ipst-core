package eu.itesla_project.iidm.datasource;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Before;
import org.junit.Test;

public class ZipMemDataSourceTest {

    private byte[] data;
    
    @Before
    public void setUp() throws Exception {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(bao));){
            ZipEntry entry = new ZipEntry("data.xiidm");
            zip.putNextEntry(entry);
            zip.closeEntry();
            zip.close();
            data = bao.toByteArray();
        }
    }
    
    @Test
    public void testFilename() {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(data, "data.zip");
            assertTrue(mem.exists(null, "xiidm"));
    }

    @Test
    public void testFormat() {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(data, "zip","xiidm");
            assertTrue(mem.exists(null, "xiidm"));
    }
}
