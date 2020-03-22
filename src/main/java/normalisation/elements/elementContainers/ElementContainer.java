package normalisation.elements.elementContainers;

import normalisation.elements.CodeLine;
import normalisation.elements.Comment;
import normalisation.elements.JavaElement;
import normalisation.elements.Variable;
import normalisation.util.CommentPatterns;
import normalisation.util.ProtectionLevel;
import normalisation.util.Text;
import org.javatuples.Pair;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static normalisation.util.CommentPatterns.*;
import static normalisation.util.MapFile.replacement_map;


/**
 *
 */
public abstract class ElementContainer implements JavaElement {

    public List<JavaElement> body = new ArrayList<>();
    String declaration = "";
    String name = "";
    ProtectionLevel protection_level = ProtectionLevel.PROTECTED;
    private Comment comment = new Comment("");
    String original_string;


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
        return line.matches("^[0-9]*\\s*((public\\s+)|(private\\s+)|(protected\\s+)|)(static\\s+)?\\s*(final)?\\s*([A-z0-9]|[|]|<|>)+\\s+[A-z]+\\s*((=.+)|;).*\\s*");
    }

    public int getElementCount() {
        return body.size();
    }

    private ProtectionLevel getProtection_level() {
        return protection_level;
    }

    /**
     * Recursively removes all comment objects from this container and all sub-containers
     */
    public void removeComments() {
        comment.setText("");
        for (int i = 0; i < body.size(); i++) {
            JavaElement element = body.get(i);
            if (element instanceof Comment) {
                body.remove(i);
                i--;
            } else if (element instanceof ElementContainer) ((ElementContainer) element).removeComments();
        }
    }

    /**
     * Replaces all data java data types and structure implementations with a corresponding interface or keyword
     * e.g HashMap -> Map, long -> Integer, BigInt -> Integer
     */
    public void replaceInterfaces() {
        for (JavaElement javaElement : body) {
            if (javaElement instanceof ElementContainer) {
                ElementContainer container = ((ElementContainer) javaElement);
                for (String interface_key : replacement_map.keySet()) {
                    List<String> implementations = replacement_map.get(interface_key);
                    for (String implementation : implementations) {
                        container.declaration = container.declaration.replaceAll(implementation, interface_key);
                        container.replaceVariableText(implementation, interface_key);
                        container.replaceInterfaces();
                    }
                }
            }
        }

    }

    /**
     * Returns all element containers in the body
     *
     * @return list of element containers
     */
    private List<ElementContainer> getContainers() {
        return body.stream()
                .filter(e -> e instanceof ElementContainer)
                .map(ElementContainer.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Normalises method names
     */
    public void normaliseMethodNames() {
        List<ElementContainer> containers = getContainers();

        for (int i = 0; i < containers.size(); i++) {
            ElementContainer current_method = containers.get(i);
            String new_name = this.name + current_method.getClass().getSimpleName() + i;
            String old_name = current_method.getName();
            current_method.setName(new_name);
            containers.forEach(method -> method.replaceMethodName(old_name, new_name));
        }

        containers.forEach(ElementContainer::normaliseMethodNames);
    }

    /**
     * Merges consecutive comments into single comment objects and merges comments with any element container they immediately precede
     * e.g. comments directly before a method will be merged with the method object, and the method object will have its comment attribute set to this comment
     */
    void combineComments() {

        List<JavaElement> new_elements = new ArrayList<>();
        Comment combined_comment;
        List<Comment> comments = new ArrayList<>();
        for (JavaElement javaElement : body) {
            if (javaElement instanceof Comment) {
                comments.add((Comment) javaElement);
            } else {
                if (!comments.isEmpty()) {
                    combined_comment = new Comment(comments);
                    comments.clear();
                    if (javaElement instanceof ElementContainer) {
                        ((ElementContainer) javaElement).setComment(combined_comment);
                    } else {
                        new_elements.add(combined_comment);
                    }
                }
                new_elements.add(javaElement);
            }
        }
        body = new_elements;
    }

    /**
     * Sets the comment attributed to the current container
     *
     * @param container_comments
     */
    private void setComment(Comment container_comments) {
        this.comment = container_comments;
    }

    /**
     * Recursively generates the string representation of all the nested containers and the other elements in the body and combines them
     *
     * @return a string of the current element container e.g a java class's code or a methods code
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (comment != null) sb.append(comment.toString());
        sb.append(declaration).append("\n");

        // append body elements
        for (int i = 0; i < body.size(); i++) {
            JavaElement javaElement = body.get(i);
            String s = javaElement.toString();
            sb.append(s).append("\n");
        }

        if (!(this instanceof JavaFile)) sb.append("}");
        return sb.toString();
    }

    /**
     * Collects all variables in the containers body as well as nested variable objects contained in other element containers
     *
     * @return List of all variables within this container and its sub-containers
     */
    List<Variable> getVariables() {
        List<Variable> variables = new ArrayList<>();
        for (JavaElement javaElement : body) {
            if (javaElement instanceof Variable) variables.add((Variable) javaElement);
            else if (javaElement instanceof ElementContainer)
                variables.addAll(((ElementContainer) javaElement).getVariables());
        }
        return variables;
    }

    public void normaliseVariables() {

        getContainers().forEach(ElementContainer::normaliseVariables);
        List<Variable> variables = body.stream().filter(x -> x instanceof Variable).map(Variable.class::cast).collect(Collectors.toList());
        if (this instanceof Method) ((Method) this).standardiseArgs();
        for (int i = 0; i < variables.size(); i++) {
            Variable current_var = variables.get(i);
            String new_name = this.name + "Var" + i;
            String old_name = current_var.getName();
            current_var.setName(new_name);
            this.replaceVariableText(old_name, new_name);
            //TODO known issue when method has aame name as variable, method name is renames as if it were var
        }


    }

    /**
     * Sorts all elements in the body, element containers are sorted by length then protection level
     * Comments are sorted by length and placed before the sorted containers
     */
    public void sortElements() {
        List<JavaElement> sorted_containers = getContainers().stream()
                .peek(ElementContainer::sortElements)
                .sorted(Comparator
                        .comparingInt(ElementContainer::getElementCount)
                        .thenComparing(ElementContainer::getProtection_level)
                )
                .map(JavaElement.class::cast)
                .collect(Collectors.toList());

        List<JavaElement> sorted_elements = body.stream()
                .filter(e -> !(e instanceof ElementContainer))
                .sorted(Comparator.comparingInt(JavaElement::length))
                .collect(Collectors.toList());

        sorted_elements.addAll(sorted_containers);
        body = sorted_elements;

    }

    /**
     * Calculates the total length of all elements
     *
     * @return total length
     */
    public int length() {
        return body.stream()
                .map(JavaElement::length)
                .reduce(Integer::sum)
                .orElse(0) + comment.length();
    }

    /**
     * Replaces variable related text in all elements and sub elements
     *
     * @param target      - target string pattern
     * @param replacement - replacement string
     */
    void replaceVariableText(String target, String replacement) {

        String[] s = declaration.split("\\(", 2);
        if (s.length > 1) {
            s[1] = s[1].replaceAll("\\b" + target + "\\b", replacement);
            declaration = s[0] + "(" + s[1];
        }

        for (JavaElement javaElement : body) {
            if (javaElement instanceof ElementContainer)
                ((ElementContainer) javaElement).replaceVariableText(target, replacement);
            else if (asList(javaElement.getClass().getInterfaces()).contains(Text.class)) {
                String old = ((Text) javaElement).getText();
                ((Text) javaElement).setText(old.replaceAll("\\b" + target + "\\b", replacement));
            }
        }
    }

    //TODO fix for escaped quotations in string

    /**
     * Replaces method name in all elements and sub elements
     *
     * @param target      - target string pattern
     * @param replacement - replacement string
     */
    void replaceMethodName(String target, String replacement) {
        declaration = declaration.replaceFirst("\\b" + target + "\\b\\s*\\(", replacement);
        for (JavaElement javaElement : body) {
            if (javaElement instanceof ElementContainer)
                ((ElementContainer) javaElement).replaceMethodName(target, replacement);
            else if (asList(javaElement.getClass().getInterfaces()).contains(Text.class)) {
                String old = ((Text) javaElement).getText();
                ((Text) javaElement).setText(old.replaceAll("\\b" + target + "\\b\\s*\\(", replacement));
            }
        }
    }

    /**
     * Gets the name of the container e.g. class name
     *
     * @return Container name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the container
     *
     * @param name new name
     */
    private void setName(String name) {
        this.declaration = this.declaration.replaceFirst(this.name, name);
        this.name = name;
    }

    public String originalString(){
        return original_string;
    }


}
