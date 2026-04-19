package klaxon.klaxon.jbest;

import klaxon.klaxon.jbest.codegen.Amd64Ops.Register;
import klaxon.klaxon.jbest.codegen.Backend;
import klaxon.klaxon.jbest.token.InputBlock;
import klaxon.klaxon.jbest.token.TokenIO;
import klaxon.klaxon.jbest.token.TokenStream;

public class Writer {

    public static void generate(String program, Backend codegen) {
        final var block = new InputBlock(program);
        final var tokens = TokenIO.makeAllTokens(block);
        final var ast = PrattParser.getBinaryNode(new TokenStream(tokens), 0);
        generate(ast, codegen);
    }

    /// Recursively run codegen. Returns the register holding the value computed.
    public static Register generate(AST.Node root, Backend output) {
        return switch (root) {
            case AST.Node.LeafNode leaf -> output.load(leaf);
            case AST.Node.ArithNode arith -> {
                var left = generate(arith.left, output);
                var right = generate(arith.right, output);
                yield switch (arith.operation) {
                    case ADD -> output.add(left, right);
                    case SUB -> output.sub(left, right);
                    case MUL -> output.mul(left, right);
                    case DIV -> output.div(left, right);
                };
            }
        };
    }
}
