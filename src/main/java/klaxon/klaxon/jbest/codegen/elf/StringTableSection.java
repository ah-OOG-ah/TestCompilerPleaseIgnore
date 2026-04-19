package klaxon.klaxon.jbest.codegen.elf;

import java.util.List;

public class StringTableSection extends Section {
    public static final int STN_UNDEF = 0x0;

    StringTableSection(String name, int nameIdx, long entsize) {
        super(name, nameIdx, Type.SHT_STRTAB, List.of(), entsize);
    }

    /// @param s The string to add.
    /// @return The index of the added string.
    public int addString(String s) {
        return 0;
    }
}
