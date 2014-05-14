package org.findclass;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {
    final private File dir;
    private String pattern;
    final Set<String> result = new LinkedHashSet<>();

    private ClassFinder(File dir) {
        this.dir = dir;
    }

    static ClassFinder searchIn(final File dir) {
        return new ClassFinder(dir);
    }

    public Collection<String> find(final String pattern) throws IOException {
        this.pattern = pattern;
        result.clear();
        process(dir.toPath());
        return result;
    }

    private void process(final Path path) {
        try {
            Files.list(path)
                    .filter(p -> p.getFileName().toString().endsWith(".jar"))
                    .forEach(this::addJarNameIfContainsClass);

            Files.list(path)
                    .filter(p -> p.toFile().canRead() && p.toFile().isDirectory())
                    .forEach(this::process);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void addJarNameIfContainsClass(Path p) {
        final JarFile jar = toJarFile(p);
        final Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            if (matchesPattern(e.nextElement()))
                result.add(jar.getName());
        }
    }

    private JarFile toJarFile(final Path p) {
        try {
            return new JarFile(p.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean matchesPattern(final JarEntry entry) {
        final String entryName = entry.getName().toLowerCase();
        return entryName.contains(pattern)
                || entryName.contains(pattern.replace(".", "/"));
    }
}
