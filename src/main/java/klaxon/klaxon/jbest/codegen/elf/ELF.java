package klaxon.klaxon.jbest.codegen.elf;

import static it.unimi.dsi.fastutil.bytes.ByteImmutableList.of;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ELF {
    final ObjectArrayList<Section> sections = new ObjectArrayList<>();
    public final Section text;
    /// Section names are stored here.
    final StringTableSection shstrtab;
    /// Index of [#shstrtab]'s header
    final short shnShrstrtab;
    /// Index of [#text]'s header
    final short shnText;

    public ELF() {
        // The first name index is hardcoded, because we don't have it yet
        shstrtab = new StringTableSection(".shstrtab", 1);
        var sstni = shstrtab.addString(".shstrtab");
        assert(sstni == 1);
        sections.add(shstrtab);
        // All header numbers are one greater than sections.size, since idx 0 is SHN_UNDEF
        shnShrstrtab = (short) sections.size();

        text = new Section(".text", shstrtab.addString(".text"), SHT_PROGBITS, List.of(SHF_EXECINSTR, SHF_ALLOC), 0);
        sections.add(text);
        shnText = (short) sections.size();
    }

    /// Segments need to be aligned by this in virtual memory.
    static final int PAGE_SIZE = 0x1000;
    /// A faster way to take the modulo by page size is to & (PAGE_SIZE - 1)
    static final int PS_FMOD = PAGE_SIZE - 1;

    /// Index of null section header
    static final int SHN_UNDEF = 0x0;

    private static final short ELF_SIZE = 0x40;
    private static final short PH_SIZE = 0x38;

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

    /// @param entryPtr Address of the code in virtual address space.
    public ByteArrayList elfHeader(int entryPtr) {
        final var buf = new ByteArrayList(ELF_SIZE);
        buf.addAll(eIdent());
        buf.addAll(bOfIs(0x02, 0)); // Executable file type
        buf.addAll(bOfIs(0x3E, 0)); // Target ISA
        buf.addAll(bOfI(0x01)); // 1 for ELF v1
        buf.addAll(bOfL(entryPtr));
        buf.addAll(bOfL(ELF_SIZE)); // Program header table address. (follows ELF header)
        buf.addAll(bOfL(ELF_SIZE + PH_SIZE)); // Section header table address. (follows prog. header)
        buf.addAll(of(new byte[4])); // Processor-specific flags. Don't think we need it.
        buf.addAll(bOfS(ELF_SIZE)); // ELF Header size
        buf.addAll(bOfS(PH_SIZE)); // Program header table entry size
        buf.addAll(bOfIs(0x01, 0)); // Number of program headers
        buf.addAll(bOfS(SH_SIZE)); // Section table entry size
        buf.addAll(sectionCount());
        buf.addAll(bOfS(shnShrstrtab));
        return buf;
    }

    /// @param offset The offset within the file where the program data begins.
    /// @param address The address code will be mapped to in virtual memory.
    public ByteArrayList programHeader(long offset, long address) {
        final var buf = new ByteArrayList(PH_SIZE);
        buf.addAll(bOfI(0x01)); // Segment type - loadable
        buf.addAll(bOfI(PF_R | PF_X)); // permissions
        buf.addAll(bOfL(offset));
        buf.addAll(bOfL(address));
        buf.addAll(bOfL(address)); // address of code in *physical* memory. irrelevant for System V.
        buf.addAll(bOfL(text.data.size())); // segment size on disk
        buf.addAll(bOfL(text.data.size())); // segment size in memory
        buf.addAll(bOfL(PAGE_SIZE)); // Alignment of segment in memory
        return buf;
    }

    public int headersSize() {
        // An extra SH, for the null section
        return ELF_SIZE + PH_SIZE + SH_SIZE * sections.size() + SH_SIZE;
    }

    public int size() {
        return headersSize() + sections.stream()
                .mapToInt(s -> s.data.size())
                .reduce(0, Integer::sum);
    }

    private static int roundUpToPageSize(int a) {
        // The bit twiddling only works if the page size is a power of two
        assert(Integer.bitCount(PAGE_SIZE) == 1);
        return (a & PS_FMOD) == 0 ? a : (a & -PAGE_SIZE) + PAGE_SIZE;
    }

    public void write(Path path) throws IOException {
        // First, we lay out the sections in memory and in the binary.
        // Remember, the zeroth section has a blank header
        final int[] secOffsets = new int[sections.size()];
        int offset = headersSize();
        final int[] secAddrs = new int[sections.size()];
        int addr = PAGE_SIZE; // first page reserved for nullptr & kernel stuff
        final int sectionCnt = sections.size();
        for (int i = 0; i < sectionCnt; ++i) {
            final int secSize = sections.get(i).data.size();
            secOffsets[i] = offset;
            offset += secSize;

            // Address is more complicated, since it has to be aligned with the page size. That means
            // (addr - offset) % PAGE_SIZE == 0. If needed, the address is shifted instead of the offset, because vaddr
            // space is free and file space isn't.
            var pageAlignedAddr = roundUpToPageSize(addr);
            addr = pageAlignedAddr + (offset & PS_FMOD);
            secAddrs[i] = addr;
            addr += secSize;
        }

        // Now we can start building the output buffer
        final var outBuf = new ByteArrayList(size());
        outBuf.addAll(elfHeader(secAddrs[shnText - 1]));
        outBuf.addAll(programHeader(secOffsets[shnText - 1], secAddrs[shnText - 1]));
        outBuf.addAll(Section.EMPTY_SEC_HEADER);
        for (int i = 0; i < sectionCnt; i++) {
            outBuf.addAll(sections.get(i).header(
                secAddrs[i],
                secOffsets[i]
            ));
        }

        // And one last time, for the section data.
        for (int i = 0; i < sectionCnt; i++) {
            outBuf.addAll(sections.get(i).data);
        }

        Files.write(path, outBuf.toByteArray(), CREATE, TRUNCATE_EXISTING);
    }
}
