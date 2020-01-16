package normalisation;

import normalisation.util.JavaElement;
import normalisation.util.ProtectionLevel;
import normalisation.util.Result;
import normalisation.util.Variable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static normalisation.Util.getElements;

public class ClassObject implements JavaElement {

    List<JavaElement> class_elements;
    List<Variable> global_variables;
    String class_name = "";
    ProtectionLevel protection_level = ProtectionLevel.PROTECTED;


    public int size() {
        return class_elements.stream().map(JavaElement::size).reduce(Integer::sum).orElse(0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // append class declaration
        sb.append(protection_level.getString() + " " + class_name + " {\n");
        // append global variables
        global_variables.stream().map(Variable ::toString).forEach(s -> sb.append(s + "\n"));
        // append methods
        class_elements.stream().map(JavaElement::toString).forEach(s -> sb.append(s + "\n"));

        sb.append("\n}");

        return sb.toString();
    }

    public ClassObject(List<String> lines) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Result res = getElements(".+\\(.*\\).*\\{\\s*", lines, Method.class);
        global_variables = res.variables;
        class_elements = res.elements;
    }



}


