package klaxon.klaxon.jbest.codegen;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Registers.R10;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Registers.R11;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Registers.R15;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Registers.R8;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Registers.RAX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Registers.RBX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Registers.RDX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Registers.RSP;
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
}
