package org.paternostro.elkromm.tools;

import java.io.InputStream;
import java.io.OutputStream;

import org.paternostro.elkromm.ElkrommFacade;

/**
 * An external representation of the model: a backup file, a JSON file, the
 * panel itself...
 * <p>
 * The type parameters are the type of the parameter needed to read from the
 * medium ({@code R}) and to write to it ({@code W}); a direction that the
 * medium does not support has type {@link Unsupported}. Media are compared by
 * identity.
 *
 * @param <R> parameter needed to read the model from this medium
 * @param <W> parameter needed to write the model to this medium
 */
public final class Medium<R, W> {
    public static final Medium<InputStream, OutputStream>       BACKUP      = readWrite("backup");
    public static final Medium<ElkrommFacade, ElkrommFacade>    PANEL       = readWrite("panel");
    public static final Medium<InputStream, OutputStream>       JSON        = readWrite("json");
    public static final Medium<Unsupported, OutputStream>       PROPERTIES  = writeOnly("properties");

    private final String    name;
    private final boolean   readable;
    private final boolean   writable;

    // Private on purpose: the flags must agree with the type parameters, and the factory methods guarantee it
    private Medium(String name, boolean readable, boolean writable) {
        this.name       = name;
        this.readable   = readable;
        this.writable   = writable;
    }

    /** Defines a medium that can be both read and written. */
    public static <R, W> Medium<R, W> readWrite(String name) {
        return new Medium<>(name, true, true);
    }

    /** Defines a medium that can only be read. */
    public static <R> Medium<R, Unsupported> readOnly(String name) {
        return new Medium<>(name, true, false);
    }

    /** Defines a medium that can only be written. */
    public static <W> Medium<Unsupported, W> writeOnly(String name) {
        return new Medium<>(name, false, true);
    }

    public String getName() {
        return name;
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWritable() {
        return writable;
    }

    @Override
    public String toString() {
        return name;
    }
}
