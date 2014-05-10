package org.findclass;


import java.io.File;
import java.util.Collection;

public class ClassFinder {
    final private  File dir;

    private ClassFinder(File dir) {
        this.dir = dir;
    }

    static ClassFinder searchIn(final File dir) {
        return new ClassFinder(dir);
    }

    public Collection<File> find() {
        return null;
    }
}
