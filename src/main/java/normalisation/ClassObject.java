package normalisation;

import normalisation.util.JavaElement;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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


    public void normaliseMethodNames() {
        List<ElementContainer> methods = body.stream()
                .filter(e -> e instanceof ElementContainer)
                .map(ElementContainer.class::cast)
                .collect(Collectors.toList());

        for (int i = 0; i < methods.size(); i++) {
            String new_name = this.name + "method" + i;
            ElementContainer current_method = methods.get(i);
            String old_name = current_method.getName();

            current_method.setName(new_name);
            for (ElementContainer method : methods) {
                // TODO add ( to replace string after normalising
                method.replaceText(old_name + " (", new_name + " (");
            }
        }
    }


}


