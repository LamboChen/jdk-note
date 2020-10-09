/*
 * Copyright (c) 1996, 2012, Oracle and/or its affiliates. All rights reserved.
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


/**
 * Abstract class for reading character streams.  The only methods that a
 * subclass must implement are read(char[], int, int) and close().  Most
 * subclasses, however, will override some of the methods defined here in order
 * to provide higher efficiency, additional functionality, or both.
 *
 * @author Mark Reinhold
 * @see BufferedReader
 * @see LineNumberReader
 * @see CharArrayReader
 * @see InputStreamReader
 * @see FileReader
 * @see FilterReader
 * @see PushbackReader
 * @see PipedReader
 * @see StringReader
 * @see Writer
 * @since JDK1.1
 */
// char stream reader
public abstract class Reader implements Readable, Closeable {

    /**
     * The object used to synchronize operations on this stream.  For
     * efficiency, a character-stream object may use an object other than
     * itself to protect critical sections.  A subclass should therefore use
     * the object in this field rather than <tt>this</tt> or a synchronized
     * method.
     */
    // 同步锁，用于同步此流上的操作的对象
    protected Object lock;

    /**
     * Creates a new character-stream reader whose critical sections will
     * synchronize on the reader itself.
     */
    protected Reader() {
        // 默认为对象锁，并且锁 this
        this.lock = this;
    }

    /**
     * Creates a new character-stream reader whose critical sections will
     * synchronize on the given object.
     *
     * @param lock The Object to synchronize on.
     */
    // 传入锁
    protected Reader(Object lock) {
        if (lock == null) {
            throw new NullPointerException();
        }
        this.lock = lock;
    }

    /**
     * Attempts to read characters into the specified character buffer.
     * The buffer is used as a repository of characters as-is: the only
     * changes made are the results of a put operation. No flipping or
     * rewinding of the buffer is performed.
     *
     * @param target the buffer to read characters into
     * @return The number of characters added to the buffer, or
     * -1 if this source of characters is at its end
     * @throws IOException                      if an I/O error occurs
     * @throws NullPointerException             if target is null
     * @throws java.nio.ReadOnlyBufferException if target is a read only buffer
     * @since 1.5
     */
    // 试图将字符读入指定的字符缓冲区,返回写入字符个数
    public int read(java.nio.CharBuffer target) throws IOException {
        // 获取缓冲区字符长度
        int len = target.remaining();
        char[] cbuf = new char[len];
        // 读取数据到 cbuf
        int n = read(cbuf, 0, len);
        if (n > 0)
            // 写入缓冲区
            target.put(cbuf, 0, n);
        // 返回写入字符个数
        return n;
    }

    /**
     * Reads a single character.  This method will block until a character is
     * available, an I/O error occurs, or the end of the stream is reached.
     *
     * <p> Subclasses that intend to support efficient single-character input
     * should override this method.
     *
     * @return The character read, as an integer in the range 0 to 65535
     * (<tt>0x00-0xffff</tt>), or -1 if the end of the stream has
     * been reached
     * @throws IOException If an I/O error occurs
     */
    // 读取一个字符
    public int read() throws IOException {
        char cb[] = new char[1];
        if (read(cb, 0, 1) == -1)
            return -1;
        else
            return cb[0];
    }

    /**
     * Reads characters into an array.  This method will block until some input
     * is available, an I/O error occurs, or the end of the stream is reached.
     *
     * @param cbuf Destination buffer
     * @return The number of characters read, or -1
     * if the end of the stream
     * has been reached
     * @throws IOException If an I/O error occurs
     */
    // 读取 cbuf.length 个字符到 cbuf 中
    public int read(char cbuf[]) throws IOException {
        return read(cbuf, 0, cbuf.length);
    }

    /**
     * Reads characters into a portion of an array.  This method will block
     * until some input is available, an I/O error occurs, or the end of the
     * stream is reached.
     *
     * @param cbuf Destination buffer
     * @param off  Offset at which to start storing characters
     * @param len  Maximum number of characters to read
     * @return The number of characters read, or -1 if the end of the
     * stream has been reached
     * @throws IOException If an I/O error occurs
     */
    // 从 off 位置开始读取 len 个字符写入 cbuf
    abstract public int read(char cbuf[], int off, int len) throws IOException;

    /**
     * Maximum skip-buffer size
     */
    // 最大跳过缓冲区大小 8k
    private static final int maxSkipBufferSize = 8192;

    /**
     * Skip buffer, null until allocated
     */
    // 跳过缓冲区，在分配之前为空
    private char skipBuffer[] = null;

    /**
     * Skips characters.  This method will block until some characters are
     * available, an I/O error occurs, or the end of the stream is reached.
     *
     * @param n The number of characters to skip
     * @return The number of characters actually skipped
     * @throws IllegalArgumentException If <code>n</code> is negative.
     * @throws IOException              If an I/O error occurs
     */
    // 跳过字符，忽略 n 个字符
    public long skip(long n) throws IOException {
        if (n < 0L)
            // n 必须为非负
            throw new IllegalArgumentException("skip value is negative");
        // 跳过字符个数不能大于 maxSkipBufferSize
        int nn = (int) Math.min(n, maxSkipBufferSize);
        // 加锁
        synchronized (lock) {
            if ((skipBuffer == null) || (skipBuffer.length < nn))
                // 确保跳过缓冲区的 char 数组长度
                skipBuffer = new char[nn];
            // 中间变量 r，确保 n 不变
            long r = n;
            while (r > 0) {
                // 获取数据写入 skipBuffer
                int nc = read(skipBuffer, 0, (int) Math.min(r, nn));
                if (nc == -1)
                    break;
                // 可能存在多次读取
                r -= nc;
            }
            // 返回忽略字符个数
            return n - r;
        }
    }

    /**
     * Tells whether this stream is ready to be read.
     *
     * @return True if the next read() is guaranteed not to block for input,
     * false otherwise.  Note that returning false does not guarantee that the
     * next read will block.
     * @throws IOException If an I/O error occurs
     */
    // 指示此流是否已准备好读取
    // 如果子类需要支持，则需要重写此方法
    public boolean ready() throws IOException {
        return false;
    }

    /**
     * Tells whether this stream supports the mark() operation. The default
     * implementation always returns false. Subclasses should override this
     * method.
     *
     * @return true if and only if this stream supports the mark operation.
     */
    // 标识此 stream 是否支持 mark() 操作
    // 默认不支持。子类需重写
    public boolean markSupported() {
        return false;
    }

    /**
     * Marks the present position in the stream.  Subsequent calls to reset()
     * will attempt to reposition the stream to this point.  Not all
     * character-input streams support the mark() operation.
     *
     * @param readAheadLimit Limit on the number of characters that may be
     *                       read while still preserving the mark.  After
     *                       reading this many characters, attempting to
     *                       reset the stream may fail.
     * @throws IOException If the stream does not support mark(),
     *                     or if some other I/O error occurs
     */
    // 标识当前 stream 的当前位置
    public void mark(int readAheadLimit) throws IOException {
        // 默认不支持
        throw new IOException("mark() not supported");
    }

    /**
     * Resets the stream.  If the stream has been marked, then attempt to
     * reposition it at the mark.  If the stream has not been marked, then
     * attempt to reset it in some way appropriate to the particular stream,
     * for example by repositioning it to its starting point.  Not all
     * character-input streams support the reset() operation, and some support
     * reset() without supporting mark().
     *
     * @throws IOException If the stream has not been marked,
     *                     or if the mark has been invalidated,
     *                     or if the stream does not support reset(),
     *                     or if some other I/O error occurs
     */
    // 重置 stream
    public void reset() throws IOException {
        throw new IOException("reset() not supported");
    }

    /**
     * Closes the stream and releases any system resources associated with
     * it.  Once the stream has been closed, further read(), ready(),
     * mark(), reset(), or skip() invocations will throw an IOException.
     * Closing a previously closed stream has no effect.
     *
     * @throws IOException If an I/O error occurs
     */
    // close stream
    abstract public void close() throws IOException;

}
