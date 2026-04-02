package klaxon.klaxon.jbest;

public class Interpreter {
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
