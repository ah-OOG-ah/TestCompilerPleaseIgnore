package klaxon.klaxon.jbest.token;

import static java.lang.Integer.parseInt;
import static java.nio.CharBuffer.wrap;

import java.util.ArrayList;

public class TokenIO {
    private static char[] buffer = new char[16];

    public static ArrayList<Token> makeAllTokens(InputBlock block) {
        final var ret = new ArrayList<Token>();
        Token tok;
        do {
            tok = tokenize(block);
            ret.add(tok);
        } while (!(tok instanceof Token.EOF));

        return ret;
    }

    public static Token tokenize(InputBlock block) {
        var charr = block.nextNonWhitespace();
        return switch (charr) {
            case '+' -> Token.T_PLUS;
            case '-' -> Token.T_MINUS;
            case '*' -> Token.T_STAR;
            case '/' -> Token.T_FWD_SLASH;
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> tokenizeInt(block, charr);
            case InputBlock.EOF -> Token.T_EOF;
            default -> throw new IllegalArgumentException("Unexpected character '" + charr + "'.");
        };
    }

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
