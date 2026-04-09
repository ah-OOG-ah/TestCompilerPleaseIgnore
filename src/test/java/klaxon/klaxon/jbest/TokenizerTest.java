package klaxon.klaxon.jbest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import klaxon.klaxon.jbest.token.InputBlock;
import klaxon.klaxon.jbest.token.Token;
import klaxon.klaxon.jbest.token.TokenIO;
import org.junit.jupiter.api.Test;

public class TokenizerTest {
    @Test
    void integer() {
        var input = new InputBlock("1942");
        var token = TokenIO.tokenize(input);
        assertEquals(new Token.Integer(1942), token);
    }

    @Test
    void plus() {
        var input = new InputBlock("+");
        var token = TokenIO.tokenize(input);
        assertEquals(new Token.Plus(), token);
    }

    @Test
    void minus() {
        var input = new InputBlock("-");
        var token = TokenIO.tokenize(input);
        assertEquals(new Token.Minus(), token);
    }

    @Test
    void star() {
        var input = new InputBlock("*");
        var token = TokenIO.tokenize(input);
        assertEquals(new Token.Star(), token);
    }

    @Test
    void fwdSlash() {
        var input = new InputBlock("/");
        var token = TokenIO.tokenize(input);
        assertEquals(new Token.FwdSlash(), token);
    }

    @Test
    void eof() {
        var input = new InputBlock("");
        var token = TokenIO.tokenize(input);
        assertEquals(new Token.EOF(), token);
    }
}
