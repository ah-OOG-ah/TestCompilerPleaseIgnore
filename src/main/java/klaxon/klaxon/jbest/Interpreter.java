package klaxon.klaxon.jbest;

import klaxon.klaxon.jbest.token.InputBlock;
import klaxon.klaxon.jbest.token.TokenIO;
import klaxon.klaxon.jbest.token.TokenStream;

public class Interpreter {
    public static int interpret(String program) {
        final var block = new InputBlock(program);
        final var tokens = TokenIO.makeAllTokens(block);
        final var ast = PrattParser.getBinaryNode(new TokenStream(tokens), 0);

        return interpret(ast);
    }

    public static int interpret(AST.Node root) {
        return switch (root) {
            case AST.Node.LeafNode leaf -> leaf.self;
            case AST.Node.ArithNode arith -> {
                final var left = interpret(arith.left); final var right = interpret(arith.right);
                yield switch (arith.operation) {
                    case ADD -> left + right;
                    case SUB -> left - right;
                    case MUL -> left * right;
                    case DIV -> left / right;
                };
            }
        };
    }
}
