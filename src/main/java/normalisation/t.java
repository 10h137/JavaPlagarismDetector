package normalisation;

import normalisation.elements.elementContainers.JavaFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class t {

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {


        JavaFile c = new JavaFile(new File("src/main/java/normalisation/t.java"));
        c.sortImports();
        c.replaceInterfaces();
        c.sortElements();
        c.normaliseMethodNames();
        c.removeComments();

        System.out.println(c.body.get(1).toString());
        System.out.println(c.toString());

        File a = new File("src/main/java/normalisation/TestClass.java");
        Pattern pattern = Pattern.compile("[A-z]+\\s*<.*>");
        Matcher matcher = pattern.matcher("8 HashMap<String, String> l = new HashMap ( ) ;\n");
        if (matcher.find()) {
            System.out.println("ojounu");
        }

        c.toString().matches(".+\\(.*\\).*\\{\\s*");

        // doesnt like escape quotes
        String s = "(\"{\", \"\");";


    }

    public int dscdscd(){
        return 0;
    }
}
