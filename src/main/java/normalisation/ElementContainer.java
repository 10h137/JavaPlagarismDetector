package normalisation;

import normalisation.util.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        Map<String, Set<String>> map = new HashMap<>();
        Set<String> k = new HashSet<>();
        k.add("HashMap");
        map.put("Map", k);

        List<Text> text_elements = body.stream()
                .filter(x -> Arrays.asList(x.getClass().getInterfaces()).contains(Text.class))
                .map(Text.class::cast)
                .collect(Collectors.toList());

        body.stream().filter(x -> x instanceof ElementContainer)
                .map(ElementContainer.class::cast)
                .forEach(ElementContainer::replaceInterfaces);

        for (Text text_element : text_elements) {
            int old_index = 0;
            while (true) {
                String text = text_element.toString();
                //TODO fix regex for nested <<>>
                Pattern pattern = Pattern.compile("[A-z]+\\s*<.*>");
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    int start = matcher.start();
                    if (start <= old_index) {
                        old_index++;
                        continue;
                    }
                    old_index = start;
                    String match = text.substring(start, matcher.end());
                    char[] a = match.toCharArray();
                    int end_index = 0;
                    for (int i = 0; i < a.length; i++) {
                        char c = a[i];
                        System.out.println(c + " " + i);
                        if (c == '<') {
                            end_index = i + start;
                            break;
                        }

                    }
                    match = text.substring(start, end_index);
                    String prefix = text.substring(0, start);
                    String remainder = text.substring(end_index);
                    for (String s : map.keySet()) {
                        if (map.get(s).contains(match)) {
                            match = s;
                            StringBuilder sb = new StringBuilder(prefix);
                            sb.append(match);
                            sb.append(remainder);
                            text_element.setText(sb.toString());
                            break;
                        }
                    }
                    break;

                } else {
                    break;
                }

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
        return body.stream().map(JavaElement::length).reduce(Integer::sum).orElse(0) + comment.length();
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
