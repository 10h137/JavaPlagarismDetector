package comparison;

import comparison.algorithms.ComparisonAlgorithm;
import comparison.resultObjects.FileComparison;
import normalisation.Normaliser;
import normalisation.elements.elementContainers.JavaFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Runner {


    public static List<FileComparison> run(Set<Normaliser.Features> enabled_features, File input_dir, ComparisonAlgorithm algorithm) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {


        int THRESHOLD = -1;

        Normaliser normaliser = new Normaliser(enabled_features);
        List<File> files = Files.walk(input_dir.toPath())
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .map(File::new)
                .collect(Collectors.toList());

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
        comparisons = comparisons.stream().filter(x -> x.getScore() > THRESHOLD).collect(Collectors.toList());

        return  comparisons;

    }


}
