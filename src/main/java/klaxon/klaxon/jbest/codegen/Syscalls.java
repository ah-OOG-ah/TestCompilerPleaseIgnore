package klaxon.klaxon.jbest.codegen;

/// Provides helpers that directly inject system calls into the program. These don't rely on the compiler to make them
/// correct, you've been warned!
public class Syscalls {

    /// May clobber any and all registers. It also doesn't matter, since we're exiting!
    public static String exit(int value) {
        return """
               \s\smov eax, 60
               \s\smov edi,\s""" + value + """
               
                 syscall
               """;
    }
}
