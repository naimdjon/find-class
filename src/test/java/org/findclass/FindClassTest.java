package org.findclass;

import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class FindClassTest {

    private Collection<File> result;

    @Before
    public void setup() throws IOException {
        final File dir = createDirectory("testdir");
        result=ClassFinder.searchIn(dir).find();
    }

    private File createDirectory(final String testdir) throws IOException {
        final File dir = new File(testdir);
        dir.mkdirs();
        final File classFile = new File(dir, "ThisClassShouldBefound.clas");
        classFile.createNewFile();
        Runtime.getRuntime().exec("jar cvf  test.jar *.class");
        return dir;
    }

    @org.junit.Test
    public void testFindClassInJar() throws Exception {

    }
}
