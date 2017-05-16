package tree;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TaxonTest {
    @Test
    public void testShouldSetNameAndAutoName() {
        Taxon taxon = new Taxon("test.name", "test.autoname");

        assertEquals("test.name", taxon.name);
        assertEquals("test.autoname", taxon.auto_name);
    }
}
