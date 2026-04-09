package klaxon.klaxon.jbest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import klaxon.klaxon.jbest.token.InputBlock;
import klaxon.klaxon.jbest.token.TokenIO;
import klaxon.klaxon.jbest.token.TokenStream;
import org.junit.jupiter.api.Test;

public class AntiParseTest {
    @Test
    void repeatNumbers() {
        var input = new InputBlock("1942 8392 + 842 - 92 * 3 / 7");
        var toks = TokenIO.makeAllTokens(input);
        var e = assertThrowsExactly(IllegalArgumentException.class, () -> PrattParser.getBinaryNode(new TokenStream(toks), 0));
        assertEquals("Could not convert Integer[value=8392] into an arithmetic operation!", e.getMessage());
    }

    @Test
    void repeatOperators() {
        var input = new InputBlock("1942 - + 842 - 92 * 3 / 7");
        var toks = TokenIO.makeAllTokens(input);
        var e = assertThrowsExactly(IllegalArgumentException.class, () -> PrattParser.getBinaryNode(new TokenStream(toks), 0));
        assertEquals("Could not convert Plus[] into an integer!", e.getMessage());
    }

    @Test
    void missingNumberStart() {
        var input = new InputBlock(" + 842 - 92 * 3 / 7");
        var toks = TokenIO.makeAllTokens(input);
        var e = assertThrowsExactly(IllegalArgumentException.class, () -> PrattParser.getBinaryNode(new TokenStream(toks), 0));
        assertEquals("Could not convert Plus[] into an integer!", e.getMessage());
    }

    @Test
    void missingNumberEnd() {
        var input = new InputBlock("1942 + 842 - 92 * 3 / ");
        var toks = TokenIO.makeAllTokens(input);
        var e = assertThrowsExactly(IllegalArgumentException.class, () -> PrattParser.getBinaryNode(new TokenStream(toks), 0));
        assertEquals("Could not convert EOF[] into an integer!", e.getMessage());
    }
}
