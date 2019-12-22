import comparison.ComparisonAlgorithm;
import comparison.ComparisonResult;
import normalisation.Normaliser;
import report.ReportGenerator;

import java.io.*;
import java.io.File;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import normalisation.Normaliser.Features;

class Main {



    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        String comparison_algorithm = "";
        List<ComparisonResult> comparison_results = new ArrayList<>();
        List<java.io.File> normalised_files = new ArrayList<>();
        String input_directory = args[0];
        String output_directory = args[1];


        try {
            normalised_files = normalise(new HashSet<>(), input_directory, output_directory);
            comparison_results = compare(normalised_files, comparison_algorithm);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // generate reports from comparisons
        List<File> reports = generateReports(comparison_results);

        // remove temporary files
        cleanUp(normalised_files);

    }

    /**
     * Normalises all files in the input directory by creating a copy in the output directory and modifying them
     * Only the enabled normalisation features will be applied
     * @param enabled_features Set of enabled feature enums
     * @return  List of normalised file objects
     * @throws IOException
     */
    private static List<File> normalise(Set<Features> enabled_features, String input_directory, String output_directory) throws IOException {
        Normaliser normaliser = new Normaliser(enabled_features, output_directory);

        List<File> normalised_files;
        try (java.util.stream.Stream<Path> paths = Files.walk(Paths.get(input_directory))) {
            normalised_files = paths.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .map(File::new)
                    .map(input -> {
                        try {
                            return normaliser.normaliseFile(input);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).collect(Collectors.toList());
        }

        if(normalised_files.contains(null)) throw new IOException();
        return normalised_files;

    }

    /**
     * Performs the comparison between all normalised files using the specified algorithm, returning a list of comparison results
     * @param files list of normalised files
     * @param algorithm string specifying the comparison algorithms to use
     * @return list of comparison result objects
     * @throws Exception thrown if the selected algorithm isn't valid
     */
    private static List<ComparisonResult> compare(List<File> files, String algorithm) throws Exception {

        // uses reflection to create a comparison algorithm instance from the given string
        ComparisonAlgorithm comparison_algorithm = (ComparisonAlgorithm) Class.forName("comparison." + algorithm).getConstructor().newInstance();
        // compare every file to every other file
        return comparison_algorithm.compareFiles(files);
    }

    /**
     * Generates a set of reports for each comparison, describing the similarities
     * @param comparison_results set of comparison results to generate the reports from
     * @return list of report files
     */
    private static List<File> generateReports(List<ComparisonResult> comparison_results) {
        return comparison_results.stream()
                .map(ReportGenerator::generateReport)
                .collect(Collectors.toList());
    }

    private static void cleanUp(List<File> normalised_files) {

    }

}
