package klaxon.klaxon.jbest.token;

import static klaxon.klaxon.jbest.token.Operation.opEx;

public sealed interface Token {
    Token.EOF T_EOF = new EOF();

    record Integer(int value) implements Token { }
    record Plus() implements Token { }
    record Minus() implements Token { }
    record Star() implements Token { }
    record FwdSlash() implements Token { }
    record EOF() implements Token { }
    
    static int precedence(Token tok) {
        return switch (tok) {
            case Integer _, EOF _ -> throw opEx(tok);
            case Minus _, Plus _ -> 10;
            case FwdSlash _, Star _ -> 20;
        };
    }
}

