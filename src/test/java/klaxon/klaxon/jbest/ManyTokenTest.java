package klaxon.klaxon.jbest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import klaxon.klaxon.jbest.AST.Node.ArithNode;
import klaxon.klaxon.jbest.AST.Node.LeafNode;
import klaxon.klaxon.jbest.token.Operation;
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

    @Test
    void manyTree() {
        var input = new InputBlock("1942 + 842 - 92 * 3 / 7");
        var tokens = Main.makeAllTokens(input);
        var ast = AST.getNode(tokens.iterator()::next);
        var reference = new ArithNode(
                new LeafNode(1942),
                Operation.ADD,
                new ArithNode(
                        new LeafNode(842),
                        Operation.SUB,
                        new ArithNode(
                                new LeafNode(92),
                                Operation.MUL,
                                new ArithNode(
                                        new LeafNode(3),
                                        Operation.DIV,
                                        new LeafNode(7)
                                )
                        )
                )
        );
        var logger = Logger.getLogger("Tests");
        logger.log(Level.INFO, Util.printAST(reference));
        logger.log(Level.INFO, Util.printAST(ast));

        assertEquals(reference, ast);
    }
}
