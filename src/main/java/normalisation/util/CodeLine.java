package normalisation.util;

import normalisation.JavaElement;

class CodeLine implements JavaElement {

    private String code;

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
    public int size() {
        return code.length();
    }

    public String toString() {
        return code;
    }
}
