package normalisation.elements;

import normalisation.util.Text;

import java.util.List;

public class Comment implements JavaElement, Text {

    private String comment = "";
    private String original_string;

    public Comment(String comment) {
        this.comment = comment;
    }

    public Comment(List<Comment> comments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            Comment comment1 = comments.get(i);
            String s = comment1.toString();
            sb.append(s);
            if (i != comments.size() - 1) sb.append("/n");
        }
        comment = sb.toString();
        original_string = this.toString();
    }

    public String toString() {
        return comment;
    }

    @Override
    public String originalString() {
        return original_string;
    }

    @Override
    public int length() {
        return comment.length();
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public void setText(String text) {
        comment = text;
    }
}
