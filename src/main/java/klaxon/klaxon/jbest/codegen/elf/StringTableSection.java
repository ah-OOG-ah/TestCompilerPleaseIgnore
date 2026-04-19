package klaxon.klaxon.jbest.codegen.elf;

import static klaxon.klaxon.jbest.Util.bOfASCII;

import java.util.List;

public class StringTableSection extends Section {
    public static final int STN_UNDEF = 0x0;

    StringTableSection(String name, int nameIdx, long entsize) {
        super(name, nameIdx, Type.SHT_STRTAB, List.of(), entsize);
        // The first byte must be null, so index 0 represents a null/none string
        data.add((byte) 0);
    }

    /// @param s The string to add.
    /// @return The index of the added string.
    /// @throws IllegalArgumentException if s is not ASCII
    public int addString(String s) {
        final int ret = data.size();
        data.addAll(bOfASCII(s));
        return ret;
    }
}
