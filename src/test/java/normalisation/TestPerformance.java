package normalisation;

import com.github.s3curitybug.similarityuniformfuzzyhash.UniformFuzzyHash;
import comparison.algorithms.ComparisonAlgorithm;
import comparison.algorithms.FingerprintComparison;
import comparison.algorithms.StringComparison;
import comparison.resultObjects.FileComparison;
import normalisation.elements.elementContainers.JavaFile;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

public class TestPerformance {

    final static String DIR_PREFIX = "src/test/java/normalisation/";
    final static Map<Integer, Integer> file_counts = new TreeMap<>();
    final static int file_count = 10000;

    static {
        for (int i = 0; i <= file_count; i += 100) {
            file_counts.put(i * i, i);

        }
    }


    @Test
    public void testFingerPrintAlgorithmPerformance() throws Exception {
        ComparisonAlgorithm alg = new FingerprintComparison();
        File myObj = new File("FingerAlg.txt");
        PrintWriter myWriter = new PrintWriter(new FileOutputStream(myObj));

        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));

        // calculate average algorithm time
        long startTime = System.nanoTime();
        for (int h = 0; h < 20000; h++) {
            FileComparison comp = new FileComparison(base, test, alg);
        }
        long endTime = System.nanoTime();
        long average_comparison_time = ((endTime - startTime) / (long) 20000);

        long average_hash_time = getHashTime();
        System.out.println(average_comparison_time);
        for (Integer comparisons : file_counts.keySet()) {
            int num_imput_files = file_counts.get(comparisons);
            myWriter.write("file count: " + num_imput_files + " time: " +
                    ((average_comparison_time * comparisons) + (average_hash_time * num_imput_files)) / 1000000000
                    + "\n");
        }
        myWriter.close();
    }

    public long getHashTime() throws Exception {
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        // calculate average hash time
        long startTime = System.nanoTime();
        for (int i = 0; i < 20000; i++) {
            UniformFuzzyHash a = new UniformFuzzyHash(base.toString(), 5);
        }
        long endTime = System.nanoTime();
        return ((endTime - startTime) / (long) 20000);
    }

    @Test
    public void testStringAlgorithmPerformance() throws Exception {
        ComparisonAlgorithm alg = new StringComparison();
        File myObj = new File("StringComp.txt");
        PrintWriter myWriter = new PrintWriter(new FileOutputStream(myObj));
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));


        // calculate average algorithm time
        long startTime = System.nanoTime();
        for (int h = 0; h < 20000; h++) {
            FileComparison comp = new FileComparison(base, test, alg);
        }
        long endTime = System.nanoTime();
        long average_comparison_time = ((endTime - startTime) / (long) 20000);

        System.out.println(average_comparison_time);
        for (Integer comparisons : file_counts.keySet()) {
            int num_imput_files = file_counts.get(comparisons);
            myWriter.write("file count: " + num_imput_files + " time: " +
                    (average_comparison_time * comparisons)/ 1000000000
                    + "\n");
        }
        myWriter.close();
    }


    @Test
    public void testNormalisationPerformance() throws Exception {
        ComparisonAlgorithm alg = new StringComparison();
        File myObj = new File("NormalisationTime.txt");
        PrintWriter myWriter = new PrintWriter(new FileOutputStream(myObj));
        Normaliser n = new Normaliser(EnumSet.allOf(Normaliser.Features.class));

        // calculate average algorithm time
        long startTime = System.nanoTime();
        for (int h = 0; h < 100; h++) {
            JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));
            n.normaliseFile(test);
        }
        long endTime = System.nanoTime();
        long avg_normalise_time = ((endTime - startTime) / (long) 100);

        System.out.println(avg_normalise_time);
        for (Integer comparisons : file_counts.keySet()) {
            int num_imput_files = file_counts.get(comparisons);
            myWriter.write("file count: " + num_imput_files + " time: " +
                    (avg_normalise_time * num_imput_files)/ 1000000000
                    + "\n");
        }
        myWriter.close();

    }



}
