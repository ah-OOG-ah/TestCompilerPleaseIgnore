package klaxon.klaxon.jbest.token;

public enum Operation {
    ADD,
    SUB,
    MUL,
    DIV;

    public static Operation operationOf(Token t) {
        return switch (t) {
            case Token.Plus _ -> ADD;
            case Token.Minus _ -> SUB;
            case Token.Star _ -> MUL;
            case Token.FwdSlash _ -> DIV;
            default -> throw new IllegalArgumentException("Could not convert " + t + " into an arithmetic operation!");
        };
    }
}
