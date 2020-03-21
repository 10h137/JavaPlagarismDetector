package normalisation.elements.elementContainers;

import normalisation.elements.JavaElement;
import normalisation.elements.Variable;
import normalisation.util.ProtectionLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



/**
 *
 */
public class Method extends ElementContainer implements JavaElement {

    //TODO sort arguments alphabetically and length on data type


    private final List<Variable> args = new ArrayList<>();
    private boolean is_static = false;


    /**
     * @param lines
     */
    public Method(List<String> lines) {
        if (lines.isEmpty()) return;
        declaration = lines.get(0);
        parseDeclaration(declaration);
        //lines.remove(0);
        boolean in_comment = false;
        for (String line : lines) {
            in_comment = getComments(body, in_comment, line, true);
        }
        // removes
        body.remove(0);
        combineComments();
        original_string = this.toString();
    }


    public void standardiseArgs() {
        for (int i = 0; i < args.size(); i++) {
            Variable current_arg = args.get(i);
            String new_name = this.name + "Arg" + i;
            String old_name = current_arg.getName();
            current_arg.setName(new_name);
            this.replaceVariableText(old_name, new_name);
        }

    }

    /**
     * Parses the method declaration, setting the methods instance variables
     *
     * @param declaration
     */
    private void parseDeclaration(String declaration) {
        declaration = declaration.replace("{", "");

        String[] s = declaration.split("\\(");
        int split_index = declaration.indexOf("(");
        String start = declaration.substring(0, split_index - 1);
        String end = declaration.substring(split_index + 1, declaration.lastIndexOf(')'));
        end = end.replace(")", "");

        String[] dec = start.split("\\s+");
        String[] args = end.split("\\s*,\\s*");

        name = dec[dec.length - 1];
        String return_type = dec[dec.length - 2];

        List<String> protection_strings = Arrays.stream(ProtectionLevel.values())
                .map(ProtectionLevel::getString)
                .collect(Collectors.toList());
        int i = 0;
        try {
            Integer.parseInt(dec[0].strip());
            i = 1;
        } catch (Exception ignored) {
        }
        for (; i < dec.length - 2; i++) {
            if (!is_static) is_static = dec[i].equals("static");
            if (protection_strings.contains(dec[i]) && !dec[i].isBlank()) {
                protection_level = ProtectionLevel.valueOf(dec[i].toUpperCase());
            }
        }

        if (protection_level == null) protection_level = ProtectionLevel.PACKAGE_PRIVATE;
        this.args.clear();
        Arrays.stream(args)
                .filter(arg -> !arg.isBlank())
                .forEach(arg -> this.args.add(new Variable(arg)));
    }


    @Override
    public List<Variable> getVariables() {
        List<Variable> variables = super.getVariables();
        variables.addAll(args);
        return variables;
    }

    //TODO sort args in declaration
    @Override
    public void sortElements() {

    }

}