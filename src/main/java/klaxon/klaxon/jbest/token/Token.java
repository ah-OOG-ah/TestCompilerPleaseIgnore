package klaxon.klaxon.jbest.token;

public sealed interface Token {
    record Integer(int value) implements Token { }
    record Plus() implements Token { }
    record Minus() implements Token { }
    record Star() implements Token { }
    record FwdSlash() implements Token { }
    record EOF() implements Token { }
}

