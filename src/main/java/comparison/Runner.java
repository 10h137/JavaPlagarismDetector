package comparison;

import com.google.common.util.concurrent.AtomicDouble;
import comparison.algorithms.ComparisonAlgorithm;
import comparison.resultObjects.FileComparison;
import javafx.concurrent.Task;
import javafx.scene.text.Text;
import main.GUI;
import normalisation.Normaliser;
import normalisation.Normaliser.Features;
import normalisation.elements.elementContainers.JavaFile;
import org.checkerframework.common.value.qual.IntRange;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Runner extends Task<List<FileComparison>> {

    private EnumSet<Features> enabled_features;
    private File input_dir;
    private ComparisonAlgorithm algorithm;
    private List<FileComparison> comparisons = new ArrayList<>();
    private GUI gui;
    private Text file_count;

    public Runner(EnumSet<Features> enabled_features, File input_dir, ComparisonAlgorithm algorithm, GUI gui, Text file_count) {
        this.enabled_features = enabled_features;
        this.input_dir = input_dir;
        this.algorithm = algorithm;
        this.gui = gui;
        this.file_count = file_count;


    }

    /**
     * @param dir
     * @return
     * @throws IOException
     */
    static Set<File> recurseDir(File dir, Set<File> visited) throws IOException {
        Set<File> files = new HashSet<>();
        if (visited.contains(dir)) return files;
        visited.add(dir);

        Files.walk(dir.toPath())
                .filter(x -> !x.toFile().equals(dir))
                .forEach(x -> {
                    if (Files.isDirectory(x)) {
                        try {
                            files.addAll(recurseDir(x.toFile(), visited));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        files.add(x.toFile());
                    }
                });
        return files;
    }


    @Override
    public List<FileComparison> call() {


        int THRESHOLD = 70;

        Normaliser normaliser = new Normaliser(enabled_features);
        List<File> files = null;
        try {
            files = new ArrayList<>(recurseDir(input_dir, new HashSet<>()));
        } catch (IOException e) {
            e.printStackTrace();
        }


        List<JavaFile> java_files = new ArrayList<>();
        double percent = 0;

        for (File file : files) {
            if (!file.getName().endsWith(".java") && !file.getName().endsWith(".txt")) {
                continue;

            }
            try {
                java_files.add(new JavaFile(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        double normalisation_percentage = (double) 50 / java_files.size();


        file_count.setText("Files -> " + java_files.size());
        for (JavaFile java_file : java_files) {
            normaliser.normaliseFile(java_file);
            percent += normalisation_percentage;
            this.updateProgress(percent, 100);
        }

        int count = 0;
        for (int i = 0; i < java_files.size(); i++) {
            count += IntStream.range(i + 1, java_files.size()).count();
        }
        double comparison_percentage = (double) 50 / count;


        List<FileComparison> comparisons = new ArrayList<>();
        for (int i = 0; i < java_files.size(); i++) {
            for (int j = i + 1; j < java_files.size(); j++) {
                comparisons.add(new FileComparison(java_files.get(i), java_files.get(j), algorithm));
                percent += comparison_percentage;
                this.updateProgress(percent, 100);

            }
        }

        // filter only file comparisons that exceed a certain similarity threshold
        comparisons = comparisons.stream()
                .filter(x -> x.getScore() > THRESHOLD)
                .sorted()
                .collect(Collectors.toList());
        this.comparisons = comparisons;
        return comparisons;

    }

    @Override
    protected void succeeded() {
        gui.updateList(this.comparisons);
    }

}
