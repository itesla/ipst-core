package eu.itesla_project.commons.datasource;

import org.junit.Test;

import eu.itesla_project.commons.datasource.ReadOnlyMemDataSource;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class GzMemDataSourceTest {

    @Test
    public void test() {
        try {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(new ByteArrayInputStream("data".getBytes()), "data.xiidm.gz");
            assertTrue(mem.exists("data.xiidm"));
        } catch (IOException e) {
        }
    }

    @Test
    public void testFormat() {
        try {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource(new ByteArrayInputStream("data".getBytes()), "", "gz", "xiidm");
            assertTrue(mem.exists(null, "xiidm"));
        } catch (IOException e) {
        }
    }

}
