package klaxon.klaxon.jbest.codegen;

import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RAX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RDX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RSP;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.VALUES;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import klaxon.klaxon.jbest.AST.Node.LeafNode;
import klaxon.klaxon.jbest.codegen.Amd64Ops.Register;
import klaxon.klaxon.jbest.codegen.Amd64Ops.RegisterSet;
import klaxon.klaxon.jbest.codegen.elf.ELF;

public class Amd64 implements Backend {
    private final ELF elf = new ELF();
    private final ByteArrayList code = elf.text.data;

    /// amd54+10 has:
    /// eax, ebx, ecx, edx, esi, edi, ebp, esp are general-purpose, and can be used by anything.
    /// Additionally, we get r8 through r15
    /// We skip:
    /// - eax (reserved for idiv)
    /// - edx (reserved for idiv)
    /// - esp (reserved for stack usage)
    private final RegisterSet freeRegisters;

    public Amd64() {
        freeRegisters = new RegisterSet();
        freeRegisters.addAll(List.of(VALUES));
        freeRegisters.remove(RAX);
        freeRegisters.remove(RDX);
        freeRegisters.remove(RSP);
    }

    @Override
    public void write(File file) throws IOException {
        elf.write(file.toPath());
    }

    @Override
    public Register load(LeafNode leaf) {
        final var reg = freeRegisters.pop();
        code.addAll(Amd64Ops.movImmediate(leaf.self, reg));
        return reg;
    }

    @Override
    public Register add(Register left, Register right) {
        code.addAll(Amd64Ops.add(right, left));
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Register div(Register left, Register right) {
        code.addAll(Amd64Ops.mov(left, RAX));
        code.add(Amd64Ops.cdq());
        code.addAll(Amd64Ops.idiv(right));
        code.addAll(Amd64Ops.mov(RAX, left));
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Register mul(Register left, Register right) {
        code.addAll(Amd64Ops.imul(right, left));
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Register sub(Register left, Register right) {
        code.addAll(Amd64Ops.sub(right, left));
        freeRegisters.add(right);
        return left;
    }
}
