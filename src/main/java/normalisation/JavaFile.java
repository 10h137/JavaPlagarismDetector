package normalisation;

import java.io.File;
import java.util.List;

public class JavaFile {

    String file_name;
    List<JavaElement> java_elements;
    List<String> imports;

    public String toString(){
        StringBuilder sb = new StringBuilder();
        imports.forEach(sb::append);
        java_elements.stream().map(JavaElement::toString).forEach(sb::append);
        return sb.toString();
    }

    public JavaFile(File file){



    }



}
