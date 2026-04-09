package klaxon.klaxon.jbest;

import static klaxon.klaxon.jbest.codegen.Interpreter.interpret;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FullStackTest {
    @Test
    void input01() {
        assertEquals(2 + 3 * 5 - 8 / 3, interpret("2 + 3 * 5 - 8 / 3"));
    }

    @Test
    void input02() {
        assertEquals(13 - 6 + 4 * 5 + 8 / 3, interpret("""
                13 -6+  4*
                5
                       +
                08 / 3"""));
    }

    @Test
    void input03() {
        assertThrows(IllegalArgumentException.class, () -> interpret("12 34 + -56 * / - - 8 + * 2"));
    }

    @Test
    void input04() {
        assertThrows(IllegalArgumentException.class, () -> interpret("""
                23 +
                18 -
                45.6 * 2
                / 18"""));
    }

    @Test
    void input05() {
        assertThrows(IllegalArgumentException.class, () -> interpret("23 * 456abcdefg"));
    }
}
