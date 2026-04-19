package klaxon.klaxon.jbest.codegen.elf;

import static klaxon.klaxon.jbest.Util.bOfI;
import static klaxon.klaxon.jbest.Util.bOfL;
import static klaxon.klaxon.jbest.codegen.elf.Section.Type.SHT_NULL;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteImmutableList;
import java.util.List;

public class Section {
    final String name;
    final int nameIdx;
    final Type type;
    final long flags;
    final long entsize;

    /// The size of a section header.
    static final short SH_SIZE = 0x40;

    enum Type {
        SHT_NULL(0x0),
        SHT_PROGBITS(0x1),
        SHT_STRTAB(0x3);

        private final int value;

        Type(int value) {
            this.value = value;
        }
    }

    enum Flag {
        SHF_ALLOC(0x2),
        SHF_EXECINSTR(0x4);

        private final long value;

        Flag(long value) {
            this.value = value;
        }
    }

    public final ByteArrayList data = new ByteArrayList();

    /// Create a section in the ELF.
    ///
    /// @param entsize If the section contains a table of fixed-size entries, the size of each entry. Otherwise 0.
    Section(String name, int nameIdx, Type type, List<Flag> flags, long entsize) {
        this.name = name;
        this.nameIdx = nameIdx;
        this.type = type;
        this.flags = compile(flags);
        this.entsize = entsize;
    }

    private long compile(List<Flag> flags) {
        long ret = 0;
        for (var f : flags) {
            ret |= f.value;
        }
        return ret;
    }

    /// @param address Address of this section in virtual memory
    /// @param offset  Offset of this section in the file
    public ByteArrayList header(long address, long offset) {
        final var buf = new ByteArrayList(SH_SIZE);
        buf.addAll(bOfI(nameIdx)); // Index of the name in the .shstrtab section
        buf.addAll(bOfI(type.value)); // Type of section
        buf.addAll(bOfL(flags)); // Flags for section
        buf.addAll(bOfL(address));
        buf.addAll(bOfL(offset));
        buf.addAll(bOfL(data.size()));
        buf.addAll(bOfI(ELF.SHN_UNDEF)); // sh_link - index of an associated section
        buf.addAll(bOfI(0)); // sh_info - Extra info
        buf.addAll(bOfL(ELF.PAGE_SIZE)); // Alignment
        buf.addAll(bOfL(entsize)); // Size of entries in this section
        return buf;
    }


    static final ByteImmutableList EMPTY_SEC_HEADER;
    static {
        final var buf = new ByteArrayList(SH_SIZE);
        buf.addAll(bOfI(0)); // No name...
        buf.addAll(bOfI(SHT_NULL.value)); // null type...
        buf.addAll(bOfL(0)); // no flags...
        buf.addAll(bOfL(0)); // no address...
        buf.addAll(bOfL(0)); // no offset...
        buf.addAll(bOfL(0)); // no size...
        buf.addAll(bOfI(ELF.SHN_UNDEF)); // no link...
        buf.addAll(bOfI(0)); // no info...
        buf.addAll(bOfL(0)); // no alignment...
        buf.addAll(bOfL(0)); // and no entries!
        EMPTY_SEC_HEADER = new ByteImmutableList(buf);
    }
}
