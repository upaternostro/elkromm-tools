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
 * <p>
 * Both parameters are things the caller opens (and closes): a stream, or a logged-in
 * {@link ElkrommFacade}. Neither the readers nor the writers close them.
 * <p>
 * New media can be defined with {@link #readWrite(String)}, {@link #readOnly(String)} and
 * {@link #writeOnly(String)}, and made usable by registering a provider in
 * {@link org.paternostro.elkromm.tools.model.actions.ModelActionFactory}.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 *
 * @param <R> parameter needed to read the model from this medium
 * @param <W> parameter needed to write the model to this medium
 */
public final class Medium<R, W> {
    /**
     * Hi-Connect backup file: a binary stream that starts with {@link CopyConfig#MAGIC}. The
     * parameter is the stream to read from or to write to.
     */
    public static final Medium<InputStream, OutputStream>       BACKUP      = readWrite("backup");
    /**
     * The panel itself (or the elkromm emulator). The parameter is an {@link ElkrommFacade} that
     * the caller has already connected and logged in, and will log out and close.
     */
    public static final Medium<ElkrommFacade, ElkrommFacade>    PANEL       = readWrite("panel");
    /**
     * JSON representation of the model, pretty-printed. The parameter is the stream to read from or
     * to write to. Reading is not implemented yet.
     */
    public static final Medium<InputStream, OutputStream>       JSON        = readWrite("json");
    /**
     * Properties in the format used by the elkromm emulator. Write only, and partial: it holds the
     * number of peripheral units, areas, partitions, inputs and outputs, leaving out the rest of the
     * configuration (users, keys, phone numbers...), and values equal to the emulator defaults are
     * omitted. The parameter is the stream to write to.
     */
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

    /**
     * Defines a medium that can be both read and written.
     *
     * @param <R> parameter needed to read from the medium
     * @param <W> parameter needed to write to the medium
     * @param name name of the medium, used in messages
     * @return the new medium
     */
    public static <R, W> Medium<R, W> readWrite(String name) {
        return new Medium<>(name, true, true);
    }

    /**
     * Defines a medium that can only be read.
     *
     * @param <R> parameter needed to read from the medium
     * @param name name of the medium, used in messages
     * @return the new medium
     */
    public static <R> Medium<R, Unsupported> readOnly(String name) {
        return new Medium<>(name, true, false);
    }

    /**
     * Defines a medium that can only be written.
     *
     * @param <W> parameter needed to write to the medium
     * @param name name of the medium, used in messages
     * @return the new medium
     */
    public static <W> Medium<Unsupported, W> writeOnly(String name) {
        return new Medium<>(name, false, true);
    }

    /**
     * @return the name of this medium
     */
    public String getName() {
        return name;
    }

    /**
     * @return {@code true} if the model can be read from this medium
     */
    public boolean isReadable() {
        return readable;
    }

    /**
     * @return {@code true} if the model can be written to this medium
     */
    public boolean isWritable() {
        return writable;
    }

    @Override
    public String toString() {
        return name;
    }
}
