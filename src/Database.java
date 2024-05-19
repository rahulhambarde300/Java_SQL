import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DATABASE_DIRECTORY = ".\\sql\\databases\\";
    private static final String DATABASE_FILE_PATH = ".\\sql\\databases.txt";
    public static final String TABLES_FOLDER_PATH = "\\tables\\";
    public static final String TABLES_FILE_PATH = "\\tables.txt";
    public static final String SCHEMA_TXT = "\\schema.txt";
    public static final String DATA_TXT = "\\data.txt";
    private String selectedDatabase = "";
    private List<File> originalFiles;
    private List<File> backupFiles;
    private boolean transactionMode = false;

    public Database(){
        originalFiles = new ArrayList<File>();
        backupFiles = new ArrayList<File>();
    }
    /**
     * Create a new database if it doesn't exist
     * @param databaseName Name of the database
     */
    public void createDatabase(String databaseName){
        try(
                FileWriter writer = new FileWriter(new File(DATABASE_FILE_PATH), true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
        ){
            if(checkIfEntryExists(DATABASE_FILE_PATH, databaseName)){
                System.out.println("Database already exists");
                return;
            }
            printWriter.println(databaseName.toLowerCase());
            File file = new File(DATABASE_DIRECTORY+databaseName);
            if (!file.exists()){
                file.mkdirs();
            }

            System.out.println("Created new database: "+databaseName);
        }
        catch (Exception e){
            System.out.println("Exception occurred while creating database: "+e);
        }
    }

    /**
     * Drop a database with its name
     * @param databaseName name of the database
     * @return True if database is dropped otherwise false
     */
    public boolean dropDatabase(String databaseName){
        boolean databaseRemoved = removeEntry(DATABASE_FILE_PATH, databaseName);
        File file = new File(DATABASE_DIRECTORY+databaseName);
        deleteDirectory(file);
        System.out.println("Database dropped: "+databaseName);
        return databaseRemoved;
    }

    /**
     * Use a database to make changes to it
     * @param databaseName Name of the database
     * @return Name of the database if it exists or empty string
     */
    public String useDatabase(String databaseName){
        if(checkIfEntryExists(DATABASE_FILE_PATH, databaseName)){
            this.selectedDatabase = databaseName;
            System.out.println("Using "+this.selectedDatabase+" database.");
            return databaseName;
        }
        System.out.println("No database exists with name: "+databaseName);
        return "";
    }

    /**
     * Show all database schemas
     */
    public String showDatabases(){
        String output = "";
        List<String> outputList = getAllEntries(DATABASE_FILE_PATH);
        if(outputList == null || outputList.isEmpty()){
            return "";
        }
        for(String entry: outputList){
            output += entry + "\n";
        }
        System.out.println("All existing databases:");
        return output;
    }

    /**
     * Show all tables in current database
     * @return Name of the database if it exists or empty string
     */
    public String showTables(){
        if(!isUsingDatabase()){
            return "";
        }
        String output = "";
        List<String> outputList = getAllEntries(DATABASE_DIRECTORY+selectedDatabase+TABLES_FILE_PATH);
        if(outputList == null || outputList.isEmpty()){
            return "";
        }
        for(String entry: outputList){
            output += entry + "\n";
        }
        return output;
    }

    public void createTable(String tableName, String columnQueries){
        if(!isUsingDatabase()){
            return;
        }
        if(selectedDatabase.isEmpty()){
            System.out.println("Select a database first");
            return;
        }
        if(tableName.isEmpty() || columnQueries.isEmpty()){
            System.out.println("Please use valid query for creating table");
            return;
        }
        try(
                FileWriter writer = new FileWriter(new File(DATABASE_DIRECTORY+selectedDatabase+TABLES_FILE_PATH), true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
        ){
            if(checkIfEntryExists(DATABASE_DIRECTORY+selectedDatabase+TABLES_FILE_PATH, tableName)){
                System.out.println("Table already exists");
                return;
            }
            printWriter.println(tableName.toLowerCase());

            //Create new folder for tables
            File tableFolder = new File(DATABASE_DIRECTORY+selectedDatabase+TABLES_FOLDER_PATH+tableName);
            if (!tableFolder.exists()){
                tableFolder.mkdirs();
            }

            System.out.println("New table created:"+tableName);
        }
        catch (Exception e){
            System.out.println("Exception occurred while creating table: "+e);
        }

        createTableAndColumns(tableName, columnQueries);
    }

    /**
     * Drop a table with its name
     * @param tableName name of the database
     * @return True if table is dropped otherwise false
     */
    public boolean dropTable(String tableName){
        if(!isUsingDatabase()){
            return false;
        }
        boolean tableRemoved = removeEntry(DATABASE_DIRECTORY+selectedDatabase+TABLES_FILE_PATH, tableName);
        File file = new File(DATABASE_DIRECTORY+selectedDatabase+TABLES_FOLDER_PATH+tableName);
        deleteDirectory(file);

        System.out.println("Table dropped:"+tableName);
        return tableRemoved;
    }

    /**
     * Insert data into give table
     * @param tableName Name of the table
     * @param columnString All the columns name
     * @param valueString All the values
     * @return True if data inserted otherwise false
     */
    public boolean insertData(String tableName, String columnString, String valueString){
        if(!isUsingDatabase()){
            return false;
        }
        String values = valueString.toLowerCase().trim().replaceAll(" ", "");
        File file = new File(DATABASE_DIRECTORY+selectedDatabase+ TABLES_FOLDER_PATH +tableName+DATA_TXT);
        try(
                FileWriter writer = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);
        )
        {
            if(transactionMode){
                File backupFile = new File(file.getAbsolutePath() + ".bak");
                Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                originalFiles.add(file);
                backupFiles.add(backupFile);
            }
            printWriter.println(values);
        }
        catch (Exception e){
            System.out.println("Exception occurred while creating table: "+e);
        }
        return false;
    }

    /**
     * Select data from a table
     * @param tableName Name of the table
     * @param columnString All the columns name
     * @param conditionColumn Condition if there is any
     * @param conditionValue Condition value if there is any
     * @return
     */
    public void selectData(String tableName, String columnString, String conditionColumn, String conditionValue){
        if(!isUsingDatabase()){
            return;
        }
        String output = "";
        List<String> outputList = getAllEntries(DATABASE_DIRECTORY+selectedDatabase+ TABLES_FOLDER_PATH +tableName+DATA_TXT);
        if(outputList == null || outputList.isEmpty()){
            System.out.println("Invalid table");
        }
        for(String entry: outputList){
            output += entry + "\n";
        }
        System.out.println(output);
    }

    public void startTransaction(){
        transactionMode = true;
        System.out.println("Using transaction");
    }

    public void commit(){
        originalFiles = new ArrayList<>();
        backupFiles = new ArrayList<>();
        transactionMode = false;
        System.out.println("Transaction committed");
    }

    public void rollback(){
        try{
            for(int i = 0; i < originalFiles.size(); i++){
                Files.move(backupFiles.get(i).toPath(), originalFiles.get(i).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            originalFiles = new ArrayList<>();
            backupFiles = new ArrayList<>();
            transactionMode = false;
            System.out.println("Transaction rollback");
        }
        catch (Exception e){
            System.out.println("Failure while rollback");
        }
    }

    /**
     * Create table with its columns
     * @param tableName
     * @param columnLines
     */
    private void createTableAndColumns(String tableName, String columnLines){
        if(!isUsingDatabase()){
            return;
        }
        String[] columnArray = columnLines.split(",");
        for(int i = 0; i < columnArray.length; i++){
            String[] column = columnArray[i].trim().split(" ");
            if(column.length != 2){
                throw new RuntimeException("Column lines not formatted properly");
            }

            /*Check for each column, the data type exists*/
            if(DataTypes.valueOfType(column[1]) == null){
                throw new RuntimeException("Invalid data type on line: "+i);
            }
        }

        try(
                FileWriter writer = new FileWriter(new File(DATABASE_DIRECTORY+selectedDatabase+ TABLES_FOLDER_PATH +tableName+SCHEMA_TXT));
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                PrintWriter printWriter = new PrintWriter(bufferedWriter);

                FileWriter writer2 = new FileWriter(new File(DATABASE_DIRECTORY+selectedDatabase+ TABLES_FOLDER_PATH +tableName+DATA_TXT));
                BufferedWriter bufferedWriter2 = new BufferedWriter(writer2);
                PrintWriter printWriter2 = new PrintWriter(bufferedWriter2);
        )
        {
            for(int i = 0; i < columnArray.length; i++){
                //Create a new file and add columns
                printWriter.println(columnArray[i].trim().toLowerCase());
            }

            String columnNames = "";
            for(int i = 0; i < columnArray.length; i++){
                String delimiter = (i == columnNames.length()) ?",": "";
                columnNames += columnArray[i].trim().toLowerCase().split(" ")[0] + delimiter;
            }
            printWriter2.println(columnNames);
        }
        catch (Exception e){
            System.out.println("Exception occurred while creating table: "+e);
        }
    }


    /**
     * Check if the entry with given name exists
     * @param name Name of the entry
     * @return True if the entry exists otherwise false
     */
    private boolean checkIfEntryExists(String path, String name){
        try(FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ){
            String line = bufferedReader.readLine();
            while(line != null){
                String entry = line.trim().toLowerCase();
                if (name.toLowerCase().equals(entry)) {
                    return true;
                }
                line = bufferedReader.readLine();
            }
        }
        catch (Exception e){
            //No file found
            return false;
        }
        return false;
    }

    private List<String> getAllEntries(String path){
        List<String> entryList = new ArrayList<>();
        try(FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ){
            String line = bufferedReader.readLine();
            while(line != null){
                String entry = line.trim().toLowerCase();
                entryList.add(entry);
                line = bufferedReader.readLine();
            }
        }
        catch (Exception e){
            //No file found
            return null;
        }
        return entryList;
    }

    private boolean removeEntry(String path, String name){
        boolean entryRemoved = false;
        String stringToRewrite = "";
        try(FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ){
            String line = bufferedReader.readLine();

            while(line != null){
                String entry = line.trim().toLowerCase();
                if (name.toLowerCase().equals(entry)) {
                    line = bufferedReader.readLine();
                    continue;
                }
                stringToRewrite += entry + "\n";
                line = bufferedReader.readLine();
            }
        }
        catch (Exception e){
            //No file found
            return entryRemoved;
        }

        try (
            FileWriter writer = new FileWriter(new File(path) );
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
        ){
            printWriter.write(stringToRewrite);
            entryRemoved = true;
        }
        catch (Exception e)
        {
            //No file found
            return entryRemoved;
        }
        return entryRemoved;
    }

    private boolean isUsingDatabase(){
        if(this.selectedDatabase.isEmpty()){
            System.out.println("Please select a database");
            return false;
        }
        return true;
    }

    /**
     * Force deletion of directory
     * @param path
     * @return
     */
    private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

}
