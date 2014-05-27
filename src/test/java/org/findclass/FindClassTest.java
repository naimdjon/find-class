package org.findclass;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class FindClassTest {

    final String sep=File.separator;
    final String testdir = String.format("src%stest%sresources%s",sep,sep,sep,sep);

    @org.junit.Test
    public void testFindClassInJar() throws Exception {
        Collection<String> files = ClassFinder.searchIn(new File(testdir))
                .find("test");
        assertNotNull(files);
        assertEquals(testdir+"test.jar", files.iterator().next());
    }

    @org.junit.Test
    public void testFindsClassInJarRecursively() throws Exception {
        Collection<String> files = ClassFinder
                .searchIn(new File(testdir))
                .find("test");
        final List<String> list = Arrays.asList(testdir + "test.jar", testdir + String.format("sub%ssub2%stest_under_sub2.jar",sep,sep));
        assertTrue(list.containsAll(files));
        assertTrue(files.containsAll(list));

    }

    @org.junit.Test
    public void testFindClassNonRecursive() throws Exception {
        Collection<String> files = ClassFinder
                .searchIn(new File(testdir))
                .recursive(false)
                .find("test");
        assertEquals(1,files.size());
        assertEquals(testdir+"test.jar", files.iterator().next());
    }

    @org.junit.Test
    public void testFindClassRecursive() throws Exception {
        Collection<String> files = ClassFinder.searchIn(new File(testdir))
                .recursive()
                .find("test");
        assertEquals(2,files.size());
        assertEquals(testdir+"test.jar", files.iterator().next());
    }


}
