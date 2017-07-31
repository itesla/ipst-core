package eu.itesla_project.commons.datasource;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.junit.Test;

import eu.itesla_project.commons.datasource.ReadOnlyMemDataSource;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Bzip2MemDataSourceTest {

    @Test
    public void test() {
        try {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(new ByteArrayInputStream("data".getBytes()), "data.xiidm.bz2");
            assertTrue(mem.exists("data.xiidm"));
        } catch (IOException e) {
        }
    }

    @Test
    public void testFormat() {
        try {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(new ByteArrayInputStream("data".getBytes()), "data", "bz2", "xiidm");
            assertTrue(mem.exists(null, "xiidm"));
        } catch (IOException e) {
        }
    }

}
