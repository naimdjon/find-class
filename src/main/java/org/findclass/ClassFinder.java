package org.findclass;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
        setNewSearch(searchString);
        process(dir, null);
        return result;
    }

    private void setNewSearch(final String searchString) {
        this.searchString = searchString.toLowerCase();
        result.clear();
    }

    private void process(final File path, final MatchListener matchListener) throws IllegalArgumentException {
        if (!path.exists()) {
            throw new IllegalArgumentException(String.format("'%s' does not exist!", path.toString()));
        }
        final File[] jarsInCurrentDir = path.listFiles(pathname -> pathname.canRead() && pathname.isFile() && pathname.getName().endsWith(".jar"));
        if (jarsInCurrentDir != null) {
            for (final File candidateJarFile : jarsInCurrentDir) {
                addJarNameContainingClass(candidateJarFile, matchListener);
            }

        }
        if (isRecursive) {
            processRecursive(path, matchListener);
        }
    }

    private void processRecursive(File path, MatchListener matchListener) {
        if (Files.isSymbolicLink(path.toPath())) {
            return;
        }
        File[] files = path.listFiles(pathname -> pathname.canRead() && pathname.isDirectory());
        if (files != null) {
            for (final File file : files)
                process(file, matchListener);
        }
    }

    private void addJarNameContainingClass(final File p, final MatchListener matchListener) {
        final JarFile jar = toJarFile(p);
        if (jar == null) return;
        final Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            if (matchesSearchString(e.nextElement())) {
                result.add(jar.getName());
                if (matchListener != null) {
                    matchListener.onMatch(jar);
                }
                break;
            }
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

    public ClassFinder recursive() {
        return recursive(true);
    }

    public ClassFinder recursive(final boolean isRecursive) {
        this.isRecursive = isRecursive;
        return this;
    }

    public ClassFinder regex(final boolean isRegex) {
        this.isRegex = isRegex;
        return this;
    }

    public void collectMatches(final String searchString, final MatchListener matchListener) {
        setNewSearch(searchString);
        process(dir, matchListener);
    }
}
