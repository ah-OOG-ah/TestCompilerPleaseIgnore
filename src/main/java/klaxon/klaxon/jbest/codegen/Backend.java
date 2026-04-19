package klaxon.klaxon.jbest.codegen;

import java.io.File;
import java.io.IOException;
import jdk.jshell.spi.ExecutionControl;
import klaxon.klaxon.jbest.AST.Node.LeafNode;
import klaxon.klaxon.jbest.codegen.Amd64Ops.Register;

public interface Backend {
    default Register load(LeafNode leaf) { throw new UnsupportedOperationException(); }

    default Register add(Register right, Register left) { throw new UnsupportedOperationException(); }
    default Register sub(Register right, Register left) { throw new UnsupportedOperationException(); }
    default Register mul(Register right, Register left) { throw new UnsupportedOperationException(); }
    default Register div(Register right, Register left) { throw new UnsupportedOperationException(); }

    default void push(Register pushed) { throw new UnsupportedOperationException(); }
    default void pop(Register popped) { throw new UnsupportedOperationException(); }

    default void write(File file) throws IOException { throw new UnsupportedOperationException(); }

    /// Print the output stored in the given register... somehow, then exit.
    default void output(Register generate) { throw new UnsupportedOperationException();  }
}
