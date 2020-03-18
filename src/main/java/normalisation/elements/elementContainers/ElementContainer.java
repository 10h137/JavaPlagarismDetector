package normalisation.elements.elementContainers;

import normalisation.elements.Comment;
import normalisation.elements.JavaElement;
import normalisation.elements.Variable;
import normalisation.util.ProtectionLevel;
import normalisation.util.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static normalisation.util.MapFile.replacement_map;

/**
 *
 */
public abstract class ElementContainer {

    public List<JavaElement> body = new ArrayList<>();
    String declaration = "";
    String name = "";
    ProtectionLevel protection_level = ProtectionLevel.PROTECTED;
    private Comment comment = new Comment("");

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

}
