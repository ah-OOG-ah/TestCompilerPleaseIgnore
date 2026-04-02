package klaxon.klaxon.jbest;

import static java.lang.Integer.parseInt;
import static java.nio.CharBuffer.wrap;

import java.util.ArrayList;
import klaxon.klaxon.jbest.token.Token;

public class Main {

    static void main(String[] args) {

    }

    public static ArrayList<Token> makeAllTokens(InputBlock block) {
        final var ret = new ArrayList<Token>();
        var tok = tokenize(block);
        while (!(tok instanceof Token.EOF)) {
            ret.add(tok);
            tok = tokenize(block);
        }

        return ret;
    }

    public static Token tokenize(InputBlock block) {
        var charr = block.nextNonWhitespace();
        return switch (charr) {
            case '+' -> new Token.Plus();
            case '-' -> new Token.Minus();
            case '*' -> new Token.Star();
            case '/' -> new Token.FwdSlash();
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> tokenizeInt(block, charr);
            default -> new Token.EOF();
        };
    }

    private static char[] buffer = new char[16];
    private static Token.Integer tokenizeInt(InputBlock block, char start) {
        int bufIdx = 0;
        while (start >= '0' && start <= '9') {
            if (bufIdx == buffer.length){
                final var newBuf = new char[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuf, 0, buffer.length);
                buffer = newBuf;
            }
            buffer[bufIdx++] = start;
            start = block.next();
        }

        block.back();
        return new Token.Integer(parseInt(wrap(buffer), 0, bufIdx, 10));
    }

}
