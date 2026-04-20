package klaxon.klaxon.jbest.codegen;

import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.R11;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RAX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RCX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RDI;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RDX;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.Register.RSI;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.mov;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.movImmediate;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.pop;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.push;
import static klaxon.klaxon.jbest.codegen.Amd64Ops.syscall;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import klaxon.klaxon.jbest.codegen.Amd64Ops.Register;

/// Provides helpers that directly inject system calls into the program. These don't rely on the compiler to make them
/// correct, you've been warned!
public class Syscalls {
    /// Clobbers NOTHING!
    /// Also, the buffer pointer and count are limited to 32-bit sizes, despite this being a 64-bit syscall. I doubt
    /// this compiler will need anything larger.
    public static ByteArrayList write(
            Register fileDescriptor,
            Register bufPtr,
            Register count,
            Amd64Ops.RegisterSet freeRegisters) {
        final var ret = new ByteArrayList();

        // Load the syscall!
        ret.addAll(movImmediate(0x01, RAX));

        // RAX is unused, but RDI might be. Save the value, just in case.
        var savedRDI = bufPtr == RDI;
        if (savedRDI) ret.addAll(push(RDI));
        ret.addAll(mov(fileDescriptor, RDI));

        // If we had to save the value earlier, we pop. Otherwise it's a move.
        // Also, we do the save check again
        var savedRSI = count == RSI;
        if (savedRSI) ret.addAll(push(RSI));
        ret.addAll(savedRDI ? pop(RSI) : mov(bufPtr, RSI));

        ret.addAll(savedRSI ? pop(RDX) : mov(count, RDX));

        // Finally, we push rcx and r11 to avoid the syscall clobber
        var savedRCX = !freeRegisters.contains(RCX); if (savedRCX) ret.addAll(push(RCX));
        var savedR11 = !freeRegisters.contains(R11); if (savedR11) ret.addAll(push(R11));
        ret.addAll(syscall());
        if (savedR11) ret.addAll(pop(R11));
        if (savedRCX) ret.addAll(pop(RCX));

        return ret;
    }

    /// May clobber any and all registers. It also doesn't matter, since we're exiting!
    public static String exit(int value) {
        return """
               mov eax, 60
               mov edi,\s""" + value + """
               
               syscall
               """;
    }
}
