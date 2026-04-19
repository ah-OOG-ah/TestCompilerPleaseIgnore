package klaxon.klaxon.jbest.codegen.elf;

import static klaxon.klaxon.jbest.Util.bOfASCII;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import java.util.List;

public class StringTableSection extends Section {
    public static final int STN_UNDEF = 0x0;
    private final Object2IntArrayMap<String> indices = new Object2IntArrayMap<>();

    StringTableSection(String name, int nameIdx, long entsize) {
        super(name, nameIdx, Type.SHT_STRTAB, List.of(), entsize);
        // The first byte must be null, so index 0 represents a null/none string
        data.add((byte) 0);
        indices.defaultReturnValue(STN_UNDEF);
    }

    /// Returns a valid index of the string in the table.
    private int findString(String s) {
        final int idx = indices.getInt(s);
        if (idx != indices.defaultReturnValue()) return idx;

        // It's perfectly valid for string table indices to start in the middle of a string. To save space, we search
        // for candidates to do this to.
        for (var e : indices.object2IntEntrySet()) {
            final var str = e.getKey();
            if (str.endsWith(s)) {
                return e.getIntValue() + str.lastIndexOf(s);
            }
        }

        return STN_UNDEF;
    }

    /// @param s The string to add.
    /// @return The index of the added string.
    /// @throws IllegalArgumentException if s is not ASCII, or if s contains a null terminator.
    public int addString(String s) {
        if (s == null) return STN_UNDEF;

        final int idx = findString(s);
        if (idx != STN_UNDEF) return idx;

        final var bytes = bOfASCII(s);
        for (final byte b : bytes)
            if (b == 0) throw new IllegalArgumentException("Cannot add string with null terminator to table!");

        final int ret = data.size();
        data.addAll(bytes);
        data.add((byte) 0); // null terminator
        indices.put(s, ret);
        return ret;
    }
}
