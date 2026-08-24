package persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SaveManager {

    public static final String SAVE_DIRECTORY = "saves";
    public static final String SAVE_EXTENSION = ".senetsave";

    private SaveManager() {
    }

    public static void save(SaveData data, String slotName) throws IOException {
        Path directory = Paths.get(SAVE_DIRECTORY);
        Files.createDirectories(directory);

        Path file = directory.resolve(sanitize(slotName) + SAVE_EXTENSION);

        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(file)))) {
            out.writeObject(data);
        }
    }

    public static SaveData load(String slotName) throws IOException, ClassNotFoundException {
        Path file = Paths.get(SAVE_DIRECTORY).resolve(sanitize(slotName) + SAVE_EXTENSION);

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(file)))) {
            return (SaveData) in.readObject();
        }
    }

    public static List<String> listSaves() {
        Path directory = Paths.get(SAVE_DIRECTORY);

        if (!Files.isDirectory(directory)) {
            return Collections.emptyList();
        }

        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.endsWith(SAVE_EXTENSION))
                    .map(name -> name.substring(0, name.length() - SAVE_EXTENSION.length()))
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static boolean delete(String slotName) throws IOException {
        Path file = Paths.get(SAVE_DIRECTORY).resolve(sanitize(slotName) + SAVE_EXTENSION);
        return Files.deleteIfExists(file);
    }

    private static String sanitize(String name) {
        String trimmed = name == null ? "" : name.trim();

        if (trimmed.isEmpty()) {
            trimmed = "save";
        }

        return trimmed.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
