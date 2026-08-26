package org.apache.shardingsphere.sql.parser.core.parser;

import java.nio.CharBuffer;

import org.antlr.v4.runtime.IntStream;

public class MyCharStreams {
	public static MyCodePointCharStream fromString(String s) {
		return fromString(s, IntStream.UNKNOWN_SOURCE_NAME);
	}
	
	public static MyCodePointCharStream fromString(String s, String sourceName) {
		// Initial guess assumes no code points > U+FFFF: one code
		// point for each code unit in the string
		MyCodePointBufferBuilder codePointBufferBuilder = MyCodePointBuffer.builder(s.length());
		// TODO: CharBuffer.wrap(String) rightfully returns a read-only buffer
		// which doesn't expose its array, so we make a copy.
		CharBuffer cb = CharBuffer.allocate(s.length());
		cb.put(s);
		cb.flip();
		codePointBufferBuilder.append(cb);
		return fromBuffer(codePointBufferBuilder.build(), sourceName);
	}
	
	public static MyCodePointCharStream fromBuffer(MyCodePointBuffer codePointBuffer, String name) {
		// Java lacks generics on primitive types.
		//
		// To avoid lots of calls to virtual methods in the
		// very hot codepath of LA() below, we construct one
		// of three concrete subclasses.
		//
		// The concrete subclasses directly access the code
		// points stored in the underlying array (byte[],
		// char[], or int[]), so we can avoid lots of virtual
		// method calls to ByteBuffer.get(offset).
		switch (codePointBuffer.getType()) {
			case BYTE:
				return new MyCodePoint8BitCharStream(
						codePointBuffer.position(),
						codePointBuffer.remaining(),
						name,
						codePointBuffer.byteArray(),
						codePointBuffer.arrayOffset());
			case CHAR:
				return new MyCodePoint16BitCharStream(
						codePointBuffer.position(),
						codePointBuffer.remaining(),
						name,
						codePointBuffer.charArray(),
						codePointBuffer.arrayOffset());
//			case INT:
//				return new MyCodePoint16BitCharStream(
//						codePointBuffer.position(),
//						codePointBuffer.remaining(),
//						name,
//						codePointBuffer.charArray(),
//						codePointBuffer.arrayOffset());
//				/*
//				return new CodePoint32BitCharStream(
//						codePointBuffer.position(),
//						codePointBuffer.remaining(),
//						name,
//						codePointBuffer.intArray(),
//						codePointBuffer.arrayOffset());
//				*/
		}
		throw new UnsupportedOperationException("Not reached");
	}
	
}
