package klaxon.klaxon.jbest;

import klaxon.klaxon.jbest.token.Operation;

public class Reference {
    static InputBlock getReferenceInput() {
        return new InputBlock("1942 + 842 - 92 * 3 / 7");
    }

    static AST.Node.ArithNode getReferenceAST() {
        return new AST.Node.ArithNode(
                new AST.Node.LeafNode(1942),
                Operation.ADD,
                new AST.Node.ArithNode(
                        new AST.Node.LeafNode(842),
                        Operation.SUB,
                        new AST.Node.ArithNode(
                                new AST.Node.LeafNode(92),
                                Operation.MUL,
                                new AST.Node.ArithNode(
                                        new AST.Node.LeafNode(3),
                                        Operation.DIV,
                                        new AST.Node.LeafNode(7)
                                )
                        )
                )
        );
    }

    static int getReferenceValue() {
        //noinspection PointlessArithmeticExpression
        return 1942 + (842 - (92 * (3 / 7)));
    }
}
