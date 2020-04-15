package comparison.resultObjects;

import comparison.algorithms.ComparisonAlgorithm;
import normalisation.elements.elementContainers.ClassObject;
import normalisation.elements.elementContainers.JavaFile;
import normalisation.elements.elementContainers.Method;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class FileComparison implements Comparable, Serializable {


    // TODO change
    private final int THRESHOLD = -1;
    private final JavaFile file1;
    private final JavaFile file2;
    private int algorithm_score = 0;
    private List<MethodComparison> method_comparisons = new ArrayList<>();

    /**
     * Creates a file comparison by comparing all methods in file 1 with all methods in file 2
     * and taking only the method comparisons that have the same strongest match in the other file
     * e.g. the strongest match for method1 in file 1 is with method1 in file 2 and
     * the strongest match for method1 in file 2 is with method1 in file 1
     *
     * @param file1
     * @param file2
     * @param alg   - Selected algorithm instance
     */
    public FileComparison(JavaFile file1, JavaFile file2, ComparisonAlgorithm alg) {
        this.file1 = file1;
        this.file2 = file2;
        // get all method comparisons that exceed a certain similarity threshold
        method_comparisons =
                compareMethods(file1, file2).stream()
                .filter(x -> x.getTotalScore() > THRESHOLD)
                .collect(Collectors.toList());
        algorithm_score = (int) (alg.compareFiles(file1, file2) * 100);
    }

    public List<MethodComparison> getMethod_comparisons() {
        return method_comparisons;
    }

    public JavaFile getFile1() {
        return file1;
    }

    public JavaFile getFile2() {
        return file2;
    }

    /**
     * Returns the algorithm comparison score
     *
     * @return algorithm comparison score
     */
    public int getScore() {
        return algorithm_score;
    }

    /**
     * Generates a name for the file comparison
     *
     * @return comparison name
     */
    public String getName() {
        return file1.getFile().getName() + " <--> " + file2.getFile().getName();
    }

    /**
     * Generates a report string by concatenating each method comparisons string and the algorithm score
     *
     * @return report string
     */
    public String getReport() {

        StringBuilder sb = new StringBuilder();
        sb.append("Text Comparison Algorithm Score ").append(algorithm_score).append("\n\n");
        sb.append("Suspected Methods -> \n");
        for (MethodComparison method_comparison : method_comparisons) {
            sb.append(method_comparison.getReport()).append("\n");
        }

        return sb.toString();
    }


    @Override
    public int compareTo(Object o) {
        return ((FileComparison) o).getScore() - this.getScore();
    }


    public static List<MethodComparison> compareMethods(JavaFile file1, JavaFile file2) {
        List<Method> methods1 = file1.getClasses().stream()
                .map(ClassObject::getMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Method> methods2 = file2.getClasses().stream()
                .map(ClassObject::getMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Set<Method> methods_to_be_processed = new HashSet<>(methods1);
        methods_to_be_processed.addAll(methods2);

        List<MethodComparison> comparisons = new ArrayList<>();
        for (int i = 0; i < methods1.size(); i++) {
            for (int j = i; j < methods2.size(); j++) {
                comparisons.add(new MethodComparison(methods1.get(i), methods2.get(j)));
            }
        }

        List<MethodComparison> best_comparisons = new ArrayList<>();
        // TODO check if in descending order
        comparisons.sort(Comparator.comparingInt(MethodComparison::getTotalScore));
        Collections.reverse(comparisons);


        for (MethodComparison comparison : comparisons) {
            if (methods_to_be_processed.contains(comparison.m1) && methods_to_be_processed.contains(comparison.m2)) {
                best_comparisons.add(comparison);
            }
            methods_to_be_processed.remove(comparison.m1);
            methods_to_be_processed.remove(comparison.m2);
        }

        return best_comparisons;

    }
}
