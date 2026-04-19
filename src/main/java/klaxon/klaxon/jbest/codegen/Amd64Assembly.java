package klaxon.klaxon.jbest.codegen;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RAX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RDX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RSP;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.VALUES;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import klaxon.klaxon.jbest.AST;
import klaxon.klaxon.jbest.codegen.elf.ELF;

public class Amd64Assembly implements Backend {
    private final StringBuilder code = new StringBuilder();

    /// See [Amd64#freeRegisters]
    private final Amd64Ops.RegisterSet freeRegisters;

    public Amd64Assembly() {
        freeRegisters = new Amd64Ops.RegisterSet();
        freeRegisters.addAll(List.of(VALUES));
        freeRegisters.remove(RAX);
        freeRegisters.remove(RDX);
        freeRegisters.remove(RSP);
    }

    @Override
    public void write(File file) throws IOException {
        Files.writeString(file.toPath(), code.toString(), CREATE, TRUNCATE_EXISTING);
    }

    @Override
    public Amd64Ops.Register load(AST.Node.LeafNode leaf) {
        final var reg = freeRegisters.pop();
        code.append("mov ").append(reg.name32).append(", ").append(leaf.self).append("\n");
        return reg;
    }

    @Override
    public Amd64Ops.Register add(Amd64Ops.Register left, Amd64Ops.Register right) {
        code.append("add ").append(left.name32).append(", ").append(right.name32).append("\n");
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Amd64Ops.Register div(Amd64Ops.Register left, Amd64Ops.Register right) {
        code.append("mov ").append(RAX.name32).append(", ").append(left.name32).append("\n");
        code.append("cdq\n");
        code.append("idiv ").append(right.name32).append("\n");
        code.append("mov ").append(left.name32).append(", ").append(RAX.name32).append("\n");
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Amd64Ops.Register mul(Amd64Ops.Register left, Amd64Ops.Register right) {
        code.append("imul ").append(left.name32).append(", ").append(right.name32).append("\n");
        freeRegisters.add(right);
        return left;
    }

    @Override
    public Amd64Ops.Register sub(Amd64Ops.Register left, Amd64Ops.Register right) {
        code.append("sub ").append(left.name32).append(", ").append(right.name32).append("\n");
        freeRegisters.add(right);
        return left;
    }
}
