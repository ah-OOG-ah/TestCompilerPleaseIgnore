package klaxon.klaxon.jbest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import klaxon.klaxon.jbest.codegen.Interpreter;
import klaxon.klaxon.jbest.token.Token;
import klaxon.klaxon.jbest.token.TokenIO;
import klaxon.klaxon.jbest.token.TokenStream;
import org.junit.jupiter.api.Test;

public class ManyTokenTest {
    @Test
    void many1() {
        var input = Reference.getReferenceInput();
        var token = TokenIO.makeAllTokens(input);
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

    @Test
    void manyTree() {
        var input = Reference.getReferenceInput();
        var tokens = TokenIO.makeAllTokens(input);
        var ast = PrattParser.getBinaryNode(new TokenStream(tokens), 0);
        var reference = Reference.getReferenceAST();
        var logger = Logger.getLogger("Tests");
        logger.log(Level.INFO, Util.printAST(reference));
        logger.log(Level.INFO, Util.printAST(ast));

        assertEquals(reference, ast);
    }

    @Test
    void eval() {
        assertEquals(Reference.getReferenceValue(), Interpreter.interpret(Reference.getReferenceAST()));
    }
}
