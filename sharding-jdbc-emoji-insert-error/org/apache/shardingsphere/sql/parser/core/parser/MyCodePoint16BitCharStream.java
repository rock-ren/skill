package org.apache.shardingsphere.sql.parser.core.parser;

import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.misc.Interval;

public class MyCodePoint16BitCharStream extends MyCodePointCharStream {
	private final char[] charArray;

	public MyCodePoint16BitCharStream(int position, int remaining, String name, char[] charArray, int arrayOffset) {
		super(position, remaining, name);
		this.charArray = charArray;
		// TODO
		assert arrayOffset == 0;
	}

	/** Return the UTF-16 encoded string for the given interval */
	@Override
	public String getText(Interval interval) {
		int startIdx = Math.min(interval.a, size);
		int len = Math.min(interval.b - interval.a + 1, size - startIdx);

		// We know there are no surrogates in this
		// array, since otherwise we would be given a
		// 32-bit int[] array.
		//
		// So, it's safe to treat this as if it were
		// UTF-16.
		return new String(charArray, startIdx, len);
	}

	@Override
	public int LA(int i) {
		int offset;
		switch (Integer.signum(i)) {
			case -1:
				offset = position + i;
				if (offset < 0) {
					return IntStream.EOF;
				}
				return charArray[offset] & 0xFFFF;
			case 0:
				// Undefined
				return 0;
			case 1:
				offset = position + i - 1;
				if (offset >= size) {
					return IntStream.EOF;
				}
				return charArray[offset] & 0xFFFF;
		}
		throw new UnsupportedOperationException("Not reached");
	}

	@Override
	Object getInternalStorage() {
		return charArray;
	}
}
