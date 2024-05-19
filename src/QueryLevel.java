public enum QueryLevel {
    DATABASE("database"),
    TABLE("table");

    private final String typeName;

    private QueryLevel(String typeName){
        this.typeName = typeName;
    }

    public String getType(){
        return typeName;
    }
    public static QueryLevel valueOfType(String name) {
        for (QueryLevel type : values()) {
            if (type.getType().equals(name.toLowerCase())) {
                return type;
            }
        }
        return null;
    }
}
