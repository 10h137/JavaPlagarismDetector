package normalisation;

import normalisation.util.CodeLine;
import normalisation.util.Comment;
import normalisation.util.JavaElement;
import normalisation.util.Variable;
import org.javatuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static normalisation.Util.CommentPatterns.*;

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
                for (int j = 0; j < chars.length; j++) {
                    char aChar = chars[j];
                    if (aChar == '{' || aChar == '}') {
                        // checks if bracket in string
                        if (checkInString(line, j)) continue;

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
                getComments(elements, in_comment, line, false);
            }

        }
        return elements;

    }

    //TODO fix for escaped quotations in string

    public static boolean checkInString(String line, int index) {
        char[] right = line.substring(index).toCharArray();
        int right_count = 0;
        int right_count_2 = 0;
        for (char c : right) {
            if (c == '"') right_count++;
            if (c == '\'') right_count_2++;

        }

        // TODO fix for all comment patterns
        boolean in_comment = Arrays.stream(values())
                .map(CommentPatterns::getValue)
                .anyMatch(line::matches);
        return ((right_count % 2 != 0)) || (right_count_2 % 2 != 0) || in_comment;
    }

    public static void getComments(List<JavaElement> body, boolean in_comment, String line, boolean get_code_lines) {

        if (in_comment) {
            if (line.matches(MULTI_CLOSE.getValue())) in_comment = false;
            body.add(new Comment(line));
        } else if (line.matches(MULTI_OPEN.getValue())) {
            if (!line.matches(MULTI_CLOSE_SINGLE_LINE.getValue())) in_comment = true;
            body.add(new Comment(line));
        } else if (line.matches(REGULAR_COMMENT.getValue())) {
            body.add(new Comment(line));
        } else if (isVariableDeclaration(line)) {
            // change to detect between global and local
            body.add(new Variable(line));
        } else if(get_code_lines){
            body.add(new CodeLine(line));
        }

    }

    //TODO fix to ignore return statements
    public static boolean isVariableDeclaration(String line) {
        return line.matches("^[0-9]*\\s*((public\\s+)|(private\\s+)|(protected\\s+)|)(static\\s+)?\\s*(final)?\\s*([A-z]|<|>)+\\s+[A-z]+\\s*((=.+)|;).*\\s*");
    }


    public enum CommentPatterns {
        MULTI_OPEN,
        MULTI_CLOSE,
        REGULAR_COMMENT,
        MULTI_CLOSE_SINGLE_LINE;

        public String getValue() {
            switch (this) {
                //multi-line comment open -> /*
                case MULTI_OPEN:
                    return "^[0-9]*(\\s*/\\*.*)";
                // multi-line comment close -> */
                case MULTI_CLOSE:
                    return "^[0-9]*(\\s*\\*/.*)";
                // regular comment -> //
                case REGULAR_COMMENT:
                    return "^[0-9]*(\\s*//.*)";
                // check if it is instance of single line 'multi-line comment' e.g.
                case MULTI_CLOSE_SINGLE_LINE:
                    return "^[0-9]*.*\\*/$";
                default:
                    return "";
            }
        }
    }

}
