package normalisation.elements;

import normalisation.util.Text;

public class CodeLine implements JavaElement, Text {

    private String code = "";
    private String original_string;

    public CodeLine(String code) {
        this.code = code;
        this.original_string = code;
    }

    @Override
    public int length() {
        return code.length();
    }

    @Override
    public String getText() {
        return toString();
    }

    @Override
    public void setText(String text) {
        code = text;
    }

    public String toString() {
        return code;
    }

    @Override
    public String originalString() {
        return original_string;
    }
}
