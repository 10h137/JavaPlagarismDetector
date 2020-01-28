package normalisation.elements.elementContainers;

import normalisation.elements.JavaElement;
import normalisation.elements.Variable;
import normalisation.util.ProtectionLevel;
import normalisation.util.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static normalisation.util.Util.getComments;


/**
 *
 */
public class Method extends ElementContainer implements JavaElement, Text {

    //TODO sort arguments alphabetically and length on data type


    private boolean is_static = false;
    private final List<Variable> args = new ArrayList<>();

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
        String end = declaration.substring(split_index + 1, findLastIndex(declaration, ')'));
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

    /**
     * @param str
     * @param c
     * @return
     */
    private static int findLastIndex(String str, Character c) {
        // Traverse from right
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == c) return i;
        }
        return -1;
    }

    @Override
    public List<Variable> getVariables() {
        List<Variable> variables = super.getVariables();
        variables.addAll(args);
        return variables;
    }

    @Override
    public String getText() {
        return declaration;
    }

    @Override
    public void setText(String text) {
        this.declaration = text;
        parseDeclaration(declaration);
    }
}