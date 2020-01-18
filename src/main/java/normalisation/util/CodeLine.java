package normalisation.util;

import normalisation.Text;

public class CodeLine implements JavaElement, Text {

    private String code = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CodeLine(String code) {
        this.code = code;
    }

    @Override
    public int length() {
        return code.length();
    }

    public String toString() {
        return code;
    }

    @Override
    public void setText(String text) {
        code = text;
    }
}
