package normalisation;

import normalisation.util.JavaElement;
import normalisation.util.Result;
import org.checkerframework.checker.regex.qual.Regex;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static normalisation.JavaFile.preProcess;
import static normalisation.Util.getElements;

public class t {

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {


        JavaFile c = new JavaFile(new File("src/main/java/normalisation/Method.java"));
        System.out.println(c.toString());
        File a = new File("src/main/java/normalisation/Method.java");
        List<String> lines = preProcess(Files.readAllLines(Paths.get(a.getAbsolutePath())));
                Result res = getElements(".*class\\s+[a-zA-Z]+\\s*\\{", lines, Method.class);
        List<JavaElement> p =res.elements;
        System.out.println(p.size());
        "hh".matches(".*class\\s+.*\\{\\s*");

        List<String> k = Arrays.asList(new String[]{"1","2","3","4","5"});
        k = k.subList(0,2);
        k.forEach(System.out::print);



    }
}
