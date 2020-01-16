package normalisation;

import normalisation.util.*;
import org.javatuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Util {

    public static Result getElements(String pattern, List<String> lines, Class<? extends JavaElement> element_class) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Stack<Character> brackets = new Stack<>();
        List<JavaElement> elements = new ArrayList<>();
        List<Variable> variables = new ArrayList<>();

        boolean in_method = false;
        boolean searching = false;


        Pair<Integer, Integer> current_method = new Pair<>(0, 0);
        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);

            if (line.matches(pattern)) {
                in_method = true;
                //brackets.push('{');
                current_method = current_method.setAt0(i);
                System.out.println("bbvbgjvjg");

            }

            if (in_method) {
                char[] chars = line.toCharArray();
                for (char aChar : chars) {
                    if (aChar == '{' || aChar == '}') {
                        // checks if bracket in string
                        if(checkInString(line, aChar)) continue;

                        if (!brackets.isEmpty() && aChar != brackets.peek()) {
                            brackets.pop();
                            if (brackets.empty()) {
                                current_method = current_method.setAt1(i+1);
                                JavaElement element = element_class
                                        .getConstructor(List.class)
                                        .newInstance(lines.subList(current_method.getValue0(), current_method.getValue1()));
                                elements.add(element);

                                System.out.println("hbkhbbhhkbk");
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
                    elements.add(new Comment(line));
                } else if (line.matches(multi_open)) {
                    if (!line.matches(multi_close_single)) searching = true;
                    elements.add(new Comment(line));
                } else if (line.matches(regular_comment)) {
                    elements.add(new Comment(line));
                } else if (isVariableDeclaration(line)) {
                    // change to detect between global and local
                    variables.add(new GlobalVariable(line));
                }

            }

        }
        return new Result(elements, variables);

    }

    static boolean checkInString(String line, int index){
        return false;
    }


    public static boolean isVariableDeclaration(String line) {
        return false;
    }

}
