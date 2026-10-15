package org.paternostro.elkromm.tools.model.actions;

import java.util.HashMap;
import java.util.Map;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.Medium;

/**
 * Creates {@link ModelReader}s and {@link ModelWriter}s for the registered
 * {@link Medium} media.
 * <p>
 * The parameter type is tied to the medium by the signatures of the methods:
 * {@code registerReader()} and {@code getReader()} share the same {@code R},
 * so the only unchecked cast is in the getters and cannot fail as long as
 * providers are registered through the {@code register*()} methods.
 * <p>
 * The factory is a singleton ({@link #getFactory()}) that already knows the readers and writers
 * of the four built-in media ({@link Medium#BACKUP}, {@link Medium#PANEL}, {@link Medium#JSON},
 * {@link Medium#PROPERTIES}, the latter write only). More can be added with
 * {@link #registerReader(Medium, ReaderProvider)} and
 * {@link #registerWriter(Medium, WriterProvider)}: registration is not synchronized, so do it
 * at start-up.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class ModelActionFactory {
    private static ModelActionFactory   instance = null;

    /**
     * Returns the shared factory.
     *
     * @return the factory
     */
    public static ModelActionFactory getFactory()
    {
        ModelActionFactory  retval = instance;

        if (retval == null) {
            synchronized(ModelActionFactory.class) {
                retval = instance;
                if (retval == null) {
                    retval = instance = new ModelActionFactory();
                }
            }
        }

        return retval;
    }

    private final Map<Medium<?, ?>, ReaderProvider<?>> readers = new HashMap<>();
    private final Map<Medium<?, ?>, WriterProvider<?>> writers = new HashMap<>();

    private ModelActionFactory() {
        this.registerReader(Medium.BACKUP,      BackupReader::new);
        this.registerWriter(Medium.BACKUP,      BackupWriter::new);
        this.registerReader(Medium.PANEL,       PanelReader::new);
        this.registerWriter(Medium.PANEL,       PanelWriter::new);
        this.registerReader(Medium.JSON,        JSONReader::new);
        this.registerWriter(Medium.JSON,        JSONWriter::new);
        // no reader for Medium.PROPERTIES: its content is incomplete
        this.registerWriter(Medium.PROPERTIES,  PropertiesWriter::new);
    }

    /**
     * Registers the provider of the readers of a medium, replacing any previous one.
     *
     * @param <R> parameter needed to read from the medium
     * @param medium the medium, which must be readable
     * @param provider creates the readers of the medium
     * @throws IllegalArgumentException if an argument is {@code null} or the medium is not readable
     */
    public <R> void registerReader(Medium<R, ?> medium, ReaderProvider<R> provider) {
        if (medium == null) throw new IllegalArgumentException("Medium cannot be null");
        if (provider == null) throw new IllegalArgumentException("Provider cannot be null");
        if (!medium.isReadable()) throw new IllegalArgumentException("Medium " + medium + " not readable");

        readers.put(medium, provider);
    }

    /**
     * Registers the provider of the writers of a medium, replacing any previous one.
     *
     * @param <W> parameter needed to write to the medium
     * @param medium the medium, which must be writable
     * @param provider creates the writers of the medium
     * @throws IllegalArgumentException if an argument is {@code null} or the medium is not writable
     */
    public <W> void registerWriter(Medium<?, W> medium, WriterProvider<W> provider) {
        if (medium == null) throw new IllegalArgumentException("Medium cannot be null");
        if (provider == null) throw new IllegalArgumentException("Provider cannot be null");
        if (!medium.isWritable()) throw new IllegalArgumentException("Medium " + medium + " not writable");

        writers.put(medium, provider);
    }

    /**
     * Creates a reader for a medium.
     *
     * @param <R> parameter needed to read from the medium
     * @param medium the medium to read from, which must be readable
     * @param param what the medium needs: a stream, a logged-in facade...
     * @return the reader
     * @throws IllegalArgumentException if the medium is {@code null} or not readable
     * @throws ElkrommException if no reader is registered for the medium, or it cannot be created
     */
    @SuppressWarnings("unchecked")
    public <R> ModelReader getReader(Medium<R, ?> medium, R param) throws ElkrommException {
        if (medium == null) throw new IllegalArgumentException("Medium cannot be null");
        if (!medium.isReadable()) throw new IllegalArgumentException("Medium " + medium + " not readable");

        ReaderProvider<R> provider = (ReaderProvider<R>) readers.get(medium);

        if (provider == null) throw new ElkrommException("No reader registered for " + medium);

        return provider.create(param);
    }

    /**
     * Creates a writer for a medium.
     *
     * @param <W> parameter needed to write to the medium
     * @param medium the medium to write to, which must be writable
     * @param param what the medium needs: a stream, a logged-in facade...
     * @return the writer
     * @throws IllegalArgumentException if the medium is {@code null} or not writable
     * @throws ElkrommException if no writer is registered for the medium, or it cannot be created
     */
    @SuppressWarnings("unchecked")
    public <W> ModelWriter getWriter(Medium<?, W> medium, W param) throws ElkrommException {
        if (medium == null) throw new IllegalArgumentException("Medium cannot be null");
        if (!medium.isWritable()) throw new IllegalArgumentException("Medium " + medium + " not writable");

        WriterProvider<W> provider = (WriterProvider<W>) writers.get(medium);

        if (provider == null) throw new ElkrommException("No writer registered for " + medium);

        return provider.create(param);
    }
}
