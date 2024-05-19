import java.util.List;

public class ParsedQuery {
    private CommandType commandType;
    private QueryLevel queryLevel;
    private String entityName;
    private List<String> fields;
    private List<String> fieldsToInsertOrUpdate;

    public ParsedQuery(CommandType commandType, QueryLevel queryLevel, String entityName, List<String> fields, List<String> fieldsToInsertOrUpdate) {
        this.commandType = commandType;
        this.queryLevel = queryLevel;
        this.entityName = entityName;
        this.fields = fields;
        this.fieldsToInsertOrUpdate = fieldsToInsertOrUpdate;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public QueryLevel getQueryLevel() {
        return queryLevel;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<String> getFieldsToInsertOrUpdate() {
        return fieldsToInsertOrUpdate;
    }

    public void setFieldsToInsertOrUpdate(List<String> fieldsToInsertOrUpdate) {
        this.fieldsToInsertOrUpdate = fieldsToInsertOrUpdate;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }
}
