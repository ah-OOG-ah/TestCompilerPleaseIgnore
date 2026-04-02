package klaxon.klaxon.jbest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TokenizerTest {
    @Test
    void integer() {
        var input = new Main.InputBlock("1942");
        var token = Main.tokenize(input);
        assertEquals(new Token.Integer(1942), token);
    }

    @Test
    void plus() {
        var input = new Main.InputBlock("+");
        var token = Main.tokenize(input);
        assertEquals(new Token.Plus(), token);
    }

    @Test
    void minus() {
        var input = new Main.InputBlock("-");
        var token = Main.tokenize(input);
        assertEquals(new Token.Minus(), token);
    }

    @Test
    void star() {
        var input = new Main.InputBlock("*");
        var token = Main.tokenize(input);
        assertEquals(new Token.Star(), token);
    }

    @Test
    void fwdSlash() {
        var input = new Main.InputBlock("/");
        var token = Main.tokenize(input);
        assertEquals(new Token.FwdSlash(), token);
    }

    @Test
    void eof() {
        var input = new Main.InputBlock("");
        var token = Main.tokenize(input);
        assertEquals(new Token.EOF(), token);
    }
}
