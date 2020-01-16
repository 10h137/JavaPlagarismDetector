package normalisation;

import normalisation.util.*;

import java.util.ArrayList;
import java.util.List;

import static normalisation.Util.isVariableDeclaration;

public class Method implements JavaElement {

    String name = "";
    String return_type = "";
    ProtectionLevel protection_level = ProtectionLevel.PROTECTED;
    List<Variable> args = new ArrayList<>();
    List<Variable> local_variables = new ArrayList<>();
    List<JavaElement> method_body = new ArrayList<>();

    @Override

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // appends method signature
        sb.append(protection_level.getString()).append(" ")
                .append(return_type)
                .append(name)
                .append(" (");
        for (int i = 0; i < args.size(); i++) {
            sb.append(args.get(i).toString());
            if (i != args.size() - 1) sb.append(", ");
        }
        sb.append(") {\n");

        // appends method body
        method_body.stream().map(JavaElement::toString).forEach(s -> sb.append(s + "\n"));
        sb.append("\n}");

        return sb.toString();
    }

    @Override
    public int size() {
        return method_body.stream().map(JavaElement::size).reduce(Integer::sum).orElse(0);
    }


    public Method(List<String> lines) {
        boolean searching = false;
        for (String line : lines) {
            // check for comments
            //multi-line comments open -> /*
            String multi_open = "^[0-9]*(\\s*/\\*.*)";
            // multi-line comments close -> */
            String multi_close = "^[0-9]*(\\s*\\*/.*)";
            // regular comment -> //
            String regular_comment = "^[0-9]*(\\s*//.*)";
            // check if it is instance of single line 'multi-line comment' e.g.
            /* comment */
            String multi_close_single = ".*\\*/$";

            if (searching) {
                if (line.matches(multi_close)) searching = false;
                method_body.add(new Comment(line));
            } else if (line.matches(multi_open)) {
                if (!line.matches(multi_close_single)) searching = true;
                method_body.add(new Comment(line));
            } else if (line.matches(regular_comment)) {
                method_body.add(new Comment(line));
            } else {
                if (isVariableDeclaration(line)) {
                    // change to detect between global and local
                    local_variables.add(new Variable(line));
                }
                method_body.add(new CodeLine(line));
            }
        }

    }


    List<Variable> parseArgs(String method_signature){
        List<Variable> result = new ArrayList<>();

        return result;
    }


}