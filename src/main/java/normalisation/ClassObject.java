package normalisation;

import normalisation.util.JavaElement;
import normalisation.util.ProtectionLevel;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

import static normalisation.Util.getElements;

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
    private void parseDeclaration(String declaration_line) {
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

        // TODO handle interface keyword
        switch (class_word_index) {
            case 1:
                protection_level = ProtectionLevel.PACKAGE_PRIVATE;
                is_abstract = false;
                break;
            case 2:
                if (s[1].equals("abstract")) {
                    is_abstract = true;
                    protection_level = ProtectionLevel.PACKAGE_PRIVATE;
                } else {
                    is_abstract = false;
                    protection_level = ProtectionLevel.valueOf(s[1].toUpperCase());
                }
                break;
            case 3:
                protection_level = ProtectionLevel.valueOf(s[1].toUpperCase());
                is_abstract = true;
                break;
            default:
        }

        name = s[class_word_index + 1];


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
                method.replaceText(old_name + " \\(", new_name + " \\(");
            }
        }
    }


}


