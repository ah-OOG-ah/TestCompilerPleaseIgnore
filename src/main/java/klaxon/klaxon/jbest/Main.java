package klaxon.klaxon.jbest;

import static java.lang.Integer.parseInt;
import static java.nio.CharBuffer.wrap;

import java.io.File;
import java.io.IOException;
import klaxon.klaxon.jbest.codegen.Amd64;

public class Main {

    static void main(String[] args) throws IOException {
        var input = "5 + 10";
        var backend = new Amd64();
        Writer.generate(input, backend);
        backend.write(new File("output"));
    }

}
