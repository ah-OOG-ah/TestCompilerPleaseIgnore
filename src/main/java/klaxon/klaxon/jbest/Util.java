package klaxon.klaxon.jbest;

import static java.nio.charset.StandardCharsets.US_ASCII;

import it.unimi.dsi.fastutil.bytes.ByteImmutableList;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class Util {
    /// Treat a list of ints as a list of bytes. If any int is outside the byte range, this throws a
    public static ByteImmutableList bOfIs(int... ints) {
        byte[] inner = new byte[ints.length];
        int idx = 0;
        for (int i : ints) {
            if (i > 0xFF || i < 0) throw new IllegalArgumentException("Cannot make byte list of non-byte!");
            inner[idx++] = (byte) i;
        }
        return ByteImmutableList.of(inner);
    }

    /// Convert a 16-bit (short) to a byte list, little-endian
    public static ByteImmutableList bOfS(short i) {
        return ByteImmutableList.of(
                (byte) (i & 0xFF),
                (byte) (i >> 8 & 0xFF)
        );
    }

    /// Convert a 32-bit (int) to a byte list, little-endian
    public static ByteImmutableList bOfI(int i) {
        return ByteImmutableList.of(
                (byte) (i & 0xFF),
                (byte) (i >> 8 & 0xFF),
                (byte) (i >> 16 & 0xFF),
                (byte) (i >> 24 & 0xFF)
        );
    }

    /// Convert a 64-bit (long) address to a byte list, little-endian
    public static ByteImmutableList bOfL(long addr) {
        return ByteImmutableList.of(
                (byte) (addr & 0xFF),
                (byte) (addr >> 8 & 0xFF),
                (byte) (addr >> 16 & 0xFF),
                (byte) (addr >> 24 & 0xFF),
                (byte) (addr >> 32 & 0xFF),
                (byte) (addr >> 40 & 0xFF),
                (byte) (addr >> 48 & 0xFF),
                (byte) (addr >> 56 & 0xFF)
        );
    }

    /// Convert a string to a byte list, little-endian.
    /// @throws IllegalArgumentException if given a non-ASCII string.
    public static ByteImmutableList bOfASCII(String s) {
        if (!US_ASCII.newEncoder().canEncode(s))
            throw new IllegalArgumentException("Cannot encode non-ASCII string!");
        return ByteImmutableList.of(s.getBytes(US_ASCII));
    }
}
