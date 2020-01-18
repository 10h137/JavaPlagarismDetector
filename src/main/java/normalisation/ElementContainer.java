package normalisation;

import normalisation.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static normalisation.MapFile.replacement_map;

public abstract class ElementContainer {

    protected Comment comment = new Comment("");
    protected List<JavaElement> body = new ArrayList<>();

    protected String declaration = "";
    protected String name = "";
    protected ProtectionLevel protection_level = ProtectionLevel.PROTECTED;

    public ProtectionLevel getProtection_level() {
        return protection_level;
    }

    public void removeComments() {
        comment = null;
        for (int i = 0; i < body.size(); i++) {
            JavaElement element = body.get(i);
            if (element instanceof Comment) body.remove(i);
            else if (element instanceof ElementContainer) ((ElementContainer) element).removeComments();
        }
    }


    public void replaceInterfaces() {
        List<Text> text_elements = body.stream()
                .filter(x -> Arrays.asList(x.getClass().getInterfaces()).contains(Text.class))
                .map(Text.class::cast)
                .collect(Collectors.toList());

        body.stream().filter(x -> x instanceof ElementContainer)
                .map(ElementContainer.class::cast)
                .forEach(ElementContainer::replaceInterfaces);

        for (Text text_element : text_elements) {
            String text = text_element.toString();
            for (String interface_key : replacement_map.keySet()) {
                List<String> implementations = replacement_map.get(interface_key);
                for (String implementation : implementations) {
                    if (text.contains(implementation)) {
                        text_element.setText(text.replaceAll(implementation, interface_key));
                        text = text_element.toString();
                    }
                }
            }
        }
    }


    public void normaliseMethodNames() {
        List<ElementContainer> methods = body.stream()
                .filter(e -> e instanceof ElementContainer)
                .map(ElementContainer.class::cast)
                .collect(Collectors.toList());

        methods.forEach(ElementContainer::normaliseMethodNames);

        for (int i = 0; i < methods.size(); i++) {
            String new_name = this.name + "method" + i;
            ElementContainer current_method = methods.get(i);
            String old_name = current_method.getName();

            current_method.setName(new_name);
            for (ElementContainer method : methods) {
                // TODO add ( to replace string after normalising
                method.replaceText(old_name + " \\(", new_name + " \\(");
            }
        }
    }

    public void combineComments() {

        List<JavaElement> new_elements = new ArrayList<>();
        Comment combined_comment = null;
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

    public void setComment(Comment class_comments) {
        this.comment = class_comments;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (comment != null) sb.append(comment.toString());
        sb.append(declaration).append("\n");

        int last_num = 0;
        // append body elements
        for (int i = 0; i < body.size(); i++) {
            JavaElement javaElement = body.get(i);
            String s = javaElement.toString();

            //TODO fix
            if (i == body.size() - 1) {
                String[] a = s.split("\n");
                last_num = Integer.parseInt(a[a.length - 1].split(" ")[0]) + 1;
            }
            sb.append(s + "\n");
        }

        if (!(this instanceof JavaFile)) sb.append(last_num + " }");
        return sb.toString();
    }

    public List<Variable> getVariables() {

        List<Variable> variables = new ArrayList<>();
        for (JavaElement javaElement : body) {
            if (javaElement instanceof Variable) variables.add((Variable) javaElement);
            if (javaElement instanceof ElementContainer)
                variables.addAll(((ElementContainer) javaElement).getVariables());
        }
        return variables;
    }

    public void sortElements() {
        // TODO shouldnt be aware of implementations
        if (this instanceof Method) return;
        List<JavaElement> sorted_containers = body.stream()
                .filter(e -> e instanceof ElementContainer)
                .map(ElementContainer.class::cast)
                .peek(ElementContainer::sortElements)
                .sorted(Comparator
                        .comparingInt(ElementContainer::length)
                        .thenComparing(ElementContainer::getProtection_level)
                        .thenComparing(ElementContainer::getName))
                .map(JavaElement.class::cast)
                .collect(Collectors.toList());

        List<JavaElement> sorted_elements = body.stream()
                .filter(e -> !(e instanceof ElementContainer))
                .sorted(Comparator.comparingInt(JavaElement::length))
                .collect(Collectors.toList());

        sorted_elements.addAll(sorted_containers);
        body = sorted_elements;

    }

    public int length() {
        return body.stream()
                .map(JavaElement::length)
                .reduce(Integer::sum)
                .orElse(0) + comment.length();
    }

    public void setName(String name) {
        this.declaration = this.declaration.replace(this.name, name);
        this.name = name;
    }


    public void replaceText(String target, String replacement) {
        for (JavaElement javaElement : body) {
            if (javaElement instanceof ElementContainer)
                ((ElementContainer) javaElement).replaceText(target, replacement);
            else if (javaElement instanceof Text) {
                String old = javaElement.toString();
                ((Text) javaElement).setText(old.replaceAll(target, replacement));
            }
        }
    }


    public String getName() {
        return name;
    }

}
