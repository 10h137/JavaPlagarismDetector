package normalisation.util;

public enum ProtectionLevel {

    PUBLIC,
    PRIVATE,
    PROTECTED,
    PACKAGE_PRIVATE;


    public String getString() {
        switch (this) {
            case PUBLIC:
                return "public";
            case PRIVATE:
                return "private";
            case PROTECTED:
                return "protected";
            case PACKAGE_PRIVATE:
                return "";
            default:
                return null;
        }
    }

}
