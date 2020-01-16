package normalisation.util;

public class Comment implements JavaElement {

    private String comment;

    public Comment(String comment){
        this.comment = comment;
    }

    @Override
    public int size() {
        return comment.length();
    }

    public String toString(){
        return comment;
    }
}
