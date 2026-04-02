package klaxon.klaxon.jbest;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.nio.CharBuffer.wrap;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class Main {

    static void main(String[] args) {
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

    public static final class InputBlock {
        private final char[] input;
        private int index;

        public InputBlock(char[] input, int index) {
            this.input = input;
            this.index = index;
        }

        public InputBlock(String input) {
            this.input = input.toCharArray();
            this.index = 0;
        }

        char next() {
            if (index >= input.length) return 128;
            return input[index++];
        }

        char nextNonWhitespace() {
            char n;
            do { n = next(); } while (Character.isWhitespace(n));
            return n;
        }

        void back() {
            index--;
        }
    }
}
