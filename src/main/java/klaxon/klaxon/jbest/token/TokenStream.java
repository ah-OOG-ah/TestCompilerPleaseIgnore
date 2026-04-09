package klaxon.klaxon.jbest.token;

import java.util.ArrayList;

public class TokenStream {
    private final ArrayList<Token> tokens;
    private int idx = 0;

    public TokenStream(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void advance() {
        if (idx >= tokens.size()) idx = tokens.size();
        else ++idx;
    }

    /// the current token at the head
    public Token peek() {
        if (idx >= tokens.size()) return Token.T_EOF;
        return tokens.get(idx);
    }

    /// the current token at the head, but pops it off
    public Token pop() {
        final var ret = peek();
        advance();
        return ret;
    }

    /// advances by one, then peeks
    public Token next() {
        advance();
        return peek();
    }
}
