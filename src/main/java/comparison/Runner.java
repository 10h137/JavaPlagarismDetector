package comparison;

import comparison.algorithms.ComparisonAlgorithm;
import comparison.resultObjects.FileComparison;
import normalisation.Normaliser;
import normalisation.elements.elementContainers.JavaFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class Runner {


    public static List<FileComparison> run(EnumSet<Normaliser.Features> enabled_features, File input_dir, ComparisonAlgorithm algorithm) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        int THRESHOLD = 70;

        Normaliser normaliser = new Normaliser(enabled_features);
        List<File> files = recurseDir(input_dir);


        List<JavaFile> java_files = new ArrayList<>();
        for (File file : files) {
            java_files.add(new JavaFile(file));
        }

        for (JavaFile java_file : java_files) {
            normaliser.normaliseFile(java_file);
        }

        List<FileComparison> comparisons = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            for (int j = i + 1; j < files.size(); j++) {
                comparisons.add(new FileComparison(java_files.get(i), java_files.get(j), algorithm));
            }
        }

        // filter only file comparisons that exceed a certain similarity threshold
        comparisons = comparisons.stream()
                .filter(x -> x.getScore() > THRESHOLD)
                .sorted()
                .collect(Collectors.toList());
        return comparisons;

    }


    /**
     * @param dir
     * @return
     * @throws IOException
     */
    static List<File> recurseDir(File dir) throws IOException {
        List<File> files = new ArrayList<>();
        Files.walk(dir.toPath())
                .filter(x -> !x.toFile().equals(dir))
                .forEach(x -> {
                    if (Files.isDirectory(x)) {
                        try {
                            files.addAll(recurseDir(x.toFile()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        files.add(x.toFile());
                    }
                });
        return files;
    }
}
