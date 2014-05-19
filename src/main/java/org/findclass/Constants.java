package org.findclass;

import java.io.File;

public class Constants {
    public static final String LAST_USED_FILE = System.getProperty("user.dir").concat(File.separator).concat("find-class-fx");

    public enum Properties {
        Last_used_dir,
        Last_used_searchString,
        Last_used_isRegex,
        Last_used_isRecursive
    }
}
