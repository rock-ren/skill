package org.apache.shardingsphere.sql.parser.core.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.misc.Interval;

public abstract class MyCodePointCharStream implements CharStream {
	protected final int size;
	protected final String name;

	// To avoid lots of virtual method calls, we directly access
	// the state of the underlying code points in the
	// CodePointBuffer.
	protected int position;

	// Use the factory method {@link #fromBuffer(CodePointBuffer)} to
	// construct instances of this type.
	protected MyCodePointCharStream(int position, int remaining, String name) {
		// TODO
		assert position == 0;
		this.size = remaining;
		this.name = name;
		this.position = 0;
	}

	// Visible for testing.
	abstract Object getInternalStorage();

	/**
	 * Constructs a {@link CodePointCharStream} which provides access
	 * to the Unicode code points stored in {@code codePointBuffer}.
	 */

	/**
	 * Constructs a named {@link CodePointCharStream} which provides access
	 * to the Unicode code points stored in {@code codePointBuffer}.
	 */

	@Override
	public final void consume() {
		if (size - position == 0) {
			assert LA(1) == IntStream.EOF;
			throw new IllegalStateException("cannot consume EOF");
		}
		position = position + 1;
	}

	@Override
	public final int index() {
		return position;
	}

	@Override
	public final int size() {
		return size;
	}

	/** mark/release do nothing; we have entire buffer */
	@Override
	public final int mark() {
		return -1;
	}

	@Override
	public final void release(int marker) {
	}

	@Override
	public final void seek(int index) {
		position = index;
	}

	@Override
	public final String getSourceName() {
		if (name == null || name.isEmpty()) {
			return UNKNOWN_SOURCE_NAME;
		}

		return name;
	}

	@Override
	public final String toString() {
		return getText(Interval.of(0, size - 1));
	}

}
