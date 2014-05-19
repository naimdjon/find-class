package org.findclass;

import java.util.jar.JarFile;

public interface MatchListener {

    void onMatch(final JarFile jarFile);

}
