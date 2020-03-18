package normalisation;

import comparison.algorithms.ComparisonAlgorithm;
import comparison.algorithms.FingerprintComparison;
import comparison.resultObjects.FileComparison;
import normalisation.elements.elementContainers.JavaFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.EnumSet;

public class TestMain {

    final static String DIR_PREFIX = "src/test/java/normalisation/";

    @Test
    public void testNoComments() throws Exception {
        Normaliser n = new Normaliser(EnumSet.of(Normaliser.Features.REMOVE_COMMENTS));
        ComparisonAlgorithm finger = new FingerprintComparison();
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "NoComments.txt"));
        n.normaliseFile(base);
        n.normaliseFile(test);
        FileComparison comp = new FileComparison(test, base, finger);
        Assert.assertEquals(100, comp.getScore());
    }


    @Test
    public void testRenamedMethods() throws Exception {
        Normaliser n = new Normaliser(EnumSet.of(Normaliser.Features.STANDARDISE_METHOD_NAMES));
        ComparisonAlgorithm finger = new FingerprintComparison();
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "RenamedMethods.txt"));
        n.normaliseFile(base);
        n.normaliseFile(test);

        FileComparison comp = new FileComparison(base, test, finger);
        Assert.assertEquals(100, comp.getScore());
    }

    @Test
    public void testRenamedVariables() throws Exception {
        Normaliser n = new Normaliser(EnumSet.of(Normaliser.Features.STANDARDISE_VARIABLE_NAMES));
        ComparisonAlgorithm finger = new FingerprintComparison();
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "RenamedVariables.txt"));
        n.normaliseFile(base);
        n.normaliseFile(test);
        FileComparison comp = new FileComparison(base, test, finger);
        Assert.assertEquals(100, comp.getScore());
    }

    @Test
    public void testReorderGlobalVariables() throws Exception {
        Normaliser n = new Normaliser(EnumSet.of(Normaliser.Features.SORT_CLASS_MEMBERS));
        ComparisonAlgorithm finger = new FingerprintComparison();
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "ReorderedGlobalVariables.txt"));
        n.normaliseFile(base);
        n.normaliseFile(test);
        FileComparison comp = new FileComparison(base, test, finger);
        Assert.assertEquals(100, comp.getScore());
    }

    @Test
    public void testReorderImports() throws Exception {
        Normaliser n = new Normaliser(EnumSet.of(Normaliser.Features.ORDER_IMPORTS));
        ComparisonAlgorithm finger = new FingerprintComparison();
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "ReorderedImports.txt"));
        n.normaliseFile(base);
        n.normaliseFile(test);
        FileComparison comp = new FileComparison(base, test, finger);
        Assert.assertEquals(100, comp.getScore());
    }

    @Test
    public void testReorderMethods() throws Exception {
        Normaliser n = new Normaliser(EnumSet.of(Normaliser.Features.SORT_CLASS_MEMBERS));
        ComparisonAlgorithm finger = new FingerprintComparison();

        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "ReorderedMethods.txt"));
        n.normaliseFile(base);
        n.normaliseFile(test);
        FileComparison comp = new FileComparison(base, test, finger);

        Assert.assertEquals(100, comp.getScore());
    }

    @Test
    public void testReduceStructures() throws Exception {
        Normaliser n = new Normaliser(EnumSet.of(Normaliser.Features.REDUCE_TYPES));
        ComparisonAlgorithm finger = new FingerprintComparison();

        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "Interfaces.txt"));
        n.normaliseFile(base);
        n.normaliseFile(test);
        FileComparison comp = new FileComparison(base, test, finger);

        System.out.println(base.toString() + "\n\n\n");
        System.out.println(test.toString());
        Assert.assertEquals(100, comp.getScore());
    }

    @Test
    public void testAllChanged() throws Exception {
        Normaliser n = new Normaliser(EnumSet.allOf(Normaliser.Features.class));
        ComparisonAlgorithm finger = new FingerprintComparison();

        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        JavaFile test = new JavaFile(new File(DIR_PREFIX + "AllChanged.txt"));
        n.normaliseFile(base);
        n.normaliseFile(test);
        FileComparison comp = new FileComparison(base, test, finger);

        Assert.assertEquals(100, comp.getScore());
    }
}
