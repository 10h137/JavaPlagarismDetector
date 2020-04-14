package normalisation;

import comparison.algorithms.ComparisonAlgorithm;
import comparison.algorithms.FingerprintComparison;
import comparison.algorithms.StringComparison;
import comparison.resultObjects.FileComparison;
import normalisation.elements.elementContainers.JavaFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

public class TestPerformance {

    final static String DIR_PREFIX = "src/test/java/normalisation/";
    final static HashMap<Integer, Integer> file_counts = new HashMap<>() ;
    final static int file_count = 5000;

    static {
        for(int i = 0 ;i < file_count; i+=100) {
            file_counts.put(i * i, i);
        };
    }
    @Test
    public void testNormalisationPerformance() throws Exception {
        ComparisonAlgorithm alg = new StringComparison();
        long startTime = System.currentTimeMillis()/1000;
        File myObj = new File("NormalisationTime.txt");
        PrintWriter myWriter = new PrintWriter(new FileOutputStream(myObj));
        for(int i = 0 ; i< file_count ; i ++){
            Normaliser n = new Normaliser(EnumSet.allOf(Normaliser.Features.class));
            JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));
            n.normaliseFile(test);
            if(i%100 == 0){
                long endTime = System.currentTimeMillis()/1000;
                long duration = (endTime - startTime);
                myWriter.write("file count: " + i + " time: " + duration +"\n");
                myWriter.flush();
            }

        }
    }

    @Test
    public void testFingerPrintAlgorithmPerformance() throws Exception {
        ComparisonAlgorithm alg = new FingerprintComparison();
        long startTime = System.currentTimeMillis()/1000;
        File myObj = new File("FingerAlg.txt");
        PrintWriter myWriter = new PrintWriter(new FileOutputStream(myObj));

        for(int i = 0 ; i< file_count*file_count ; i ++){
            if(file_counts.containsKey(i)){
                long endTime = System.currentTimeMillis()/1000;
                long duration = (endTime - startTime);
                myWriter.write("file count: " + file_counts.get(i) + " time: " + duration +"\n");
                myWriter.flush();
            }

            JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
            JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));
            FileComparison comp = new FileComparison(base, test, alg);
        }

        myWriter.close();
    }

    @Test
    public void testStringAlgorithmPerformance() throws Exception {
        ComparisonAlgorithm alg = new StringComparison();
        long startTime = System.currentTimeMillis()/1000;
        File myObj = new File("StringComp.txt");
        PrintWriter myWriter = new PrintWriter(new FileOutputStream(myObj));

        for(int i = 0 ; i< file_count*file_count ; i ++){
            if(file_counts.containsKey(i)){
                long endTime = System.currentTimeMillis()/1000;
                long duration = (endTime - startTime);
                myWriter.write("file count: " + file_counts.get(i) + " time: " + duration +"\n");
                myWriter.flush();
            }

            JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
            JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));
            FileComparison comp = new FileComparison(base, test, alg);
        }

        myWriter.close();
    }



}
