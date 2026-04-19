package klaxon.klaxon.jbest.codegen.elf;

import static it.unimi.dsi.fastutil.bytes.ByteImmutableList.of;
import static klaxon.klaxon.jbest.Util.bOfI;
import static klaxon.klaxon.jbest.Util.bOfIs;
import static klaxon.klaxon.jbest.Util.bOfL;
import static klaxon.klaxon.jbest.Util.bOfS;
import static klaxon.klaxon.jbest.codegen.elf.Section.Flag.SHF_ALLOC;
import static klaxon.klaxon.jbest.codegen.elf.Section.Flag.SHF_EXECINSTR;
import static klaxon.klaxon.jbest.codegen.elf.Section.SH_SIZE;
import static klaxon.klaxon.jbest.codegen.elf.Section.Type.SHT_PROGBITS;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.nio.file.Path;
import java.util.List;

public class ELF {
    final ObjectArraySet<Section> sections = new ObjectArraySet<>();
    public final Section text;
    /// Section names are stored here.
    final StringTableSection shstrtab;

    public ELF() {
        // The first name index is hardcoded, because we don't have it yet
        shstrtab = new StringTableSection(".shstrtab", 1);
        var sstni = shstrtab.addString(".shstrtab");
        assert(sstni == 1);
        sections.add(shstrtab);

        text = new Section(".text", shstrtab.addString(".text"), SHT_PROGBITS, List.of(SHF_EXECINSTR, SHF_ALLOC), 0);
        sections.add(text);
    }

    /// Segments need to be aligned by this in virtual memory.
    static final int PAGE_SIZE = 0x1000;

    /// Index of null section header
    static final int SHN_UNDEF = 0x0;

    private static final int ELF_SIZE = 0x40;
    /// Technically, 0x0 is a valid entry point. However, I want to leave that open for nullptr.
    private static final long ENTRY_PTR = PAGE_SIZE;
    private static final int PH_SIZE = 0x38;

    private static final int PF_X = 0x1;
    private static final int PF_W = 0x2;
    private static final int PF_R = 0x4;

    private static ByteImmutableList eIdent() {
        return bOfIs(
                0x7F, 'E', 'L', 'F', // ELF magic
                0x02, // EI_CLASS: ELF64
                0x01, // EI_DATA: Little-endian
                0x01, // EI_VERSION: EV_CURRENT
                0x00, // EI_OSABI: ELFOSABI_NONE
                0x00, // EI_ABIVERSION: zero, this is statically linked
                0, 0, 0, 0, 0, 0, 0 // seven padding bytes
        );
    }

    public ByteImmutableList sectionCount() {
        var count = sections.size();
        if (count > 0x7F_FF) throw new IllegalStateException("Too many sections in ELF!");
        return bOfS((short) count);
    }

    public ByteArrayList elfHeader() {
        final var buf = new ByteArrayList(ELF_SIZE);
        buf.addAll(eIdent());
        buf.addAll(bOfIs(0x02, 0)); // Executable file type
        buf.addAll(bOfIs(0x3E, 0)); // Target ISA
        buf.addAll(bOfI(0x01)); // 1 for ELF v1
        buf.addAll(bOfL(ENTRY_PTR)); // Entry point address in process address space
        buf.addAll(bOfL(0x40)); // Program header table address. (follows ELF header)
        buf.addAll(bOfL(ELF_SIZE + PH_SIZE)); // Section header table address. (follows prog. header)
        buf.addAll(of(new byte[4])); // Processor-specific flags. Don't think we need it.
        buf.addAll(bOfIs(0x40, 0)); // ELF Header size
        buf.addAll(bOfIs(0x38, 0)); // Header table entry size
        buf.addAll(bOfIs(0x01, 0)); // Header table entry count
        buf.addAll(bOfIs(SH_SIZE, 0)); // Section table entry size
        buf.addAll(sectionCount());
        buf.addAll(bOfIs(0x01, 0)); // Index of the section names entry. I hereby declare it is the first section!
        return buf;
    }

    public ByteArrayList programHeader() {
        final var buf = new ByteArrayList(PH_SIZE);
        buf.addAll(bOfI(0x01)); // Segment type - loadable
        buf.addAll(bOfI(PF_R | PF_X)); // permissions
        buf.addAll(bOfL(ELF_SIZE + PH_SIZE )); // segment address in file
        buf.addAll(bOfL(ENTRY_PTR)); // address of segment in vmem
        buf.addAll(bOfL(ENTRY_PTR)); // address of code in physical memory. irrelevant for System V
        buf.addAll(bOfL(text.data.size())); // segment size on disk
        buf.addAll(bOfL(text.data.size())); // segment size in memory
        buf.addAll(bOfL(PAGE_SIZE)); // Alignment of segment in memory
        return buf;
    }

    public int size() {
        return ELF_SIZE
                + PH_SIZE
                + SH_SIZE * sections.size()
                + sections.stream().map(s -> s.data.size()).reduce(0, Integer::sum);
    }

    public void write(Path path) {
        final var outBuf = new ByteArrayList(size());

    }
}
