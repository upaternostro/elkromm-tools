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
 */
public class ModelActionFactory {
    private static ModelActionFactory   instance = null;

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
        // no reader for properties (incomplete content) {@link Medium#PROPERTIES}
        this.registerWriter(Medium.PROPERTIES,  PropertiesWriter::new);
    }

    public <R> void registerReader(Medium<R, ?> medium, ReaderProvider<R> provider) {
        if (medium == null) throw new IllegalArgumentException("Medium cannot be null");
        if (provider == null) throw new IllegalArgumentException("Provider cannot be null");
        if (!medium.isReadable()) throw new IllegalArgumentException("Medium " + medium + " not readable");

        readers.put(medium, provider);
    }

    public <W> void registerWriter(Medium<?, W> medium, WriterProvider<W> provider) {
        if (medium == null) throw new IllegalArgumentException("Medium cannot be null");
        if (provider == null) throw new IllegalArgumentException("Provider cannot be null");
        if (!medium.isWritable()) throw new IllegalArgumentException("Medium " + medium + " not writable");

        writers.put(medium, provider);
    }

    @SuppressWarnings("unchecked")
    public <R> ModelReader getReader(Medium<R, ?> medium, R param) throws ElkrommException {
        if (medium == null) throw new IllegalArgumentException("Medium cannot be null");
        if (!medium.isReadable()) throw new IllegalArgumentException("Medium " + medium + " not readable");

        ReaderProvider<R> provider = (ReaderProvider<R>) readers.get(medium);

        if (provider == null) throw new ElkrommException("No reader registered for " + medium);

        return provider.create(param);
    }

    @SuppressWarnings("unchecked")
    public <W> ModelWriter getWriter(Medium<?, W> medium, W param) throws ElkrommException {
        if (medium == null) throw new IllegalArgumentException("Medium cannot be null");
        if (!medium.isWritable()) throw new IllegalArgumentException("Medium " + medium + " not writable");

        WriterProvider<W> provider = (WriterProvider<W>) writers.get(medium);

        if (provider == null) throw new ElkrommException("No writer registered for " + medium);

        return provider.create(param);
    }
}
