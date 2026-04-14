package klaxon.klaxon.jbest.codegen;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.R10;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.R11;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.R15;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.R8;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RAX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RBX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RDX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RSP;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.idiv;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.imul;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.mov;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.movImmediate;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.add;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.sub;
import static org.junit.jupiter.api.Assertions.assertEquals;

import klaxon.klaxon.jbest.Util;
import org.junit.jupiter.api.Test;

public class Amd64Test {
    @Test
    void _movI() {
        // Values from godbolt
        assertEquals(Util.bOfIs(0xb8, 0x28, 0, 0, 0), movImmediate(40, RAX));
        assertEquals(Util.bOfIs(0xbc, 0xe6, 0, 0, 0), movImmediate(230, RSP));
        assertEquals(Util.bOfIs(0x41, 0xb8, 0x55, 0xa6, 0, 0), movImmediate(42581, R8));
        assertEquals(Util.bOfIs(0x41, 0xbf, 0x39, 0xe9, 0xff, 0xff), movImmediate(-5831, R15));
    }

    @Test
    void _mov() {
        // Values from godbolt
        assertEquals(Util.bOfIs(0x89, 0xc2), mov(RAX, RDX));
        assertEquals(Util.bOfIs(0x41, 0x89, 0xd8), mov(RBX, R8));
        assertEquals(Util.bOfIs(0x44, 0x89, 0xd0), mov(R10, RAX));
        assertEquals(Util.bOfIs(0x44, 0x89, 0xdc), mov(R11, RSP));
    }

    @Test
    void _add() {
        // Values from godbolt
        assertEquals(Util.bOfIs(0x01, 0xc2), add(RAX, RDX));
        assertEquals(Util.bOfIs(0x41, 0x01, 0xd8), add(RBX, R8));
        assertEquals(Util.bOfIs(0x44, 0x01, 0xd0), add(R10, RAX));
        assertEquals(Util.bOfIs(0x44, 0x01, 0xdc), add(R11, RSP));
    }

    @Test
    void _sub() {
        // Values from godbolt
        assertEquals(Util.bOfIs(0x29, 0xc2), sub(RAX, RDX));
        assertEquals(Util.bOfIs(0x41, 0x29, 0xd8), sub(RBX, R8));
        assertEquals(Util.bOfIs(0x44, 0x29, 0xd0), sub(R10, RAX));
        assertEquals(Util.bOfIs(0x44, 0x29, 0xdc), sub(R11, RSP));
    }

    @Test
    void _mul() {
        // Values from godbolt
        assertEquals(Util.bOfIs(0x0f, 0xaf, 0xd0), imul(RAX, RDX));
        assertEquals(Util.bOfIs(0x44, 0x0f, 0xaf, 0xc3), imul(RBX, R8));
        assertEquals(Util.bOfIs(0x41, 0x0f, 0xaf, 0xc2), imul(R10, RAX));
        assertEquals(Util.bOfIs(0x41, 0x0f, 0xaf, 0xe3), imul(R11, RSP));
    }

    @Test
    void _div() {
        // Values from godbolt
        assertEquals(Util.bOfIs(0xf7, 0xfa), idiv(RDX));
        assertEquals(Util.bOfIs(0x41, 0xf7, 0xf8), idiv(R8));
    }
}
