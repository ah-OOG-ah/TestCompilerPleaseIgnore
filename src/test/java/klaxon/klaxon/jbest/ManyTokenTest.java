package klaxon.klaxon.jbest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import klaxon.klaxon.jbest.token.Token;
import org.junit.jupiter.api.Test;

public class ManyTokenTest {
    @Test
    void many1() {
        var input = new InputBlock("1942 + 842 - 92 * 3 / 7");
        var token = Main.makeAllTokens(input);
        assertEquals(List.of(
                new Token.Integer(1942),
                new Token.Plus(),
                new Token.Integer(842),
                new Token.Minus(),
                new Token.Integer(92),
                new Token.Star(),
                new Token.Integer(3),
                new Token.FwdSlash(),
                new Token.Integer(7),
                new Token.EOF()
        ), token);
    }
}
