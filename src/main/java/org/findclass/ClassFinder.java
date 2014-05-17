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
    final private File dir;
    private String searchString;
    private boolean isRegex = false;
    private boolean isRecursive = true;
    final Set<String> result = new LinkedHashSet<>();

    private ClassFinder(File dir) {
        this.dir = dir;
    }

    static ClassFinder searchIn(final String dir) {
        return searchIn(new File(dir));
    }

    static ClassFinder searchIn(final File dir) {
        return new ClassFinder(dir);
    }


    public Collection<String> findRegex(final String searchString) throws IOException {
        isRegex = true;
        return find(searchString);
    }

    public Collection<String> find(final String searchString) throws IOException {
        this.searchString = searchString;
        result.clear();
        process(dir.toPath());
        return result;
    }

    private void process(final Path path) throws IllegalArgumentException{
        try {
            if (!Files.exists(path)) {
                throw new IllegalArgumentException(String.format("'%s' does not exist!",path.toString()));
            }
            Files.list(path)
                    .filter(p -> p.getFileName().toString().endsWith(".jar"))
                    .forEach(this::addJarNameIfContainsClass);

            if (isRecursive) {
               String[] files=path.toFile().list((dir1, name) -> {
                   File f = new File(name);
                   return f.canRead() && f.isDirectory();
               });
                for (final String file : files)
                    process(new File(file).toPath());
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void addJarNameIfContainsClass(Path p) {
        final JarFile jar = toJarFile(p);
        final Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            if (matchesSearchString(e.nextElement()))
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

    private boolean matchesSearchString(final JarEntry entry) {
        final String entryName = entry.getName().toLowerCase();
        return entryName.contains(searchString)
                || entryName.contains(searchString.replace(".", "/"))
                || isRegex && entryName.matches(searchString)
                ;
    }


    public static URL loadResource(final String name) {
        return ClassFinder.class.getClassLoader().getResource(name);
    }

}
