/*
 * Copyright (c) 1996, 2011, Oracle and/or its affiliates. All rights reserved.
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
 * Abstract class for writing to character streams.  The only methods that a
 * subclass must implement are write(char[], int, int), flush(), and close().
 * Most subclasses, however, will override some of the methods defined here in
 * order to provide higher efficiency, additional functionality, or both.
 *
 * @author Mark Reinhold
 * @see Writer
 * @see BufferedWriter
 * @see CharArrayWriter
 * @see FilterWriter
 * @see OutputStreamWriter
 * @see FileWriter
 * @see PipedWriter
 * @see PrintWriter
 * @see StringWriter
 * @see Reader
 * @since JDK1.1
 */
// 用于写字符流的抽象类
public abstract class Writer implements Appendable, Closeable, Flushable {

    /**
     * Temporary buffer used to hold writes of strings and single characters
     */
    // 写缓冲区, 用于保存字符串和单个字符写入的临时缓冲区
    private char[] writeBuffer;

    /**
     * Size of writeBuffer, must be >= 1
     */
    // 写缓冲区大小，固定 1024
    private static final int WRITE_BUFFER_SIZE = 1024;

    /**
     * The object used to synchronize operations on this stream.  For
     * efficiency, a character-stream object may use an object other than
     * itself to protect critical sections.  A subclass should therefore use
     * the object in this field rather than <tt>this</tt> or a synchronized
     * method.
     */
    // 同步锁
    protected Object lock;

    /**
     * Creates a new character-stream writer whose critical sections will
     * synchronize on the writer itself.
     */
    // 无参数构造，仅允许子类访问
    protected Writer() {
        this.lock = this;
    }

    /**
     * Creates a new character-stream writer whose critical sections will
     * synchronize on the given object.
     *
     * @param lock Object to synchronize on
     */
    // 传入同步锁
    protected Writer(Object lock) {
        if (lock == null) {
            // lock 不能为空，对象锁
            throw new NullPointerException();
        }
        this.lock = lock;
    }

    /**
     * Writes a single character.  The character to be written is contained in
     * the 16 low-order bits of the given integer value; the 16 high-order bits
     * are ignored.
     *
     * <p> Subclasses that intend to support efficient single-character output
     * should override this method.
     *
     * @param c int specifying a character to be written
     * @throws IOException If an I/O error occurs
     */
    // 将 c 写入 stream
    public void write(int c) throws IOException {
        // 对 stream 加锁
        synchronized (lock) {
            if (writeBuffer == null) {
                // 初始化缓冲区
                writeBuffer = new char[WRITE_BUFFER_SIZE];
            }
            // 将 c 写入缓冲
            writeBuffer[0] = (char) c;
            // 将缓冲区中的 c 写入 stream
            write(writeBuffer, 0, 1);
        }
    }

    /**
     * Writes an array of characters.
     *
     * @param cbuf Array of characters to be written
     * @throws IOException If an I/O error occurs
     */
    // 将 cbuf 所有字符写入 stream
    public void write(char cbuf[]) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    /**
     * Writes a portion of an array of characters.
     *
     * @param cbuf Array of characters
     * @param off  Offset from which to start writing characters
     * @param len  Number of characters to write
     * @throws IOException If an I/O error occurs
     */
    // 将 cbuf 字符数组的 off -> len 部分写入流中
    abstract public void write(char cbuf[], int off, int len) throws IOException;

    /**
     * Writes a string.
     *
     * @param str String to be written
     * @throws IOException If an I/O error occurs
     */
    // 将 str 写入 stream
    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    /**
     * Writes a portion of a string.
     *
     * @param str A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     * @throws IndexOutOfBoundsException If <tt>off</tt> is negative, or <tt>len</tt> is negative,
     *                                   or <tt>off+len</tt> is negative or greater than the length
     *                                   of the given string
     * @throws IOException               If an I/O error occurs
     */
    // 将 str 的 off 位置起 len 个字符写入 stream
    public void write(String str, int off, int len) throws IOException {
        // 加锁
        synchronized (lock) {
            char cbuf[];
            // check len 与缓冲区最大长度，取较大值
            if (len <= WRITE_BUFFER_SIZE) {
                if (writeBuffer == null) {
                    writeBuffer = new char[WRITE_BUFFER_SIZE];
                }
                cbuf = writeBuffer;
            } else {    // Don't permanently allocate very large buffers.
                cbuf = new char[len];
            }
            // 从 str 中获取字符写入 cbuf
            str.getChars(off, (off + len), cbuf, 0);
            // 将 cbuf 写入 stream
            write(cbuf, 0, len);
        }
    }

    /**
     * Appends the specified character sequence to this writer.
     *
     * <p> An invocation of this method of the form <tt>out.append(csq)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.write(csq.toString()) </pre>
     *
     * <p> Depending on the specification of <tt>toString</tt> for the
     * character sequence <tt>csq</tt>, the entire sequence may not be
     * appended. For instance, invoking the <tt>toString</tt> method of a
     * character buffer will return a subsequence whose content depends upon
     * the buffer's position and limit.
     *
     * @param csq The character sequence to append.  If <tt>csq</tt> is
     *            <tt>null</tt>, then the four characters <tt>"null"</tt> are
     *            appended to this writer.
     * @return This writer
     * @throws IOException If an I/O error occurs
     * @since 1.5
     */
    // 向 stream 中追加 字符序列
    public Writer append(CharSequence csq) throws IOException {
        if (csq == null)
            // 字符序列为 null 则直接追加 "null" ????
            write("null");
        else
            // 追加字符
            write(csq.toString());
        // 返回当前 stream
        return this;
    }

    /**
     * Appends a subsequence of the specified character sequence to this writer.
     * <tt>Appendable</tt>.
     *
     * <p> An invocation of this method of the form <tt>out.append(csq, start,
     * end)</tt> when <tt>csq</tt> is not <tt>null</tt> behaves in exactly the
     * same way as the invocation
     *
     * <pre>
     *     out.write(csq.subSequence(start, end).toString()) </pre>
     *
     * @param csq   The character sequence from which a subsequence will be
     *              appended.  If <tt>csq</tt> is <tt>null</tt>, then characters
     *              will be appended as if <tt>csq</tt> contained the four
     *              characters <tt>"null"</tt>.
     * @param start The index of the first character in the subsequence
     * @param end   The index of the character following the last character in the
     *              subsequence
     * @return This writer
     * @throws IndexOutOfBoundsException If <tt>start</tt> or <tt>end</tt> are negative, <tt>start</tt>
     *                                   is greater than <tt>end</tt>, or <tt>end</tt> is greater than
     *                                   <tt>csq.length()</tt>
     * @throws IOException               If an I/O error occurs
     * @since 1.5
     */
    // 将字符序列 start->end 追加
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        CharSequence cs = (csq == null ? "null" : csq);
        write(cs.subSequence(start, end).toString());
        return this;
    }

    /**
     * Appends the specified character to this writer.
     *
     * <p> An invocation of this method of the form <tt>out.append(c)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     out.write(c) </pre>
     *
     * @param c The 16-bit character to append
     * @return This writer
     * @throws IOException If an I/O error occurs
     * @since 1.5
     */
    // 追加一个字符
    public Writer append(char c) throws IOException {
        write(c);
        return this;
    }

    /**
     * Flushes the stream.  If the stream has saved any characters from the
     * various write() methods in a buffer, write them immediately to their
     * intended destination.  Then, if that destination is another character or
     * byte stream, flush it.  Thus one flush() invocation will flush all the
     * buffers in a chain of Writers and OutputStreams.
     *
     * <p> If the intended destination of this stream is an abstraction provided
     * by the underlying operating system, for example a file, then flushing the
     * stream guarantees only that bytes previously written to the stream are
     * passed to the operating system for writing; it does not guarantee that
     * they are actually written to a physical device such as a disk drive.
     *
     * @throws IOException If an I/O error occurs
     */
    // 刷新当前 stream，强制将 缓冲区 数据进行写入
    // 如果流在缓冲区中保存了各种 write（）方法中的任何字符, 立即把它们写到预定的目的地
    // 然后，如果目标是另一个字符或字节流，则刷新它
    abstract public void flush() throws IOException;

    /**
     * Closes the stream, flushing it first. Once the stream has been closed,
     * further write() or flush() invocations will cause an IOException to be
     * thrown. Closing a previously closed stream has no effect.
     *
     * @throws IOException If an I/O error occurs
     */
    // 关闭 stream
    abstract public void close() throws IOException;

}
