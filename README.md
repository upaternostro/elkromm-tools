# elkromm-tools

Command line tools built on [elkromm](https://github.com/upaternostro/elkromm), the Java
library that speaks Elkron's alarm protocol. They work on the configuration of an Elkron
MP-508 control panel: today they copy it from a Hi-Connect backup file or from a live panel
to other formats.

**Status: work in progress.** Nothing has been released yet (`0.1-SNAPSHOT`), and parts of
the tools are still missing (see the [Roadmap](#roadmap)).

## Disclaimer

Elkron is a trademark of Urmet S.p.A. Hi-Connect is a software product developed
and distributed by Urmet S.p.A. under the Elkron brand.

This project is not endorsed, sponsored, affiliated with, nor approved by Urmet. The
knowledge about the protocol comes from the same independent, interoperability-oriented
analysis described in the [elkromm disclaimer](https://github.com/upaternostro/elkromm#disclaimer),
which also applies here.

These tools are intended for use with alarm systems that the user legitimately owns or
is otherwise authorized to access.

**This software interacts with a physical security system.** It is provided "as is",
without warranty of any kind, as detailed in the EUPL v1.2 (see [License](#license)).
It must not be relied upon as the sole means of protecting life, property, or safety.
Use entirely at your own risk.

At the moment the tools only *read* from the panel: nothing is written to it.

## Features

- Copy the configuration of a panel to one or more files in a single run, reading it from:
  - a Hi-Connect backup file (or standard input)
  - a live panel, over the LAN
- Output formats:
  - a Hi-Connect backup file, rebuilt from the decoded configuration
  - a pretty-printed JSON file
  - a properties file for the elkromm [emulator](https://github.com/upaternostro/elkromm#emulator).
    The format cannot express the whole configuration (see [Output formats](#output-formats))
- Readers and writers are pluggable: new formats can be added without touching the tool
  (see [Adding a medium](#adding-a-medium))

## Requirements

- Java 8 or later
- Maven
- [elkromm](https://github.com/upaternostro/elkromm), which in turn uses
  [mock-ipc](https://github.com/upaternostro/mock-ipc)
- An Elkron MP-508 v3 alarm system equipped with a LAN expansion board (only required to
  talk to a real panel)

## License

This software is licensed under EUPL v1.2 or above. See [LICENSE.md](LICENSE.md) for full license text.

## Usage

The tool is `org.paternostro.elkromm.tools.CopyConfig`, the main class of the jar. Unpack a
release archive (`.zip` or `.tar.gz`) and run:

```
java -jar elkromm-tools-<version>.jar -h
```

The `lib` folder, which holds the dependencies, must stay next to the jar. The same works on a
local build (see [Compile](#compile)), straight from the `target` folder:

```
java -jar target/elkromm-tools-<version>.jar -h
```

The same class can also be started from an IDE. In the examples below `CopyConfig` stands for
the command above.

### Options

Exactly one source is required, and at least one output.

| Option | Description |
|---|---|
| `-h`, `--help` | Print the usage and exit |
| `-f`, `--file <file>` | Source: a Hi-Connect backup file; use `-` to read from standard input |
| `-H`, `--host <host>` | Source: IP address or host name of a live panel |
| `-c`, `--plant-code <code>` | Plant code (default `55555555`) |
| `-P`, `--port <port>` | TCP port of the panel (default `8030`); only with `--host` |
| `-t`, `--tech-pin <pin>` | Technician user PIN; only with `--host` |
| `-b`, `--backup <file>` | Output: Hi-Connect backup file; use `-` for standard output |
| `-j`, `--json <file>` | Output: JSON file; use `-` for standard output |
| `-p`, `--properties <file>` | Output: emulator properties file; use `-` for standard output |

`-f` and `-H` are mutually exclusive, and every file given with `-f` must be a Hi-Connect
backup (reading JSON is not implemented yet). Standard output can be used by one output at
most.

When reading from a panel and `--tech-pin` is missing, the PIN is read from the environment
variable `ELKROMM_PIN` and, failing that, asked on the console. Passing a PIN on the command
line leaves it in the shell history and in the process list, so the variable or the prompt are
preferable.

On bad arguments the tool prints the usage on standard error and exits with status 1. If the source
cannot be read, or an output cannot be written, it logs the error and exits with status 2.

### Examples

Convert a backup made with Hi-Connect to JSON and to an emulator properties file:

```
CopyConfig -f backup.cfg -c 55555555 -j config.json -p emulator.properties
```

Make a backup of a live panel (the PIN is asked on the console):

```
CopyConfig -H 192.168.1.50 -c 55555555 -b panel.cfg
```

Pipe a backup into JSON on standard output:

```
CopyConfig -f - -c 55555555 -j - < backup.cfg
```

### Output formats

- **Backup**: the same binary format written by Hi-Connect, a signature followed by the
  protocol packets of every part of the configuration.
- **JSON**: the whole configuration, one object member per part (peripheral units, expansions
  with their inputs and outputs, keypads, readers, time programmer, areas and partitions, phone
  parameters and numbers, C200B parameters, SMS, PSTN/GSM, users, keys, parameters enablings).
- **Properties**: only the number of peripheral units, areas, partitions, inputs and outputs, in
  the format read by the elkromm emulator. Values equal to the emulator defaults are left out,
  and so are unused inputs and outputs. Users, keys, phone numbers and the other parts of the
  configuration cannot be expressed in this format, so this output is partial by design and
  cannot be read back.

## Project structure

- `org.paternostro.elkromm.tools` — the `CopyConfig` tool, and `Medium`, which names the formats
  and places the configuration can be read from or written to
- `org.paternostro.elkromm.tools.model` — `Model`, the in-memory configuration, independent of
  the medium it comes from
- `org.paternostro.elkromm.tools.model.actions` — one reader and/or writer per medium, and
  `ModelActionFactory`, which creates them
- `org.paternostro.elkromm.tools.serializers` — Jackson serializers for the JSON layout of some
  parts of the model

Reading and writing are decoupled from the medium: a `ModelReader` produces a `Model` and a
`ModelWriter` consumes it, so any source can be combined with any output. A reader or writer
never closes the stream or the connection it works on: whoever opened it closes it.

## Adding a medium

A `Medium<R, W>` carries the type of the parameter needed to read from it (`R`) and to write
to it (`W`); a direction that is not supported has type `Unsupported`. The factory ties the
parameter to the medium at compile time, so a medium cannot be given the wrong kind of
parameter.

```java
Medium<InputStream, Unsupported> xml = Medium.readOnly("xml");
ModelActionFactory factory = ModelActionFactory.getFactory();

factory.registerReader(xml, XmlReader::new);        // XmlReader(InputStream) implements ModelReader

ModelReader reader = factory.getReader(xml, inputStream);
Model model = reader.read();
```

Registration is not synchronized: do it at start-up.

## Compile

```
mvn install
```

The build leaves in `target` the jar, a `lib` folder with the runtime dependencies, and the release
archives (`.zip` and `.tar.gz`).

## Documentation

The API is documented with Javadoc. Generate it locally with:

```
mvn javadoc:javadoc
```

The generated HTML is written to an `apidocs` folder under `target` (with the current version of
the Maven Javadoc plugin, `target/reports/apidocs`; the exact location depends on the plugin version).

## Roadmap

* read JSON, so that a JSON file can be used as a source
* restore a configuration to a panel
* a tool to compare two configurations
* tests

## Credits

* Anthropic Claude for Javadocs and README
