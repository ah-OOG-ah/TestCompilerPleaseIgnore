package klaxon.klaxon.jbest;

import java.util.Objects;
import klaxon.klaxon.jbest.token.Operation;

/// An abstract syntax tree, for C!
public final class AST {

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
