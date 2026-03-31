package klaxon.klaxon.jbest;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Main {

    static void main(String[] args) throws IOException {
        if (args.length == 0) return;

        var input = Files.readString(Path.of(args[0]));

        var output = Path.of(args[0].replaceFirst("\\.c$", ".s"));
        try (var writer = Files.newBufferedWriter(output, TRUNCATE_EXISTING, CREATE)) {
            writer.append("  mov eax, 42\n");
            writer.append("  ret\n");
        }
    }
}
