public enum DataTypes {
    VARCHAR("varchar"),
    INT("int");

    private final String typeName;

    private DataTypes(String typeName){
        this.typeName = typeName;
    }

    public String getType(){
        return typeName;
    }
    public static DataTypes valueOfType(String name) {
        for (DataTypes type : values()) {
            if (type.getType().equals(name.toLowerCase())) {
                return type;
            }
        }
        return null;
    }
}
