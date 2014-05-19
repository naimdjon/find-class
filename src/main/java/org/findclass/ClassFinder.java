package org.findclass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFinder {
    private final File dir;
    private String searchString;
    private boolean isRegex = false;
    private boolean isRecursive = true;
    private final Set<String> result = new LinkedHashSet<>();

    private ClassFinder(File dir) {
        this.dir = dir;
    }

    static ClassFinder searchIn(final String dir) {
        return searchIn(new File(dir));
    }

    static ClassFinder searchIn(final File dir) {
        return new ClassFinder(dir);
    }

    public Collection<String> find(final String searchString) throws IOException {
        this.searchString = searchString;
        result.clear();
        process(dir.toPath());
        return result;
    }

    private void process(final Path path) throws IllegalArgumentException {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException(String.format("'%s' does not exist!", path.toString()));
        }
        final File[] jarsInCurrentDir = path.toFile().listFiles(pathname -> pathname.canRead() && pathname.isFile() && pathname.getName().endsWith(".jar"));
        if (jarsInCurrentDir != null) {
            for (final File candidateJarFile : jarsInCurrentDir)
                addJarNameContainingClass(candidateJarFile);
        }
            /*Files.list(path) //too many open files
                    .filter(p -> p.getFileName().toString().endsWith(".jar"))
                    .forEach(this::addJarNameContainingClass);*/

        if (isRecursive) {
            File[] files = path.toFile().listFiles(pathname -> pathname.canRead() && pathname.isDirectory());
            if (files != null) {
                for (final File file : files)
                    process(file.toPath());
            }
        }
    }

    private void addJarNameContainingClass(final File p) {
        final JarFile jar = toJarFile(p);
        if(jar==null)return;
        final Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            if (matchesSearchString(e.nextElement()))
                result.add(jar.getName());
        }
    }

    private JarFile toJarFile(final File f) {
        try {
            return new JarFile(f);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean matchesSearchString(final JarEntry entry) {
        final String entryName = entry.getName().toLowerCase();
        return entryName.contains(searchString)
                || entryName.contains(searchString.replace(".", "/"))
                || (isRegex && entryName.matches(searchString))
                ;
    }


    public static URL loadResource(final String name) {
        return ClassFinder.class.getClassLoader().getResource(name);
    }

    public ClassFinder recursive(final boolean isRecursive) {
        this.isRecursive = isRecursive;
        return this;
    }

    public ClassFinder regex(final boolean isRegex) {
        this.isRegex = isRegex;
        return this;
    }
}
