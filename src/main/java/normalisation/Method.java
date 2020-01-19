package normalisation;

import normalisation.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static normalisation.Util.isVariableDeclaration;

//https://www.trivago.co.uk?cpt2=8242182%2F100&sharedcid=8242182&tab=info
//https://www.trivago.co.uk?cpt2=5522470%2F100&sharedcid=5522470&tab=gallery

public class Method extends ElementContainer implements JavaElement, Text{

    //TODO sort arguments alphabetically and length on data type


    String return_type = "";
    boolean is_static = false;
    final List<Variable> args = new ArrayList<>();


    public Method(List<String> lines) {
        if (lines.isEmpty()) return;
        declaration = lines.get(0);
        parseDeclaration(declaration);
        //lines.remove(0);
        boolean searching = false;
        for (String line : lines) {
            // check for comment
            //multi-line comment open -> /*
            String multi_open = "^[0-9]*(\\s*/\\*.*)";
            // multi-line comment close -> */
            String multi_close = "^[0-9]*(\\s*\\*/.*)";
            // regular comment -> //
            String regular_comment = "^[0-9]*(\\s*//.*)";
            // check if it is instance of single line 'multi-line comment' e.g.
            /* comment */
            String multi_close_single = ".*\\*/$";

            if (searching) {
                if (line.matches(multi_close)) searching = false;
                body.add(new Comment(line));
            } else if (line.matches(multi_open)) {
                if (!line.matches(multi_close_single)) searching = true;
                body.add(new Comment(line));
            } else if (line.matches(regular_comment)) {
                body.add(new Comment(line));
            } else if (isVariableDeclaration(line)) {
                // change to detect between global and local
                body.add(new Variable(line));
            } else {
                body.add(new CodeLine(line));
            }
        }
        // removes
        body.remove(0);
        combineComments();

    }


    @Override
    public List<Variable> getVariables(){
        List<Variable> variables = super.getVariables();
        variables.addAll(args);
        return variables;
    }
    public void parseDeclaration(String declaration) {
        declaration = declaration.replace(")", "");
        declaration = declaration.replace("{", "");

        String[] s = declaration.split("\\(");
        String[] dec = s[0].split("\\s+");
        String[] args = s[1].split("\\s*,\\s*");

        name = dec[dec.length - 1];
        return_type = dec[dec.length - 2];

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
        Arrays.stream(args)
                .filter(arg -> !arg.isBlank())
                .forEach(arg -> this.args.add(new Variable(arg)));
    }


    @Override
    public void setText(String text) {
        this.declaration = text;
        parseDeclaration(declaration);
    }
}