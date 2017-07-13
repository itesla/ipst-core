package eu.itesla_project.iidm.datasource;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

public class ZipMemDataSourceTest {

    @Test
    public void test() {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(bao));){
            ZipEntry entry = new ZipEntry("data.xiidm");
            zip.putNextEntry(entry);
            zip.closeEntry();
            zip.close();

            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(bao.toByteArray(), "data.zip");
            assertTrue(mem.exists(null, "xiidm"));
        } catch (Exception ex) {
            fail();
        }
    }

}
