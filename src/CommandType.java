public enum CommandType {
    CREATE("create"),
    DROP("drop"),
    INSERT("insert"),
    TRUNCATE("truncate"),
    SHOW("show"),
    SELECT("select"),
    USE("use"),
    START_TRANSACTION("start transaction"),
    COMMIT("commit"),
    ROLLBACK("rollback"),
    INVALID("invalid");

    private final String typeName;

    private CommandType(String typeName){
        this.typeName = typeName;
    }

    public String getType(){
        return typeName;
    }
    public static CommandType valueOfType(String name) {
        for (CommandType type : values()) {
            if (type.getType().equals(name.toLowerCase())) {
                return type;
            }
        }
        return null;
    }
}

