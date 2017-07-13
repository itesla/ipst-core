package eu.itesla_project.iidm.datasource;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReadOnlyMemDataSourceTest {
    
    @Test
    public void test(){
        ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource("data".getBytes(), "data.xiidm");
        assertTrue(mem.exists(null, "xiidm"));
    }
    
    @Test
    public void testFormat() {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource("data".getBytes(), "xiidm","xiidm");
            assertTrue(mem.exists(null, "xiidm"));
    }
}
