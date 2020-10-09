/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.io;

import java.lang.annotation.Native;

/**
 * Package-private abstract class for the local filesystem abstraction.
 */
// 文件系统
abstract class FileSystem {

    /* -- Normalization and construction -- */

    /**
     * Return the local filesystem's name-separator character.
     */
    // 获取命名分隔符
    public abstract char getSeparator();

    /**
     * Return the local filesystem's path-separator character.
     */
    // 获取路径分隔符
    public abstract char getPathSeparator();

    /**
     * Convert the given pathname string to normal form.  If the string is
     * already in normal form then it is simply returned.
     */
    // 将给定的路径名字符串转换为普通格式，如果 path 已经是普通格式则直接返回
    public abstract String normalize(String path);

    /**
     * Compute the length of this pathname string's prefix.  The pathname
     * string must be in normal form.
     */
    // 计算路径名称的前缀长度
    public abstract int prefixLength(String path);

    /**
     * Resolve the child pathname string against the parent.
     * Both strings must be in normal form, and the result
     * will be in normal form.
     */
    // 针对父对象解析子路径名字符串。两个字符串必须是普通格式，并且返回结果也是普通格式
    public abstract String resolve(String parent, String child);

    /**
     * Return the parent pathname string to be used when the parent-directory
     * argument in one of the two-argument File constructors is the empty
     * pathname.
     */
    // 获取父路径
    public abstract String getDefaultParent();

    /**
     * Post-process the given URI path string if necessary.  This is used on
     * win32, e.g., to transform "/c:/foo" into "c:/foo".  The path string
     * still has slash separators; code in the File class will translate them
     * after this method returns.
     */
    // 对给定的URI路径字符串进行后处理
    public abstract String fromURIPath(String path);


    /* -- Path operations -- */

    /**
     * Tell whether or not the given abstract pathname is absolute.
     */
    // 是否绝对路径
    public abstract boolean isAbsolute(File f);

    /**
     * Resolve the given abstract pathname into absolute form.  Invoked by the
     * getAbsolutePath and getCanonicalPath methods in the File class.
     */
    // 将给定的抽象路径名解析为绝对路径形式
    public abstract String resolve(File f);

    // 将路径进行规范化
    public abstract String canonicalize(String path) throws IOException;


    /* -- Attribute accessors -- */

    /* Constants for simple boolean attributes */
    // 是否存在
    @Native public static final int BA_EXISTS    = 0x01;
    // 是否常规文件
    @Native public static final int BA_REGULAR   = 0x02;
    // 是否文件夹
    @Native public static final int BA_DIRECTORY = 0x04;
    // 是否隐藏文件
    @Native public static final int BA_HIDDEN    = 0x08;

    /**
     * Return the simple boolean attributes for the file or directory denoted
     * by the given abstract pathname, or zero if it does not exist or some
     * other I/O error occurs.
     */
    public abstract int getBooleanAttributes(File f);

    // 可读
    @Native public static final int ACCESS_READ    = 0x04;
    // 可写
    @Native public static final int ACCESS_WRITE   = 0x02;
    // 可执行
    @Native public static final int ACCESS_EXECUTE = 0x01;

    /**
     * Check whether the file or directory denoted by the given abstract
     * pathname may be accessed by this process.  The second argument specifies
     * which access, ACCESS_READ, ACCESS_WRITE or ACCESS_EXECUTE, to check.
     * Return false if access is denied or an I/O error occurs
     */
    // check 访问权限
    public abstract boolean checkAccess(File f, int access);
    /**
     * Set on or off the access permission (to owner only or to all) to the file
     * or directory denoted by the given abstract pathname, based on the parameters
     * enable, access and oweronly.
     */
    // 设置权限
    public abstract boolean setPermission(File f, int access, boolean enable, boolean owneronly);

    /**
     * Return the time at which the file or directory denoted by the given
     * abstract pathname was last modified, or zero if it does not exist or
     * some other I/O error occurs.
     */
    // 获取最后一次修改时间
    public abstract long getLastModifiedTime(File f);

    /**
     * Return the length in bytes of the file denoted by the given abstract
     * pathname, or zero if it does not exist, is a directory, or some other
     * I/O error occurs.
     */
    // 获取文件长度
    public abstract long getLength(File f);


    /* -- File operations -- */

    /**
     * Create a new empty file with the given pathname.  Return
     * <code>true</code> if the file was created and <code>false</code> if a
     * file or directory with the given pathname already exists.  Throw an
     * IOException if an I/O error occurs.
     */
    // 使用给定的路径名创建一个新的空文件
    public abstract boolean createFileExclusively(String pathname)
        throws IOException;

    /**
     * Delete the file or directory denoted by the given abstract pathname,
     * returning <code>true</code> if and only if the operation succeeds.
     */
    // 删除文件
    public abstract boolean delete(File f);

    /**
     * List the elements of the directory denoted by the given abstract
     * pathname.  Return an array of strings naming the elements of the
     * directory if successful; otherwise, return <code>null</code>.
     */
    // 列举给定路径下的文件夹列表
    public abstract String[] list(File f);

    /**
     * Create a new directory denoted by the given abstract pathname,
     * returning <code>true</code> if and only if the operation succeeds.
     */
    // 创建文件夹
    public abstract boolean createDirectory(File f);

    /**
     * Rename the file or directory denoted by the first abstract pathname to
     * the second abstract pathname, returning <code>true</code> if and only if
     * the operation succeeds.
     */
    // 重命名文件
    public abstract boolean rename(File f1, File f2);

    /**
     * Set the last-modified time of the file or directory denoted by the
     * given abstract pathname, returning <code>true</code> if and only if the
     * operation succeeds.
     */
    // 设置最后一次修改时间
    public abstract boolean setLastModifiedTime(File f, long time);

    /**
     * Mark the file or directory denoted by the given abstract pathname as
     * read-only, returning <code>true</code> if and only if the operation
     * succeeds.
     */
    // 设置只读
    public abstract boolean setReadOnly(File f);


    /* -- Filesystem interface -- */

    /**
     * List the available filesystem roots.
     */
    // 列出可用的文件系统根
    public abstract File[] listRoots();

    /* -- Disk usage -- */
    @Native public static final int SPACE_TOTAL  = 0;
    @Native public static final int SPACE_FREE   = 1;
    @Native public static final int SPACE_USABLE = 2;

    public abstract long getSpace(File f, int t);

    /* -- Basic infrastructure -- */

    /**
     * Compare two abstract pathnames lexicographically.
     */
    public abstract int compare(File f1, File f2);

    /**
     * Compute the hash code of an abstract pathname.
     */
    public abstract int hashCode(File f);

    // Flags for enabling/disabling performance optimizations for file
    // name canonicalization
    static boolean useCanonCaches      = true;
    static boolean useCanonPrefixCache = true;

    private static boolean getBooleanProperty(String prop, boolean defaultVal) {
        String val = System.getProperty(prop);
        if (val == null) return defaultVal;
        if (val.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    static {
        useCanonCaches      = getBooleanProperty("sun.io.useCanonCaches",
                                                 useCanonCaches);
        useCanonPrefixCache = getBooleanProperty("sun.io.useCanonPrefixCache",
                                                 useCanonPrefixCache);
    }
}
