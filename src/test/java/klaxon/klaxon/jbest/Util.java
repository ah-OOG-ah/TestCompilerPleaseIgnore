package klaxon.klaxon.jbest;

import ajs.printutils.PrettyPrintTree;
import java.util.List;
import org.w3c.dom.Node;

public class Util {
    public static String printAST(AST.Node root) {
        final var printer = new PrettyPrintTree<AST.Node>(
                node -> switch (node) {
                    case AST.Node.ArithNode an -> List.of(an.left, an.right);
                    case AST.Node.LeafNode _ -> List.of();
                }, Object::toString);

        return printer.toStr(root);
    }
}
