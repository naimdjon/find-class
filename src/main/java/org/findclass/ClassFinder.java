package org.findclass;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {
    final private File dir;
    private String pattern;

    private ClassFinder(File dir) {
        this.dir = dir;
    }

    static ClassFinder searchIn(final File dir) {
        return new ClassFinder(dir);
    }

    public Collection<String> find(final String pattern) throws IOException {
        this.pattern = pattern;
        result.clear();
        System.out.println("Classfinder Files.list(dir.toPath())" + Arrays.toString(Files.list(dir.toPath()).toArray()));
        Files.list(dir.toPath())
                .filter(p -> p.getFileName()
                        .toString().endsWith(".jar"))
                .forEach(this::process);

        return result;
    }
    final Set<String> result=new LinkedHashSet<>();
    private void process(final Path p) {
        try {
            File j = p.toFile();
            JarFile jar = new JarFile(j);
            final Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                if (entry.getName().toLowerCase().contains(pattern)
                        || entry.getName().toLowerCase().contains(pattern.replace(".", "/"))
                        ) {
                    result.add(jar.getName());
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }
}
