package klaxon.klaxon.jbest;

import static klaxon.klaxon.jbest.Reference.getInvalidInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;

public class AnitParseTest {
    @Test
    void repeatNumbers() {
        var input = getInvalidInput();
        var toks = Main.makeAllTokens(input);
        var e = assertThrowsExactly(IllegalArgumentException.class, () -> AST.getNode(toks.iterator()::next));
        assertEquals("Could not convert Integer[value=8392] into an arithmetic operation!", e.getMessage());
    }
}
