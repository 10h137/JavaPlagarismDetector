package normalisation;

import normalisation.util.*;

import java.util.ArrayList;
import java.util.List;

import static normalisation.Util.isVariableDeclaration;

//https://www.trivago.co.uk?cpt2=8242182%2F100&sharedcid=8242182&tab=info
//https://www.trivago.co.uk?cpt2=5522470%2F100&sharedcid=5522470&tab=gallery

public class Method extends ElementContainer implements JavaElement {


    String return_type = "";
    boolean is_static = false;
    List<Variable> args = new ArrayList<>();


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
            } else {
                if (isVariableDeclaration(line)) {
                    // change to detect between global and local
                    body.add(new Variable(line));
                } else {
                    body.add(new CodeLine(line));
                }
            }
        }
        // removes
        body.remove(0);

        combineComments();

    }


    /**
     * Parses the method declaration line and initialises variables - method_name, protection_level, return_type, args
     *
     * @param method_signature
     */
    void parse_local_variable(String method_signature) {
        List<Variable> result = new ArrayList<>();
        method_signature.matches("^[0-9]*\\s*(final)?\\s*[A-z]+\\s+[A-z]+\\s*(=.*)?;.*\\s*");
    }

    public void parseDeclaration(String declaration) {
        declaration = declaration.replace(")", "");
        String[] s = declaration.split("\\(");
        String[] dec = s[0].split("\\s+");
        String[] args = s[1].split("\\s+");

        switch (dec.length) {
            case 3:
                protection_level = ProtectionLevel.PACKAGE_PRIVATE;
                is_static = false;

                break;
            case 4:
                if (dec[1].equals("static")) {
                    protection_level = ProtectionLevel.PACKAGE_PRIVATE;
                    is_static = true;

                } else {
                    protection_level = ProtectionLevel.valueOf(dec[1].toUpperCase());
                    is_static = false;
                }
                break;
            case 5:
                protection_level = ProtectionLevel.valueOf(dec[1].toUpperCase());
                is_static = true;
            default:

        }

        name = dec[dec.length - 1];
        return_type = dec[dec.length - 2];


    }


}