package klaxon.klaxon.jbest;

import java.util.function.Supplier;
import klaxon.klaxon.jbest.token.Operation;
import klaxon.klaxon.jbest.token.Token;

public class PrattParser {

    /// The entry point to the parser - i.e. where the going gets tough.
    ///
    /// Currently handles: basic arithmetic.
    public static AST.Node parseNodes(Supplier<Token> token) {
        // Assume the next three nodes are primary, operator, anything
        final var left = getPrimaryNode(token.get());
        var next = token.get();

        // Just an integer, all alone.
        if (next instanceof Token.EOF) return left;

        // BUT WAIT, THERE'S MORE
        final var op = Operation.operationOf(next);
        final var right = parseNodes(token);

        return new AST.Node.ArithNode(left, op, right);
    }

    /// Grabs an immediate value, or else DIES!
    public static AST.Node getPrimaryNode(Token token) {
        if (!(token instanceof Token.Integer(int value))) {
            throw new IllegalArgumentException("Could not convert " + token + " into an integer!");
        }

        return new AST.Node.LeafNode(value);
    }
}
