package klaxon.klaxon.jbest;

import java.util.Objects;
import java.util.function.Supplier;
import klaxon.klaxon.jbest.token.Operation;
import klaxon.klaxon.jbest.token.Token;

/// An abstract syntax tree, for C!
public final class AST {
    public Node head;

    public static Node getNode(Supplier<Token> token) {
        // Assume the next three nodes are primary, operator, anything
        final var left = getPrimaryNode(token.get());
        var next = token.get();

        // Just an integer, all alone.
        if (next instanceof Token.EOF) return left;

        // BUT WAIT, THERE'S MORE
        final var op = Operation.operationOf(next);
        final var right = getNode(token);

        return new Node.ArithNode(left, op, right);
    }

    /// Grabs an immediate value, or else DIES!
    public static Node getPrimaryNode(Token token) {
        if (!(token instanceof Token.Integer(int value))) {
            throw new IllegalStateException("Unexpected value: " + token);
        }

        return new Node.LeafNode(value);
    }

    public sealed abstract static class Node {
        public static final class ArithNode extends Node {
            public Node left;
            public Node right;
            public Operation operation;

            public ArithNode(Node left, Operation op, Node right) {
                this.left = left;
                this.operation = op;
                this.right = right;
            }

            @Override
            public String toString() {
                return "Arith[" + operation + "]";
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof ArithNode arith)) return false;
                if (!Objects.equals(arith.left, left)) return false;
                if (arith.operation != operation) return false;
                return Objects.equals(arith.right, right);
            }
        }

        public static final class LeafNode extends Node {
            public int self;

            public LeafNode(int indh) {
                self = indh;
            }

            @Override
            public String toString() {
                return "Leaf[" + self + "]";
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof LeafNode arith)) return false;
                return arith.self == self;
            }
        }
    }
}
