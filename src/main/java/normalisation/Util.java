package normalisation;

import normalisation.util.Comment;
import normalisation.util.JavaElement;
import normalisation.util.Variable;
import org.javatuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Util {

    public static List<JavaElement> getElements(String pattern, List<String> lines, Class<? extends JavaElement> element_class) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Stack<Character> brackets = new Stack<>();
        List<JavaElement> elements = new ArrayList<>();

        boolean in_method = false;
        boolean in_comment = false;


        Pair<Integer, Integer> current_method = new Pair<>(0, 0);
        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);

            if (line.matches(pattern) && !in_method) {
                in_method = true;
                current_method = current_method.setAt0(i);
            }

            if (in_method) {
                char[] chars = line.toCharArray();
                for (char aChar : chars) {
                    if (aChar == '{' || aChar == '}') {
                        // checks if bracket in string
                        if (checkInString(line, aChar)) continue;

                        if (!brackets.isEmpty() && aChar != brackets.peek()) {
                            brackets.pop();
                            if (brackets.empty()) {
                                current_method = current_method.setAt1(i);
                                JavaElement element = element_class
                                        .getDeclaredConstructor(List.class)
                                        .newInstance(lines.subList(current_method.getValue0(), current_method.getValue1()));
                                elements.add(element);

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

                if (in_comment) {
                    if (line.matches(multi_close)) in_comment = false;
                    elements.add(new Comment(line));
                } else if (line.matches(multi_open)) {
                    if (!line.matches(multi_close_single)) in_comment = true;
                    elements.add(new Comment(line));
                } else if (line.matches(regular_comment)) {
                    elements.add(new Comment(line));
                } else if (isVariableDeclaration(line)) {
                    elements.add(new Variable(line));
                }

            }

        }
        return elements;

    }

    static boolean checkInString(String line, int index) {
        return false;
    }


    public static boolean isVariableDeclaration(String line) {
        return line.matches("^[0-9]*\\s*((public\\s+)|(private\\s+)|(protected\\s+)|)(static\\s+)?\\s*(final)?\\s*[A-z]+\\s+[A-z]+\\s*((=.+;)|;).*\\s*");
    }

}
