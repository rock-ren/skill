package org.apache.shardingsphere.sql.parser.core.parser;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;


public class MyCodePointBuffer {
//	public enum Type {
//		BYTE, CHAR//, INT
//	}

	private final MyCodeType type;
	private final ByteBuffer byteBuffer;
	private final CharBuffer charBuffer;
	//private final IntBuffer intBuffer;

	public MyCodePointBuffer(MyCodeType type, ByteBuffer byteBuffer, CharBuffer charBuffer, IntBuffer intBuffer) {
		this.type = type;
		this.byteBuffer = byteBuffer;
		this.charBuffer = charBuffer;
		//this.intBuffer = intBuffer;
	}

	public static MyCodePointBuffer withBytes(ByteBuffer byteBuffer) {
		return new MyCodePointBuffer(MyCodeType.BYTE, byteBuffer, null, null);
	}

	public static MyCodePointBuffer withChars(CharBuffer charBuffer) {
		return new MyCodePointBuffer(MyCodeType.CHAR, null, charBuffer, null);
	}

	//public static MyCodePointBuffer withInts(IntBuffer intBuffer) {
	//	return new MyCodePointBuffer(MyCodeType.INT, null, null, intBuffer);
	//}

	public int position() {
		switch (type) {
		case BYTE:
			return byteBuffer.position();
		case CHAR:
			return charBuffer.position();
		//case INT:
		//	return intBuffer.position();
		}
		throw new UnsupportedOperationException("Not reached");
	}

	public void position(int newPosition) {
		switch (type) {
		case BYTE:
			byteBuffer.position(newPosition);
			break;
		case CHAR:
			charBuffer.position(newPosition);
			break;
		//case INT:
		//	intBuffer.position(newPosition);
		//	break;
		}
	}

	public int remaining() {
		switch (type) {
		case BYTE:
			return byteBuffer.remaining();
		case CHAR:
			return charBuffer.remaining();
		//case INT:
		//	return intBuffer.remaining();
		}
		throw new UnsupportedOperationException("Not reached");
	}

	public int get(int offset) {
		switch (type) {
		case BYTE:
			return byteBuffer.get(offset);
		case CHAR:
			return charBuffer.get(offset);
		//case INT:
		//	return intBuffer.get(offset);
		}
		throw new UnsupportedOperationException("Not reached");
	}

	MyCodeType getType() {
		return type;
	}

	int arrayOffset() {
		switch (type) {
		case BYTE:
			return byteBuffer.arrayOffset();
		case CHAR:
			return charBuffer.arrayOffset();
		//case INT:
		//	return intBuffer.arrayOffset();
		}
		throw new UnsupportedOperationException("Not reached");
	}

	byte[] byteArray() {
		assert type == MyCodeType.BYTE;
		return byteBuffer.array();
	}

	char[] charArray() {
		assert type == MyCodeType.CHAR;
		return charBuffer.array();
	}

	//int[] intArray() {
	//	assert type == MyCodeType.INT;
	//	return intBuffer.array();
	//}

	public static MyCodePointBufferBuilder builder(int initialBufferSize) {
		return new MyCodePointBufferBuilder(initialBufferSize);
	}

}
