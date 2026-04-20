package klaxon.klaxon.jbest;

import java.io.IOException;
import java.nio.file.Path;
import klaxon.klaxon.jbest.codegen.Amd64Assembly;

public class Main {

    static void main(String[] args) throws IOException {
        var input = "5 + 10";
        var asm = new Amd64Assembly();
        Writer.generate(input, asm);

        final var asmOut = Path.of("output.asm");
        final var objOut = Path.of("output.o");
        final var elfOut = Path.of("output");
        asm.write(asmOut);

        new ProcessBuilder("nasm", asmOut.toString(), "-f", "elf64").start();
        new ProcessBuilder("ld", objOut.toString(), "-o", elfOut.toString()).start();
    }

}
