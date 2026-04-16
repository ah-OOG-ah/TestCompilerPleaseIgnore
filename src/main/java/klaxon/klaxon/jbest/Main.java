package klaxon.klaxon.jbest;

import static java.lang.Integer.parseInt;
import static java.nio.CharBuffer.wrap;

import java.io.File;
import java.io.IOException;
import klaxon.klaxon.jbest.codegen.Amd64;
import klaxon.klaxon.jbest.token.InputBlock;
import klaxon.klaxon.jbest.token.TokenIO;
import klaxon.klaxon.jbest.token.TokenStream;

public class Main {

    static void main(String[] args) throws IOException {
        var input = "5 + 10";
        var backend = new Amd64();
        Assembler.generate(input, backend);
        backend.write(new File("output"));
    }

}
