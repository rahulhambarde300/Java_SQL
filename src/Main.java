import java.util.Scanner;

public class Main {
    public static void main(String args[]){
        Authentication.authenticate();
        Database database = new Database();

        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.println("Input the query:");
            String query = sc.nextLine();
            QueryParser queryParser = new QueryParser();
            ParsedQuery parsedQuery = queryParser.getParsedQuery(query);

            if(parsedQuery.getCommandType() == CommandType.SHOW){
                if(parsedQuery.getQueryLevel() == QueryLevel.DATABASE){
                    System.out.print(database.showDatabases());
                }
                else{
                    System.out.print(database.showTables());
                }
            }

            MatchRegex matchRegex = new MatchRegex(database);

            if(parsedQuery.getCommandType() == CommandType.USE){
                matchRegex.matchUseRegex(query);
            }
            else if(parsedQuery.getCommandType() == CommandType.CREATE){
                matchRegex.matchCreateRegex(query, parsedQuery.getQueryLevel());
            }
            else if(parsedQuery.getCommandType() == CommandType.DROP){
                matchRegex.matchDropRegex(query);
            }
            else if(parsedQuery.getCommandType() == CommandType.SELECT){
                matchRegex.matchSelectRegex(query);
            }
            else if(parsedQuery.getCommandType() == CommandType.INSERT){
                matchRegex.matchInsertRegex(query);
            }
            else if(parsedQuery.getCommandType() == CommandType.START_TRANSACTION){
                database.startTransaction();
            }
            else if(parsedQuery.getCommandType() == CommandType.COMMIT){
                database.commit();
            }
            else if(parsedQuery.getCommandType() == CommandType.ROLLBACK){
                database.rollback();
            }
            else{
                if(parsedQuery.getCommandType() != CommandType.SHOW)
                {
                    System.out.println("Please enter valid command");
                }
            }
        }

    }
}
