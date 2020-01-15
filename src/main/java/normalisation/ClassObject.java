package normalisation;

import normalisation.util.GlobalVariable;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ClassObject implements JavaElement {

    List<JavaElement> class_elements;
    List<GlobalVariable> global_variables;
    String class_name;
    ProtectionLevel protection_level;


    public int size() {
        return class_elements.stream().map(JavaElement::size).reduce(Integer::sum).orElse(0);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // append class declaration
        sb.append(protection_level.toString().toLowerCase() + " " + class_name + " {\n");
        // append global variables
        global_variables.stream().map(GlobalVariable::toString).forEach(sb::append);
        // append methods
        class_elements.stream().map(JavaElement::toString).forEach(sb::append);

        sb.append("\n}");

        return sb.toString();
    }


    List<JavaElement> parseClass(List<String> lines) {

        Stack<Character> brackets = new Stack<>();
        List<JavaElement> class_elements = new ArrayList<>();

        boolean searching = false;

        boolean in_method = false;
        Pair<Integer, Integer> current_method = new Pair<>(0, 0);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.matches(".+\\(.*\\)\\s*\\{")) {
                in_method = true;
                current_method = current_method.setAt0(i);
            }

            if (in_method) {
                char[] chars = line.toCharArray();
                for (char aChar : chars) {
                    if (aChar == '{' || aChar == '}') {
                        if (aChar != brackets.peek()) {
                            brackets.pop();
                            if (brackets.empty()) {
                                current_method = current_method.setAt1(i);

                                Method method = new Method(lines.subList(current_method.getValue0(), current_method.getValue1()));
                                class_elements.add(method);

                                current_method = new Pair<>(0, 0);
                                in_method = false;

                                // misses case where comment on same line as closing bracket     } //
                                // can be fixed with preprocessing, add \n after evey closing bracket
                                break;
                            }

                        } else {
                            brackets.push(aChar);
                        }
                    }
                }

            }


            if (!in_method) {
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
                    class_elements.add(new Comment(line));
                } else if (line.matches(multi_open)) {
                    if (!line.matches(multi_close_single)) searching = true;
                    class_elements.add(new Comment(line));
                } else if (line.matches(regular_comment)) {
                    class_elements.add(new Comment(line));
                } else if (isVariableDeclaration(line)) {
                    global_variables.add(new GlobalVariable(line));
                }

            }
        }


        return class_elements;


    }

    public boolean isVariableDeclaration(String line) {
        return false;
    }


}


