package normalisation;

import normalisation.util.JavaElement;
import normalisation.util.Variable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static normalisation.Util.getElements;

public class ClassObject extends ElementContainer implements JavaElement {


    public ClassObject(List<String> lines) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        parseDeclaration(lines.get(0));
        body = getElements(".+\\(.*\\).*\\{\\s*", lines, Method.class);
        combineComments();
    }

    /**
     * Parses the class declaration line and initialises variables - class_name & protection_level
     *
     * @param declaration_line
     */
    private void parseDeclaration(String declaration_line) {
        this.declaration = declaration_line;
        String[] split_declaration = declaration_line.split("s+");
        for (int i = 0; i < Arrays.asList(split_declaration).size(); i++) {
            if (split_declaration[i].trim().equals("class")) {
                name = split_declaration[i + 1].trim();
            }
        }

    }



}


