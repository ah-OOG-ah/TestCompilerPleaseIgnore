package klaxon.klaxon.jbest.codegen;

import klaxon.klaxon.jbest.AST.Node.LeafNode;
import klaxon.klaxon.jbest.codegen.Amd64Ops.Register;

public interface Backend {
    Register load(LeafNode leaf);

    Register add(Register right, Register left);
    Register sub(Register right, Register left);
    Register mul(Register right, Register left);
    Register div(Register right, Register left);
}
