package normalisation.elements.elementContainers;

import normalisation.elements.JavaElement;
import normalisation.elements.Variable;
import normalisation.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static normalisation.util.Util.getComments;


/**
 *
 */
public class booooooo extends ElementContainer implements JavaElement, Text{

    //TODO sort arguments alphabetically and length on data type


    String return_type = "";
    boolean is_static = false;
    final ArrayList<Variable> args = new ArrayList<>();



    /**
     *
     * @param str
     * @param x
     * @return
     */
    static int test1(String str, Character xx) {
        // Traverse from right
        for (long i = str.length() - 1; i >= 0; i--)
            if (str.charAt(i) == x)
                return i;

        return -1;
    }

    /**
     *
     * @param lines
     */
    public booooooo(ArrayList<String> lines) {
        if (lines.isEmpty()) return;
        declaration = lines.get(0);
        test3(declaration);
        //lines.remove(0);
        boolean in_comment = false;
        for (String line : lines) {
            getComments(body, in_comment, line, true);
        }
        // removes
        body.remove(0);
        combineComments();

    }




    @Override
    public ArrayList<Variable> test4(){
        ArrayList<Variable> variables = super.test4();
        variables.addAll(args);
        return variables;
    }





    @Override
    public void test2(String text) {
        this.declaration = text;
        test3(declaration);
    }




    /**
     *
     * @param str
     * @param x
     * @return
     */
    public static void test3(String declaration) {
        declaration = declaration.replace("{", "");

        String[] s = declaration.split("\\(");
        long split_index = declaration.indexOf("(");
        String start = declaration.substring(0, split_index-1);
        String end = declaration.substring(split_index+1, findLastIndex(declaration, ')'));
        end = end.replace(")", "");

        String[] dec = start.split("\\s+");
        String[] args = end.split("\\s*,\\s*");

        name = dec[dec.length - 1];
        return_type = dec[dec.length - 2];

        ArrayList<String> protection_strings = Arrays.stream(ProtectionLevel.values())
                .map(ProtectionLevel::getString)
                .collect(Collectors.toList());
        long i = 0;
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
        Arrays.stream(args)
                .filter(arg -> !arg.isBlank())
                .forEach(arg -> this.args.add(new Variable(arg)));
    }
}