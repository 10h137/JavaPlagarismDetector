package normalisation.util;

import comparison.resultObjects.MethodComparison;
import normalisation.elements.CodeLine;
import normalisation.elements.Comment;
import normalisation.elements.JavaElement;
import normalisation.elements.Variable;
import normalisation.elements.elementContainers.ClassObject;
import normalisation.elements.elementContainers.JavaFile;
import normalisation.elements.elementContainers.Method;
import org.javatuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static normalisation.util.Util.CommentPatterns.*;

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
                                // can be fixed with pre-processing, add \n after evey closing bracket
                                break;
                            }

                        } else {
                            brackets.push(aChar);
                        }
                    }
                }

            }
            if (!in_method) {
                in_comment = getComments(elements, in_comment, line, false);
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

    public static boolean getComments(List<JavaElement> body, boolean in_comment, String line, boolean get_code_lines) {

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
        } else if (get_code_lines) {
            body.add(new CodeLine(line));
        }

        return in_comment;

    }

    //TODO fix to ignore return statements
    private static boolean isVariableDeclaration(String line) {
        return line.matches("^[0-9]*\\s*((public\\s+)|(private\\s+)|(protected\\s+)|)(static\\s+)?\\s*(final)?\\s*([A-z]|<|>)+\\s+[A-z]+\\s*((=.+)|;).*\\s*");
    }

    public static List<MethodComparison> compareMethods(JavaFile file1, JavaFile file2) {
        List<Method> methods1 = file1.getClasses().stream()
                .map(ClassObject::getMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Method> methods2 = file2.getClasses().stream()
                .map(ClassObject::getMethods)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Set<Method> methods_to_be_processed = new HashSet<>(methods1);
        methods_to_be_processed.addAll(methods2);

        List<MethodComparison> comparisons = new ArrayList<>();
        for (int i = 0; i < methods1.size(); i++) {
            for (int j = i; j < methods2.size(); j++) {
                comparisons.add(new MethodComparison(methods1.get(i), methods2.get(j)));
            }
        }

        List<MethodComparison> best_comparisons = new ArrayList<>();
        // TODO check if in descending order
        comparisons.sort(Comparator.comparingInt(MethodComparison::getTotalScore));
        Collections.reverse(comparisons);

        for (MethodComparison comparison : comparisons) {
            System.out.println(comparison.getReport());
        }


        for (MethodComparison comparison : comparisons) {
            if (methods_to_be_processed.contains(comparison.m1) && methods_to_be_processed.contains(comparison.m2)) {
                best_comparisons.add(comparison);
            }
            methods_to_be_processed.remove(comparison.m1);
            methods_to_be_processed.remove(comparison.m2);
        }

        return best_comparisons;

    }


    public enum CommentPatterns {
        MULTI_OPEN,
        MULTI_CLOSE,
        REGULAR_COMMENT,
        MULTI_CLOSE_SINGLE_LINE;

        String getValue() {
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
