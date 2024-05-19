import java.util.List;

public class QueryParser {

    public ParsedQuery getParsedQuery(String query){
        query = query.toLowerCase();
        CommandType commandType = getCommandType(query);
        QueryLevel queryLevel = getQueryLevel(query);
        return new ParsedQuery(commandType, queryLevel, getEntityName(commandType, queryLevel, query), getFields(query), getFieldsToInsertOrUpdate(query));
    }

    /**
     * Select the type of command for the query
     * @param query query to parse
     * @return the type of command
     */
    private CommandType getCommandType(String query){
        CommandType commandType;
        if(query.startsWith("create database") || query.startsWith("create table")){
            commandType = CommandType.CREATE;
        }
        else if(query.startsWith("drop database") || query.startsWith("drop table")){
            commandType = CommandType.DROP;
        }
        else if(query.startsWith("insert into")){
            commandType = CommandType.INSERT;
        }
        else if(query.startsWith("truncate table")){
            commandType = CommandType.TRUNCATE;
        }
        else if(query.startsWith("show databases") || query.startsWith("show tables")){
            commandType = CommandType.SHOW;
        }
        else if(query.startsWith("select")){
            commandType = CommandType.SELECT;
        }
        else if(query.startsWith("use")){
            commandType = CommandType.USE;
        }
        else if(query.startsWith("start transaction")){
            commandType = CommandType.START_TRANSACTION;
        }
        else if(query.startsWith("commit")){
            commandType = CommandType.COMMIT;
        }
        else if(query.startsWith("rollback")){
            commandType = CommandType.ROLLBACK;
        }

        else{
            commandType = CommandType.INVALID;
        }

        return commandType;
    }

    /**
     * Check if the query is at database level or table level
     * @param query query to parse
     * @return Level of query
     */
    private QueryLevel getQueryLevel(String query){
        QueryLevel queryLevel;
        if(query.contains("database")){
            queryLevel = QueryLevel.DATABASE;
        }
        else{
            queryLevel = QueryLevel.TABLE;
        }
        return queryLevel;
    }

    private String getEntityName(CommandType commandType, QueryLevel queryLevel, String query){
        query = query.replaceAll(commandType.getType(), "").trim();
        String queryArr[] = query.split(" ");
        String name = null;

        name = queryArr[0].replaceAll("[;()]", "");

        return name;
    }

    private List<String> getFields(String query){
        return null;
    }

    private List<String> getFieldsToInsertOrUpdate(String query){
        return null;
    }
}
