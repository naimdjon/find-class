package org.findclass;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class FindClassTest {


    final String testdir = "src/test/resources";

    @org.junit.Test
    public void testFindClassInJar() throws Exception {
        Collection<String> files = ClassFinder.searchIn(new File(testdir)).find("test");
        assertNotNull(files);
        assertEquals(testdir+"/test.jar", files.iterator().next());
    }

    @org.junit.Test
    public void testFindsClassInJarRecursively() throws Exception {
        Collection<String> files = ClassFinder.searchIn(new File(testdir)).find("test");
        final List<String> list = Arrays.asList(testdir + "/test.jar", testdir + "/sub/sub2/test_under_sub2.jar");
        assertTrue(list.containsAll(files));
        assertTrue(files.containsAll(list));

    }

}
