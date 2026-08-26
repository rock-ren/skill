package org.apache.shardingsphere.sql.parser.core.parser;

import java.nio.charset.StandardCharsets;

import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.misc.Interval;

public class MyCodePoint8BitCharStream extends MyCodePointCharStream {
	private final byte[] byteArray;

	public MyCodePoint8BitCharStream(int position, int remaining, String name, byte[] byteArray, int arrayOffset) {
		super(position, remaining, name);
		// TODO
		assert arrayOffset == 0;
		this.byteArray = byteArray;
	}

	/** Return the UTF-16 encoded string for the given interval */
	@Override
	public String getText(Interval interval) {
		int startIdx = Math.min(interval.a, size);
		int len = Math.min(interval.b - interval.a + 1, size - startIdx);

		// We know the maximum code point in byteArray is U+00FF,
		// so we can treat this as if it were ISO-8859-1, aka Latin-1,
		// which shares the same code points up to 0xFF.
		return new String(byteArray, startIdx, len, StandardCharsets.ISO_8859_1);
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
				return byteArray[offset] & 0xFF;
			case 0:
				// Undefined
				return 0;
			case 1:
				offset = position + i - 1;
				if (offset >= size) {
					return IntStream.EOF;
				}
				return byteArray[offset] & 0xFF;
		}
		throw new UnsupportedOperationException("Not reached");
	}

	@Override
	Object getInternalStorage() {
		return byteArray;
	}
}
