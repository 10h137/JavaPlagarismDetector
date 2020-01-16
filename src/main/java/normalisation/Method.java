package normalisation;

import normalisation.util.CodeLine;
import normalisation.util.Comment;
import normalisation.util.JavaElement;
import normalisation.util.Variable;

import java.util.ArrayList;
import java.util.List;

import static normalisation.Util.isVariableDeclaration;

//https://www.trivago.co.uk?cpt2=8242182%2F100&sharedcid=8242182&tab=info
//https://www.trivago.co.uk?cpt2=5522470%2F100&sharedcid=5522470&tab=gallery

public class Method extends ElementContainer implements JavaElement {


    String return_type = "";
    List<Variable> args = new ArrayList<>();


    public Method(List<String> lines) {
        if(lines.isEmpty()) return;
        declaration = lines.get(0);
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
                }else{
                    body.add(new CodeLine(line));
                }
            }
        }
        // removes
        body.remove(0);

        combineComments();

    }


    public void replaceText(String target, String replacement){

        for (JavaElement javaElement : body) {
            String old = javaElement.toString();
            //javaElement.setString(old.replaceAll(target, replacement));
        }


    }


    /**
     * Parses the method declaration line and initialises variables - method_name, protection_level, return_type, args
     *
     * @param method_signature
     */
    void parse_declaration(String method_signature) {
        List<Variable> result = new ArrayList<>();

    }


}