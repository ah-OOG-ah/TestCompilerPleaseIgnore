package klaxon.klaxon.jbest;

import static java.lang.ProcessBuilder.Redirect.INHERIT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import klaxon.klaxon.jbest.codegen.Amd64Assembly;

public class Main {

    static void main(String[] args) throws IOException {
        var input = "2 + 3 * 5 - 8 / 3";
        var asm = new Amd64Assembly();
        Writer.generate(input, asm);

        final var asmOut = Path.of("output.asm");
        final var objOut = Path.of("output.o");
        final var elfOut = Path.of("output");
        Files.deleteIfExists(asmOut);
        Files.deleteIfExists(objOut);
        Files.deleteIfExists(elfOut);
        asm.write(asmOut);

        // Sadly, we are not yet at the point where we can handle this. Instead, we shell out.
        shell("clang", "-O3", "printInt.c", "-c");
        shell("nasm", asmOut.toString(), "-f", "elf64");
        shell(
                "ld",
                objOut.toString(), "printInt.o",
                "-o", elfOut.toString(),
                "-I", "/lib/ld-linux-x86-64.so.2",
                "-lc");
    }

    private static void shell(String... args) throws IOException {
        try {
            new ProcessBuilder(args)
                    .redirectOutput(INHERIT)
                    .redirectError(INHERIT)
                    .start()
                    .waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
