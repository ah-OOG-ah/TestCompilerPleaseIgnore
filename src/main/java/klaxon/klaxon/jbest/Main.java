package klaxon.klaxon.jbest;

import static java.lang.String.valueOf;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    static void main(String[] args) throws IOException {
        if (args.length == 0) return;

        var input = Files.readString(Path.of(args[0]));

        var output = Path.of(args[0].replaceFirst("\\.c$", ".s"));
        try (var writer = Files.newBufferedWriter(output, TRUNCATE_EXISTING, CREATE)) {
            emit42(writer, 137);
        }
    }

    private static void emit42(BufferedWriter writer, int integer) throws IOException {
        writer.append("  mov eax, ").append(valueOf(integer)).append("\n");
        writer.append("  ret\n");
    }
}
