package normalisation.util;

import java.util.List;

public class Comment implements JavaElement, Text {

    private String comment = "";

    public Comment(String comment){
        this.comment = comment;
    }

    public Comment(List<Comment> comments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            Comment comment1 = comments.get(i);
            String s = comment1.toString();
            sb.append(s);
            if(i!= comments.size() -1) sb.append("/n");
        }
        comment = sb.toString();
    }

    @Override
    public int length() {
        return comment.length();
    }

    public String toString(){
        return comment;
    }

    @Override
    public void setText(String text) {
        comment = text;
    }
}
