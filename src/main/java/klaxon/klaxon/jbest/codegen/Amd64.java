package klaxon.klaxon.jbest.codegen;

import static it.unimi.dsi.fastutil.bytes.ByteImmutableList.of;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static klaxon.klaxon.jbest.Util.bOfIs;
import static klaxon.klaxon.jbest.Util.bOfL;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RAX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RDX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RSP;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.VALUES;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
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

    private static final ByteImmutableList ELF_MAGIC = bOfIs(0x7F, 'E', 'L', 'F');
    private static final int ELF_SIZE = 0x40;
    private static final int PH_SIZE = 0x38;
    /// Technically, 0x0 is a valid entry point. However, I want to leave that open for nullptr.
    private static final long ENTRY_PTR = 0xDEAD_BEEFL;

    private static ByteImmutableList eIdent() {
        return bOfIs(
            0x7F, 'E', 'L', 'F', // ELF magic
            0x02, // EI_CLASS: ELF64
            0x01, // EI_DATA: Little-endian
            0x01, // EI_VERSION: EV_CURRENT
            0x00, // EI_OSABI: ELFOSABI_GNU
            0x00, // EI_ABIVERSION: zero, this is statically linked
            0, 0, 0, 0, 0, 0, 0 // seven padding bytes
        );
    }

    public ByteArrayList elfHeader() {
        final var buf = new ByteArrayList(ELF_SIZE);

        buf.addAll(eIdent());
        buf.addAll(bOfIs(0x02, 0)); // Executable file type
        buf.addAll(bOfIs(0x3E, 0)); // Target ISA
        buf.addAll(Util.bOfI(0x01)); // 1 for ELF v1
        buf.addAll(bOfL(ENTRY_PTR)); // Entry point address in process address space
        buf.addAll(bOfL(0x40)); // Program header table address. (follows ELF header)
        buf.addAll(bOfL(0x00)); // Section header table address. Zero, we don't have one.
        buf.addAll(of(new byte[4])); // Processor-specific flags. Don't think we need it.
        buf.addAll(bOfIs(0x40, 0)); // ELF Header size
        buf.addAll(bOfIs(0x38, 0)); // Header table entry size
        buf.addAll(bOfIs(0x01, 0)); // Header table entry count
        buf.addAll(bOfIs(0, 0)); // Section table entry size - not needed
        buf.addAll(bOfIs(0, 0)); // Section table entry count
        buf.addAll(bOfIs(0, 0)); // Section table names entry
        return buf;
    }

    private static final int PF_X = 0x1;
    private static final int PF_W = 0x2;
    private static final int PF_R = 0x4;

    public ByteArrayList programHeader() {
        final var buf = new ByteArrayList(PH_SIZE);
        buf.addAll(Util.bOfI(0x01)); // Segment type - loadable
        buf.addAll(Util.bOfI(PF_R | PF_X)); // permissions
        buf.addAll(bOfL(ELF_SIZE)); // segment address in binary
        buf.addAll(bOfL(ENTRY_PTR)); // address of segment in vmem
        buf.addAll(bOfL(0x0)); // address of code in memory. irrelevant for System V
        buf.addAll(bOfL(output.size())); // segment size on disk
        buf.addAll(bOfL(output.size())); // segment size in memory
        buf.addAll(bOfL(0x00)); // no alignment
        return buf;
    }

    public ByteArrayList stackSectionHeader() {
        final var buf = new ByteArrayList(0x40);
        buf.addAll(Util.bOfI(0x01)); // Segment type - loadable
        buf.addAll(Util.bOfI(PF_R | PF_X)); // permissions
        buf.addAll(bOfL(ELF_SIZE)); // segment address in binary
        buf.addAll(bOfL(0x0)); // address of segment in vmem
        buf.addAll(bOfL(0x0)); // address of code in memory. irrelevant for System V
        buf.addAll(bOfL(output.size())); // segment size on disk
        buf.addAll(bOfL(output.size())); // segment size in memory
        buf.addAll(bOfL(0x00)); // no alignment
        return buf;
    }

    @Override
    public void write(File file) throws IOException {
        var out = elfHeader();
        out.addAll(programHeader());
        out.addAll(this.output);

        Files.write(file.toPath(), out.toByteArray(), TRUNCATE_EXISTING, CREATE);
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
    public Register div(Register left, Register right) {
        output.addAll(Amd64Ops.mov(left, RAX));
        output.add(Amd64Ops.cdq());
        output.addAll(Amd64Ops.idiv(right));
        output.addAll(Amd64Ops.mov(RAX, left));
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
    public Register sub(Register left, Register right) {
        output.addAll(Amd64Ops.sub(right, left));
        freeRegisters.add(right);
        return left;
    }
}
