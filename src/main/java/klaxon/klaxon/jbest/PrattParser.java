package klaxon.klaxon.jbest;

import static klaxon.klaxon.jbest.token.Token.precedence;

import klaxon.klaxon.jbest.token.Operation;
import klaxon.klaxon.jbest.token.Token;
import klaxon.klaxon.jbest.token.TokenStream;

public class PrattParser {

    /// The entry point to the parser - i.e. where the going gets tough.
    ///
    /// Currently handles: basic arithmetic.
    public static AST.Node getBinaryNode(TokenStream tokens, int prevPrecedence) {
        // We have no knowledge, the only valid expression is a number
        var left = getPrimaryNode(tokens.pop());
        var currentOp = tokens.peek();

        // Just an integer, all alone. Contrary to the name, this isn't actually a binary node.
        if (currentOp instanceof Token.EOF) return left;

        // BUT WAIT, THERE'S MORE
        // If this operator has a greater precedence than the previous one...
        int curPrecedence = precedence(currentOp);
        while (curPrecedence > prevPrecedence) {
            // we're of the form ... (prev)+ 800 (cur)* ..., or similar, and need to return *our* binary op as the
            // parent instead. But since the *next next* op might do the same, we jump ahead again. This recurses as
            // necessary until it finds an operator of lower or equal precedence to ours.
            tokens.advance();
            var right = getBinaryNode(tokens, curPrecedence);

            // ...at which point we join them.
            left = new AST.Node.ArithNode(left, Operation.operationOf(currentOp), right);

            // Update the precedence if the next token isn't an EOF. It can't be a number, since we would have just
            // consumed one.
            switch (tokens.peek()) {
                case Token.EOF _ -> { return left; }
                case Token t -> curPrecedence = precedence(t);
            }
        }

        // Finally, we return the T R E E
        return left;
    }

    /// Grabs an immediate value, or else DIES!
    public static AST.Node getPrimaryNode(Token token) {
        if (!(token instanceof Token.Integer(int value))) {
            throw new IllegalArgumentException("Could not convert " + token + " into an integer!");
        }

        return new AST.Node.LeafNode(value);
    }
}
