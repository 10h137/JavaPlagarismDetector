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
        for (JavaElement javaElement : body) {
            if (javaElement instanceof ElementContainer) {
                for (String interface_key : replacement_map.keySet()) {
                    List<String> implementations = replacement_map.get(interface_key);
                    for (String implementation : implementations) {
                        ((ElementContainer) javaElement).replaceText(implementation, interface_key);
                        ((ElementContainer) javaElement).replaceInterfaces();
                    }
                }
            }
        }

    }

    public List<ElementContainer> getContainers() {
        return body.stream()
                .filter(e -> e instanceof ElementContainer)
                .map(ElementContainer.class::cast)
                .collect(Collectors.toList());
    }


    public void normaliseMethodNames() {
        List<ElementContainer> containers = getContainers();

        for (int i = 0; i < containers.size(); i++) {
            ElementContainer current_method = containers.get(i);
            String new_name = this.name + current_method.getClass().getSimpleName() + i;
            String old_name = current_method.getName();
            current_method.setName(new_name);
            containers.forEach(method -> method.replaceText(old_name, new_name));
        }

        containers.forEach(ElementContainer::normaliseMethodNames);
    }


    public void combineComments() {

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
            else if (javaElement instanceof ElementContainer)
                variables.addAll(((ElementContainer) javaElement).getVariables());
        }
        return variables;
    }

    public void sortElements() {
        if (this instanceof Method) return;
        List<JavaElement> sorted_containers = getContainers().stream()
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

    public void replaceText(String target, String replacement) {
        for (JavaElement javaElement : body) {
            if (javaElement instanceof ElementContainer)
                ((ElementContainer) javaElement).replaceText(target, replacement);
            else if (Arrays.asList(javaElement.getClass().getInterfaces()).contains(Text.class)) {
                String old = javaElement.toString();
                ((Text) javaElement).setText(old.replaceAll("\\b" + target + "\\b", replacement));
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.declaration = this.declaration.replace(this.name, name);
        this.name = name;
    }

}
