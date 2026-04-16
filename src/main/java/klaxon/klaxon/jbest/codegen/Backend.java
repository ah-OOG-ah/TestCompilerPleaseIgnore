package klaxon.klaxon.jbest.codegen;

import jdk.jshell.spi.ExecutionControl;
import klaxon.klaxon.jbest.AST.Node.LeafNode;
import klaxon.klaxon.jbest.codegen.Amd64Ops.Register;

public interface Backend {
    default Register load(LeafNode leaf) { throw new UnsupportedOperationException(); }

    default Register add(Register right, Register left) { throw new UnsupportedOperationException(); }
    default Register sub(Register right, Register left) { throw new UnsupportedOperationException(); }
    default Register mul(Register right, Register left) { throw new UnsupportedOperationException(); }
    default Register div(Register right, Register left) { throw new UnsupportedOperationException(); }
}
