package normalisation;

import comparison.algorithms.ComparisonAlgorithm;
import comparison.algorithms.FingerprintComparison;
import comparison.algorithms.StringComparison;
import comparison.resultObjects.FileComparison;
import normalisation.elements.elementContainers.JavaFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.EnumSet;

public class TestPerformance {

    final static String DIR_PREFIX = "src/test/java/normalisation/";

    @Test
    public void testNormalisationPerformance() throws Exception {
        ComparisonAlgorithm alg = new StringComparison();
        long startTime = System.currentTimeMillis()/1000;
        for(int i = 0 ; i< 1000 ; i ++){
            Normaliser n = new Normaliser(EnumSet.allOf(Normaliser.Features.class));
            JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
            JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));
            n.normaliseFile(base);
            n.normaliseFile(test);
        }

        long endTime = System.currentTimeMillis()/1000;
        long duration = (endTime - startTime);
        System.out.println(duration);
    }


    @Test
    public void testStringAlgorithmPerformance() throws Exception {
        ComparisonAlgorithm alg = new StringComparison();
        long startTime = System.currentTimeMillis()/1000;
        int num_files = 1000;
        for(int i = 0 ; i< num_files*num_files ; i ++){
            JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
            JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));
            FileComparison comp = new FileComparison(base, test, alg);
        }

        long endTime = System.currentTimeMillis()/1000;

        long duration = (endTime - startTime);
        System.out.println(duration);
    }

    @Test
    public void testFingerPrintAlgorithmPerformance() throws Exception {
        ComparisonAlgorithm alg = new FingerprintComparison();
        long startTime = System.currentTimeMillis()/1000;
        int num_files = 1000;
        for(int i = 0 ; i< num_files*num_files ; i ++){
            JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
            JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));
            FileComparison comp = new FileComparison(base, test, alg);
        }

        long endTime = System.currentTimeMillis()/1000;

        long duration = (endTime - startTime);
        System.out.println(duration);
    }

}
