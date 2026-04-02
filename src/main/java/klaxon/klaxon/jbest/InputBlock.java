package klaxon.klaxon.jbest;

public class InputBlock {
    private final char[] input;
    private int index;

    public InputBlock(char[] input, int index) {
        this.input = input;
        this.index = index;
    }

    public InputBlock(String input) {
        this(input.toCharArray(), 0);
    }

    char next() {
        if (index >= input.length) return 128;
        return input[index++];
    }

    char nextNonWhitespace() {
        char n;
        do {
            n = next();
        } while (Character.isWhitespace(n));
        return n;
    }

    /// Note: the input can only be consumed once. If an EOF is put back, the block remains at EOF.
    void back() {
        if (index >= input.length) return;
        index--;
    }
}
