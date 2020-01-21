package normalisation.elements.elementContainers;

import normalisation.elements.JavaElement;
import normalisation.util.ProtectionLevel;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static normalisation.util.Util.getElements;

public class ClassObject extends ElementContainer implements JavaElement {

    private boolean is_abstract = false;
    private boolean is_interface = false;

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
    public void parseDeclaration(String declaration_line) {
        this.declaration = declaration_line;
        String[] s = declaration_line.split("\\s+");
        int class_word_index = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i].equals("class") || s[i].equals("interface")) {
                is_interface =  s[i].equals("interface");
                class_word_index = i;
                break;
            }
        }

        name = s[class_word_index + 1];

        List<String> protection_strings = Arrays.stream(ProtectionLevel.values())
                .map(ProtectionLevel::getString)
                .collect(Collectors.toList());
        int i = 0;
        try {
            Integer.parseInt(s[0].strip());
            i = 1;
        } catch (Exception ignored) {}
        for (; i < class_word_index; i++) {
            if (!is_abstract) is_abstract = s[i].equals("abstract");
            if (protection_strings.contains(s[i]) && !s[i].isBlank()) {
                protection_level = ProtectionLevel.valueOf(s[i].toUpperCase());
            }
        }

        if (protection_level == null) protection_level = ProtectionLevel.PACKAGE_PRIVATE;

    }


public List<Method> getMethods(){
        return body.stream().filter(x -> x instanceof Method).map(Method.class::cast).collect(Collectors.toList());
}




}


