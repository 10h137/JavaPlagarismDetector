package normalisation.util;

public enum CommentPatterns {
    MULTI_OPEN,
    MULTI_CLOSE,
    REGULAR_COMMENT,
    MULTI_CLOSE_SINGLE_LINE;

    public String getValue() {
        switch (this) {
            //multi-line comment open -> /*
            case MULTI_OPEN:
                return "^[0-9]*(\\s*/\\*.*)";
            // multi-line comment close -> */
            case MULTI_CLOSE:
                return "^[0-9]*(\\s*\\*/.*)";
            // regular comment -> //
            case REGULAR_COMMENT:
                return "^[0-9]*(\\s*//.*)";
            // check if it is instance of single line 'multi-line comment' e.g.
            case MULTI_CLOSE_SINGLE_LINE:
                return "^[0-9]*.*\\*/$";
            default:
                return "";
        }
    }
}


