import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchRegex {
    private Database database;
    private static String USE_REGEX = "^use\\s+([a-zA-Z0-9_]+);$";
    private static String CREATE_DATABASE_REGEX = "^create\\s+database\\s+([a-zA-Z0-9_]+);$";
    private static String CREATE_TABLE_REGEX = "^create\\s+table\\s+([a-zA-Z0-9_]+)\\s+\\((\\s*[a-zA-Z0-9_]+\\s+[a-zA-Z0-9_]+(?:,\\s*[a-zA-Z0-9_]+\\s+[a-zA-Z0-9_]+)*)\\);$";
    private static String DROP_REGEX = "^drop\\s+(database|table)\\s+([a-zA-Z0-9_]+);$";
    private static String SELECT_REGEX = "^select\\s+(\\*|[a-zA-Z0-9_,\\s]+)\\s+from\\s+([a-zA-Z0-9_]+)(\\s+where\\s+([a-zA-Z0-9_]+)\\s*=\\s*([a-zA-Z0-9_]+))?;?$";
    private static String INSERT_REGEX = "^insert into\\s+([a-zA-Z0-9_`]+)\\s*(\\(([a-zA-Z0-9_,\\s`]+)\\))?\\s+values\\s+\\(([^)]+)\\);?$";


    public MatchRegex(Database database){
        this.database = database;
    }

    public boolean matchUseRegex(String query){
        Pattern pattern = Pattern.compile(USE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if(matcher.find()) {
            this.database.useDatabase(matcher.group(1));
        } else {
            System.out.println("Query does not match the pattern.");
            return false;
        }
        return true;
    }

    public boolean matchCreateRegex(String query, QueryLevel queryLevel){
        if(queryLevel == QueryLevel.DATABASE){
            return matchCreateDatabaseRegex(query);
        }
        else{
            return matchCreateTableRegex(query);
        }
    }

    private boolean matchCreateDatabaseRegex(String query){
        Pattern pattern = Pattern.compile(CREATE_DATABASE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if(matcher.find()) {
            String entity = matcher.group(1);
            this.database.createDatabase(entity);
        } else {
            System.out.println("Query does not match the pattern.");
            return false;
        }
        return true;
    }

    private boolean matchCreateTableRegex(String query){
        Pattern pattern = Pattern.compile(CREATE_TABLE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if(matcher.find()) {
            String entity = matcher.group(1);
            String columns = matcher.group(2);
            this.database.createTable(entity, columns);
        } else {
            System.out.println("Query does not match the pattern.");
            return false;
        }
        return true;
    }

    public boolean matchDropRegex(String query){
        Pattern pattern = Pattern.compile(DROP_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if(matcher.find()) {
            String entity = matcher.group(1);
            String name = matcher.group(2);
            if(entity.equals("database")){
                this.database.dropDatabase(name);
            }
            else{
                this.database.dropTable(name);
            }
        } else {
            System.out.println("Query does not match the pattern.");
            return false;
        }
        return true;
    }

    public boolean matchSelectRegex(String query){
        Pattern pattern = Pattern.compile(SELECT_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if(matcher.find()) {
            database.selectData(matcher.group(2), matcher.group(1), matcher.group(4), matcher.group(5));
        } else {
            System.out.println("Query does not match the pattern.");
            return false;
        }

        return true;
    }

    public boolean matchInsertRegex(String query){
        Pattern pattern = Pattern.compile(INSERT_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(query);

        if(matcher.find()) {
            database.insertData(matcher.group(1), matcher.group(3), matcher.group(4));
        } else {
            System.out.println("Query does not match the pattern.");
            return false;
        }

        return true;
    }
}
