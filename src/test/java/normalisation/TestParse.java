package normalisation;

import comparison.algorithms.ComparisonAlgorithm;
import comparison.algorithms.StringComparison;
import comparison.resultObjects.FileComparison;
import normalisation.elements.JavaElement;
import normalisation.elements.Variable;
import normalisation.elements.elementContainers.ElementContainer;
import normalisation.elements.elementContainers.JavaFile;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestParse {


    final static String DIR_PREFIX = "src/test/java/normalisation/";

    /**
     * Tests to see if an interface is correctly parsed
     */
    @Test
    public void testParseInterface() throws Exception {
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestInterface.java"));
        assertEquals(1, base.getClasses().size());
        assertEquals(3, base.getClasses().get(0).body.get(0));

    }

    /**
     * Tests parsing an empty file does not cause issues
     */
    @Test
    public void testParseEmptyJavaFile() throws Exception {

        JavaFile base = new JavaFile(new File(DIR_PREFIX + "Empty.java"));
        assertEquals(0, base.getClasses().size());

    }


    /**
     * Tests the parsing of a java file containing two classes
     */
    @Test
    public void testParseTwoClasses() throws Exception {
        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TwoClasses.java"));
        assertEquals(2, base.getClasses().size());
        assertEquals(1, base.getClasses().get(0).getMethods().size());
        assertEquals(1, base.getClasses().get(1).getMethods().size());
    }

    @Test
    public void testAllMethodsParsedSuccessfully() throws Exception {

        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        assertEquals(7, base.getClasses().get(0).getMethods().size());

    }

    @Test
    public void testGlobalParsedSuccessfully() throws Exception {

        JavaFile base = new JavaFile(new File(DIR_PREFIX + "TestClass.java"));
        base.getClasses().get(0).getVariables().forEach(x-> System.out.println(x + "\n"));
        assertEquals(4, base.getClasses().get(0).getVariables().size());

    }

    @Test
    public void testParseVariableDeclaration() throws Exception {

        assertEquals(false, ElementContainer.isVariableDeclaration("Integer> l"));
        assertEquals(false, ElementContainer.isVariableDeclaration("HashMap<Long,Double> map = new HashMap<>();"));
        assertEquals(false, ElementContainer.isVariableDeclaration("return null ;"));

    }

    @Test
    public void testParseVariableObject() throws Exception {

        Variable v = new Variable("   HashMap<Long,Double> map = new HashMap<>();   ");
        System.out.println(v.toString());

    }


    @Test
    public void testZeroMethodsParsedSuccessfully() throws Exception {

        JavaFile base = new JavaFile(new File(DIR_PREFIX + "NoMethods.java"));
        assertEquals(0, base.getClasses().get(0).getMethods().size());


    }

}
