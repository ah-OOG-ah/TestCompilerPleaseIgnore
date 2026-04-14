package klaxon.klaxon.jbest.codegen;

import static klaxon.klaxon.jbest.Util.bOfI;
import static klaxon.klaxon.jbest.Util.bOfIs;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteImmutableList;

public class Amd64Ops {
    public static final byte REX = 0b0100_0000;
    /// When REX_R is present, ModR/M:reg is extended
    public static final byte REX_R = 0b100;
    /// When REX_B is present, ModR/M:r/m is extended
    public static final byte REX_B = 0b001;
    public enum Register {
        RAX("eax", (byte) 0b000),
        RBX("ebx", (byte) 0b011),
        RCX("ecx", (byte) 0b001),
        RDX("edx", (byte) 0b010),
        RSI("esi", (byte) 0b110),
        RDI("edi", (byte) 0b111),
        RSP("esp", (byte) 0b100),
        RBP("ebp", (byte) 0b101),
        R8("r8d", (byte) 0b000),
        R9("r9d", (byte) 0b001),
        R10("r10d", (byte) 0b010),
        R11("r11d", (byte) 0b011),
        R12("r12d", (byte) 0b100),
        R13("r13d", (byte) 0b101),
        R14("r14d", (byte) 0b110),
        R15("r15d", (byte) 0b111);

        final String name32;
        final byte code32;

        static final Register[] VALUES = values();

        Register(String name32, byte code32) {
            this.name32 = name32;
            this.code32 = code32;
        }

        public boolean extended() {
            return ordinal() >= R8.ordinal();
        }
    }

    /// Returns a byte array of the given size, or one bigger if the given register is extended. This will extend via
    /// REX_B, as one-byte extensions are wont to do.
    public static ByteArrayList buf(Register reg, int size) {
        if (reg.extended()) { var ret = new ByteArrayList(size + 1); ret.add((byte) (REX | REX_B)); return ret; }
        return new ByteArrayList(size);
    }

    /// See [#buf(Register , int)]. Does the same thing, except it uses REX_R and REX_B as appropriate for the
    /// given registers
    public static ByteArrayList buf(Register reg, Register r_m, int size) {
        if (!reg.extended() && !r_m.extended()) return new ByteArrayList(size);

        var ret = new ByteArrayList(size + 1);
        var rex = REX;
        if (reg.extended()) rex |= REX_R;
        if (r_m.extended()) rex |= REX_B;
        ret.add(rex);
        return ret;
    }

    /// Many instructions are of the same form, listed as MR in Intel's documentation. This is a helper for any of them.
    private static ByteImmutableList insnMR(byte opcode, Register src, Register dst) {
        var ret = buf(src, dst, 2);
        ret.add(opcode);
        ret.add((byte) (0b1100_0000 | (src.code32 << 3) | dst.code32)); // MODR/M byte

        return new ByteImmutableList(ret);
    }

    /*

    Below are the actual instructions

     */

    /// Returns the ADD dst, src instruction.
    public static ByteImmutableList add(Register src, Register dst) { return insnMR((byte) 0x01, src, dst); }

    /// Returns CDQ. That sign-extends eax into edx:eax.
    public static byte cdq() { return (byte) 0x99; }

    /// Returns the IDIV src instruction. That is, computes
    /// eax = edx:eax / src
    public static ByteImmutableList idiv(Register src) {
        var ret = buf(src, 2);
        ret.add((byte) 0xF7);
        ret.add((byte) (0b1111_1000 | src.code32));

        return new ByteImmutableList(ret);
    }

    /// Returns the IMUL dst, src instruction. That is, computes
    /// dst = dst * src
    public static ByteImmutableList imul(Register src, Register dst) {
        var ret = buf(dst, src, 3);
        ret.addAll(bOfIs(0x0F, 0xAF));
        ret.add((byte) (0b1100_0000 | (dst.code32 << 3) | src.code32)); // MODR/M byte

        return new ByteImmutableList(ret);
    }

    /// Returns the MOV dst, src instruction.
    public static ByteImmutableList mov(Register src, Register dst) { return insnMR((byte) 0x89, src, dst); }

    /// Returns the MOV dst, 0xvalue instruction.
    public static ByteImmutableList movImmediate(int value, Register register) {
        var ret = buf(register, 5);
        ret.add((byte) (0xb8 | register.code32));
        ret.addAll(bOfI(value));

        return new ByteImmutableList(ret);
    }

    /// Returns the SUB dst, src instruction. That is, computes
    /// dst = dst - src
    public static ByteImmutableList sub(Register src, Register dst) { return insnMR((byte) 0x29, src, dst); }
}
