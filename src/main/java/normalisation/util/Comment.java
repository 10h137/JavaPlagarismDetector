package normalisation.util;

import java.util.List;

public class Comment implements JavaElement {

    private String comment;

    public Comment(String comment){
        this.comment = comment;
    }

    public Comment(List<Comment> comments) {
        StringBuilder sb = new StringBuilder();
        comments.stream().map(Comment::toString).forEach(s -> sb.append(s + "\n"));
        comment = sb.toString();
    }

    @Override
    public int length() {
        return comment.length();
    }

    public String toString(){
        return comment;
    }
}
