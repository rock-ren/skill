package org.apache.shardingsphere.sql.parser.core.parser;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

public class MyCodePointBufferBuilder {
	private MyCodeType type;
	private ByteBuffer byteBuffer;
	private CharBuffer charBuffer;
	private IntBuffer intBuffer;
	private int prevHighSurrogate;

	public MyCodePointBufferBuilder(int initialBufferSize) {
		type = MyCodeType.BYTE;
		byteBuffer = ByteBuffer.allocate(initialBufferSize);
		charBuffer = null;
		intBuffer = null;
		prevHighSurrogate = -1;
	}

	MyCodeType getType() {
		return type;
	}

	ByteBuffer getByteBuffer() {
		return byteBuffer;
	}

	CharBuffer getCharBuffer() {
		return charBuffer;
	}

	IntBuffer getIntBuffer() {
		return intBuffer;
	}

	public MyCodePointBuffer build() {
		switch (type) {
		case BYTE:
			byteBuffer.flip();
			break;
		case CHAR:
			charBuffer.flip();
			break;
		//case INT:
		//	intBuffer.flip();
		//	break;
		}
		return new MyCodePointBuffer(type, byteBuffer, charBuffer, intBuffer);
	}

	private static int roundUpToNextPowerOfTwo(int i) {
		int nextPowerOfTwo = 32 - Integer.numberOfLeadingZeros(i - 1);
		return (int) Math.pow(2, nextPowerOfTwo);
	}

	public void ensureRemaining(int remainingNeeded) {
		switch (type) {
		case BYTE:
			if (byteBuffer.remaining() < remainingNeeded) {
				int newCapacity = roundUpToNextPowerOfTwo(byteBuffer.capacity() + remainingNeeded);
				ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
				byteBuffer.flip();
				newBuffer.put(byteBuffer);
				byteBuffer = newBuffer;
			}
			break;
		case CHAR:
			if (charBuffer.remaining() < remainingNeeded) {
				int newCapacity = roundUpToNextPowerOfTwo(charBuffer.capacity() + remainingNeeded);
				CharBuffer newBuffer = CharBuffer.allocate(newCapacity);
				charBuffer.flip();
				newBuffer.put(charBuffer);
				charBuffer = newBuffer;
			}
			break;
//		case INT:
//			if (intBuffer.remaining() < remainingNeeded) {
//				int newCapacity = roundUpToNextPowerOfTwo(intBuffer.capacity() + remainingNeeded);
//				IntBuffer newBuffer = IntBuffer.allocate(newCapacity);
//				intBuffer.flip();
//				newBuffer.put(intBuffer);
//				intBuffer = newBuffer;
//			}
//			break;
		}
	}

	public void append(CharBuffer utf16In) {
		ensureRemaining(utf16In.remaining());
		if (utf16In.hasArray()) {
			appendArray(utf16In);
		} else {
			// TODO
			throw new UnsupportedOperationException("TODO");
		}
	}

	private void appendArray(CharBuffer utf16In) {
		assert utf16In.hasArray();

		switch (type) {
		case BYTE:
			appendArrayByte(utf16In);
			break;
		case CHAR:
			appendArrayChar(utf16In);
			break;
		//case INT:
		//	appendArrayInt(utf16In);
		//	break;
		}
	}

	private void appendArrayByte(CharBuffer utf16In) {
		assert prevHighSurrogate == -1;

		char[] in = utf16In.array();
		int inOffset = utf16In.arrayOffset() + utf16In.position();
		int inLimit = utf16In.arrayOffset() + utf16In.limit();

		byte[] outByte = byteBuffer.array();
		int outOffset = byteBuffer.arrayOffset() + byteBuffer.position();

		while (inOffset < inLimit) {
			char c = in[inOffset];
			if (c <= 0xFF) {
				outByte[outOffset] = (byte) (c & 0xFF);
			} else {
				utf16In.position(inOffset - utf16In.arrayOffset());
				byteBuffer.position(outOffset - byteBuffer.arrayOffset());
				//if (!Character.isHighSurrogate(c)) {
					byteToCharBuffer(utf16In.remaining());
					appendArrayChar(utf16In);
					return;
				//} else {
				//	byteToIntBuffer(utf16In.remaining());
				//	appendArrayInt(utf16In);
				//	return;
				//}
			}
			inOffset++;
			outOffset++;
		}

		utf16In.position(inOffset - utf16In.arrayOffset());
		byteBuffer.position(outOffset - byteBuffer.arrayOffset());
	}

	private void appendArrayChar(CharBuffer utf16In) {
		assert prevHighSurrogate == -1;

		char[] in = utf16In.array();
		int inOffset = utf16In.arrayOffset() + utf16In.position();
		int inLimit = utf16In.arrayOffset() + utf16In.limit();

		char[] outChar = charBuffer.array();
		int outOffset = charBuffer.arrayOffset() + charBuffer.position();

		while (inOffset < inLimit) {
			char c = in[inOffset];
//			if (!Character.isHighSurrogate(c)) {
				outChar[outOffset] = c;
//			} else {
//				utf16In.position(inOffset - utf16In.arrayOffset());
//				charBuffer.position(outOffset - charBuffer.arrayOffset());
//				charToIntBuffer(utf16In.remaining());
//				appendArrayInt(utf16In);
//				return;
//			}
			inOffset++;
			outOffset++;
		}

		utf16In.position(inOffset - utf16In.arrayOffset());
		charBuffer.position(outOffset - charBuffer.arrayOffset());
	}

//	private void appendArrayInt(CharBuffer utf16In) {
//		char[] in = utf16In.array();
//		int inOffset = utf16In.arrayOffset() + utf16In.position();
//		int inLimit = utf16In.arrayOffset() + utf16In.limit();
//
//		int[] outInt = intBuffer.array();
//		int outOffset = intBuffer.arrayOffset() + intBuffer.position();
//
//		while (inOffset < inLimit) {
//			char c = in[inOffset];
//			inOffset++;
//			if (prevHighSurrogate != -1) {
//				if (Character.isLowSurrogate(c)) {
//					outInt[outOffset] = Character.toCodePoint((char) prevHighSurrogate, c);
//					outOffset++;
//					prevHighSurrogate = -1;
//				} else {
//					// Dangling high surrogate
//					outInt[outOffset] = prevHighSurrogate;
//					outOffset++;
//					if (Character.isHighSurrogate(c)) {
//						prevHighSurrogate = c & 0xFFFF;
//					} else {
//						outInt[outOffset] = c & 0xFFFF;
//						outOffset++;
//						prevHighSurrogate = -1;
//					}
//				}
//			} else if (Character.isHighSurrogate(c)) {
//				prevHighSurrogate = c & 0xFFFF;
//			} else {
//				outInt[outOffset] = c & 0xFFFF;
//				outOffset++;
//			}
//		}
//
//		if (prevHighSurrogate != -1) {
//			// Dangling high surrogate
//			outInt[outOffset] = prevHighSurrogate & 0xFFFF;
//			outOffset++;
//		}
//
//		utf16In.position(inOffset - utf16In.arrayOffset());
//		intBuffer.position(outOffset - intBuffer.arrayOffset());
//	}

	private void byteToCharBuffer(int toAppend) {
		byteBuffer.flip();
		// CharBuffers hold twice as much per unit as ByteBuffers, so start
		// with half the capacity.
		CharBuffer newBuffer = CharBuffer
				.allocate(Math.max(byteBuffer.remaining() + toAppend, byteBuffer.capacity() / 2));
		while (byteBuffer.hasRemaining()) {
			newBuffer.put((char) (byteBuffer.get() & 0xFF));
		}
		type = MyCodeType.CHAR;
		byteBuffer = null;
		charBuffer = newBuffer;
	}

//	private void byteToIntBuffer(int toAppend) {
//		byteBuffer.flip();
//		// IntBuffers hold four times as much per unit as ByteBuffers, so
//		// start with one quarter the capacity.
//		IntBuffer newBuffer = IntBuffer
//				.allocate(Math.max(byteBuffer.remaining() + toAppend, byteBuffer.capacity() / 4));
//		while (byteBuffer.hasRemaining()) {
//			newBuffer.put(byteBuffer.get() & 0xFF);
//		}
//		type = MyCodeType.INT;
//		byteBuffer = null;
//		intBuffer = newBuffer;
//	}

//	private void charToIntBuffer(int toAppend) {
//		charBuffer.flip();
//		// IntBuffers hold two times as much per unit as ByteBuffers, so
//		// start with one half the capacity.
//		IntBuffer newBuffer = IntBuffer
//				.allocate(Math.max(charBuffer.remaining() + toAppend, charBuffer.capacity() / 2));
//		while (charBuffer.hasRemaining()) {
//			newBuffer.put(charBuffer.get() & 0xFFFF);
//		}
//		type = MyCodeType.INT;
//		charBuffer = null;
//		intBuffer = newBuffer;
//	}
}
