package eu.itesla_project.iidm.datasource;

import org.junit.Test;

import static org.junit.Assert.*;

public class Bzip2MemDataSourceTest {
    
    @Test
    public void test(){
        ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource("data".getBytes(), "data.xiidm.bz2");
        assertTrue(mem.exists(null, "xiidm"));
    }
    
    @Test
    public void testFormat() {
            ReadOnlyMemDataSource mem = DataSourceUtil.createMemDataSource("data".getBytes(), "bz2","xiidm");
            assertTrue(mem.exists(null, "xiidm"));
    }

}
