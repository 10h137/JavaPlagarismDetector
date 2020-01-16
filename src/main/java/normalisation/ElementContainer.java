package normalisation;

import normalisation.util.Comment;
import normalisation.util.JavaElement;
import normalisation.util.ProtectionLevel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ElementContainer {

    protected Comment comment = null;
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

    public void combineComments() {

        List<JavaElement> new_elements = new ArrayList<>();
        Comment combined_comment = null;
        List<Comment> comments = new ArrayList<>();
        for (JavaElement javaElement : body) {

            if (javaElement instanceof Comment) {
                comments.add((Comment) javaElement);
            } else if (!comments.isEmpty()) {
                combined_comment = new Comment(comments);
                comments.clear();
                if (javaElement instanceof ElementContainer) {
                    ((ElementContainer) javaElement).setComment(combined_comment);
                } else {
                    new_elements.add(combined_comment);
                }
                new_elements.add(javaElement);
            } else {
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

        // append class method_declaration
        // sb.append(protection_level.getString() + " " + class_name + " {\n");

        if (comment != null) sb.append(comment.toString());
        sb.append(declaration + "\n");

        // append body elements
        body.stream().map(JavaElement::toString).forEach(s -> sb.append(s + "\n"));

        if (!(this instanceof JavaFile)) sb.append("}");

        return sb.toString();
    }

    public void sortElements() {
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
        return body.stream().map(JavaElement::length).reduce(Integer::sum).orElse(0) + comment.length();
    }



    public String getName() {
        return name;
    }

}
