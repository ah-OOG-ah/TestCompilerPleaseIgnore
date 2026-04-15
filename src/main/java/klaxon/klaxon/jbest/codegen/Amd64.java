package klaxon.klaxon.jbest.codegen;

import static it.unimi.dsi.fastutil.bytes.ByteImmutableList.of;
import static klaxon.klaxon.jbest.Util.bOfL;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RAX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RDX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RSP;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.VALUES;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteImmutableList;
import java.util.List;
import klaxon.klaxon.jbest.AST.Node.LeafNode;
import klaxon.klaxon.jbest.Util;
import klaxon.klaxon.jbest.codegen.Amd64Ops.Register;
import klaxon.klaxon.jbest.codegen.Amd64Ops.RegisterSet;

public class Amd64 implements Backend {
    /// amd54+10 has:
    /// eax, ebx, ecx, edx, esi, edi, ebp, esp are general-purpose, and can be used by anything.
    /// Additionally, we get r8 through r15
    /// We skip:
    /// - eax (reserved for idiv)
    /// - edx (reserved for idiv)
    /// - esp (reserved for stack usage)
    private final RegisterSet freeRegisters;

    private final ByteArrayList output = new ByteArrayList();

    public Amd64() {
        freeRegisters = new RegisterSet();
        freeRegisters.addAll(List.of(VALUES));
        freeRegisters.remove(RAX);
        freeRegisters.remove(RDX);
        freeRegisters.remove(RSP);
    }

    private static final ByteImmutableList ELF_MAGIC = Util.bOfIs(0x7F, 'E', 'L', 'F');
    private static final long ENTRY_ADDR = 0xDEAD_BEEFL;
    private static final long ELF_SIZE = 0x40L;
    private static final long PH_SIZE = 0x38L;

    public ByteArrayList elfHeader() {
        final var buf = new ByteArrayList(0x40);

        // e_ident
        buf.addAll(ELF_MAGIC);
        buf.add((byte) 2); // 2 for 64-bit
        buf.add((byte) 1); // 1 for little-endian
        buf.add((byte) 3); // 3 for GNU ELF (i.e. Linux)
        buf.add((byte) 3); // 3 for GNU ELF (i.e. Linux)
        buf.addAll(of(new byte[8])); // Padding bytes

        buf.addAll(Util.bOfIs(0x02, 0)); // Executable file type
        buf.addAll(Util.bOfIs(0x3E, 0)); // Target ISA
        buf.addAll(Util.bOfI(0x01)); // 1 for ELF v1
        buf.addAll(bOfL(ENTRY_ADDR)); // Entry point address in process address space
        buf.addAll(bOfL(0x40)); // Program header table address. (follows ELF header)
        buf.addAll(bOfL(0x00)); // Section header table address. Zero, we don't have one.
        buf.addAll(of(new byte[4])); // Processor-specific flags. Don't think we need it.
        buf.addAll(Util.bOfIs(0x40, 0)); // ELF Header size
        buf.addAll(Util.bOfIs(0x38, 0)); // Header table entry size
        buf.addAll(Util.bOfIs(0x01, 0)); // Header table entry count
        buf.addAll(Util.bOfIs(0, 0)); // Section table entry size - not needed
        buf.addAll(Util.bOfIs(0, 0)); // Section table entry count
        buf.addAll(Util.bOfIs(0, 0)); // Section table names entry
        return buf;
    }

    private static final int PF_X = 0x1;
    private static final int PF_W = 0x2;
    private static final int PF_R = 0x4;

    public ByteArrayList programHeader() {
        final var buf = new ByteArrayList(0x40);
        buf.addAll(Util.bOfI(PF_R | PF_X)); // permissions
        buf.addAll(Util.bOfI(0x01)); // Segment type - loadable
        buf.addAll(bOfL(ELF_SIZE)); // segment address in binary
        buf.addAll(bOfL(0x0)); // address of segment in vmem
        buf.addAll(bOfL(0x0)); // address of code in memory. irrelevant for System V
        buf.addAll(bOfL(output.size())); // segment size on disk
        buf.addAll(bOfL(output.size())); // segment size in memory
        buf.addAll(bOfL(0x00)); // no alignment
        return buf;
    }

    @Override
    public Register load(LeafNode leaf) {
        final var reg = freeRegisters.pop();
        output.addAll(Amd64Ops.movImmediate(leaf.self, reg));
        return reg;
    }

    @Override
    public Register add(Register left, Register right) {
        output.addAll(Amd64Ops.add(right, left));
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Register sub(Register left, Register right) {
        output.addAll(Amd64Ops.sub(right, left));
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Register mul(Register left, Register right) {
        output.addAll(Amd64Ops.imul(right, left));
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Register div(Register left, Register right) {
        output.addAll(Amd64Ops.mov(left, RAX));
        output.add(Amd64Ops.cdq());
        output.addAll(Amd64Ops.idiv(right));
        output.addAll(Amd64Ops.mov(RAX, left));
        freeRegisters.add(right);
        return left;
    }
}
