package org.findclass;

import java.io.File;
import java.util.Collection;

import static junit.framework.TestCase.assertNotNull;

public class FindClassTest {


    final String testdir = "src/test/resources";

    @org.junit.Test
    public void testFindClassInJar() throws Exception {
        Collection<String> files = ClassFinder.searchIn(new File(testdir)).find("test");
        assertNotNull(files);
    }
}
