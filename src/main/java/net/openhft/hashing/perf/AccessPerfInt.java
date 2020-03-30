package net.openhft.hashing;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import static net.openhft.hashing.Util.NATIVE_LITTLE_ENDIAN;
import static java.nio.charset.StandardCharsets.*;

@State(Scope.Thread)
public class AccessPerfInt {
    private static Access<byte[]> COMPACT_ACCESS = CompactLatin1CharSequenceAccess.INSTANCE;
    private static Access<CharSequence> CHARSEQ_ACCESS = CharSequenceAccess.nativeCharSequenceAccess();
    private static Access<Object> UNSAFE_ACCESS = UnsafeAccess.INSTANCE;

    private int N = 10007;

    @Param({"0"})
    private int alignOff;

    @Param({"true", "false"})
    private boolean benchCompact;

    private byte[] latinBytes;
    private byte[] utf16Bytes;
    private String latinString;
    private String utf16String;

    @Setup
    public void createData() {
        latinBytes = new byte[N];
        char[] utf16Chars = new char[N];
        for (int i = 0; i < N; i++) {
            latinBytes[i] = (byte)i;
            utf16Chars[i] = (char)i;
        }

        latinString = new String(latinBytes, ISO_8859_1);
        utf16String = new String(utf16Chars);
        utf16Bytes = utf16String.getBytes(UTF_16);
    }

    @Benchmark
    public void benchIntCompact(Blackhole bh) {
        if (benchCompact) {
            for (int i = 0; i < N - 7; i++) {
              bh.consume(COMPACT_ACCESS.getInt(latinBytes, i * 2 + alignOff));
            }
        } else {
            for (int i = 0; i < N - 7; i++) {
              bh.consume(UNSAFE_ACCESS.getInt(utf16Bytes, UnsafeAccess.BYTE_BASE + i * 2 + alignOff));
            }
        }
    }
    @Benchmark
    public void benchIntCompactUnsigned(Blackhole bh) {
        if (benchCompact) {
            for (int i = 0; i < N - 7; i++) {
              bh.consume(COMPACT_ACCESS.getUnsignedInt(latinBytes, i * 2 + alignOff));
            }
        } else {
            for (int i = 0; i < N - 7; i++) {
              bh.consume(UNSAFE_ACCESS.getUnsignedInt(utf16Bytes, UnsafeAccess.BYTE_BASE + i * 2 + alignOff));
            }
        }
    }

    @Benchmark
    public void benchIntGeneric(Blackhole bh) {
        String str = benchCompact ? latinString : utf16String;
        for (int i = 0; i < N - 7; i++) {
          bh.consume(CHARSEQ_ACCESS.getInt(str, i * 2 + alignOff));
        }
    }
    @Benchmark
    public void benchIntGenericUnsigned(Blackhole bh) {
        String str = benchCompact ? latinString : utf16String;
        for (int i = 0; i < N - 7; i++) {
          bh.consume(CHARSEQ_ACCESS.getUnsignedInt(str, i * 2 + alignOff));
        }
    }
}
