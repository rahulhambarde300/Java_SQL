import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Authentication {

    private static final String USER_FILE_PATH = ".\\sql\\users.txt";
    private static String captcha = "";

    /**
     * Manages the authentication of a user
     */
    public static void authenticate(){
        Scanner sc = new Scanner(System.in);
        boolean isLoggedIn = false;

        while(!isLoggedIn){
            System.out.println("Select one of the options:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            int selectedOption = -1;
            try{
                selectedOption = sc.nextInt();
            }
            catch (Exception e){
                System.out.println("Please select integer only");
                authenticate();
            }
            sc.nextLine();


            switch (selectedOption){
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    isLoggedIn = true;
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Please select a valid option");
                    authenticate();
            }
        }
    }

    /*
    I used this blog to learn MD5 hashing and implement it in my program
    Title: MD5 Hashing in Java
    Author: baeldung
    Date: February 24, 2024
    Type: Source code
    Availability: https://www.baeldung.com/java-md5
    */
    private static void registerUser(){
        try(
            FileWriter writer = new FileWriter(USER_FILE_PATH, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
        ){
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter new username");
            String username = sc.nextLine();
            System.out.println("Enter new password");
            String password = sc.nextLine();

            messageDigest.update(password.getBytes());
            byte[] digest = messageDigest.digest();
            String hashedPassword = bytesToHex(digest);
            if(checkIfUserExists(username)){
                System.out.println("The username already exists, please login");
                authenticate();
            }

            printWriter.println(username+":"+hashedPassword);
            System.out.println("User registration completed");
        }
        catch (Exception e){
            System.out.println("Exception while registering user");
        }
    }

    private static void loginUser(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your username");
        String username = sc.nextLine();
        System.out.println("Enter your password");
        String password = sc.nextLine();
        String captcha = generateCaptcha();
        System.out.println("Please input this captcha below: "+captcha);
        String inputCaptcha = sc.nextLine();

        if(!checkIfUserExists(username)){
            System.out.println("The username doesn't exist, please register");
            authenticate();
            return;
        }
        if(!captcha.equals(inputCaptcha)){
            System.out.println("Captcha doesn't match, please retry");
            loginUser();
            return;
        }
        else{
            System.out.println("Captcha successful");
        }
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes());
            byte[] digest = messageDigest.digest();
            String hashedPassword = bytesToHex(digest);

            if(validatePassword(username, hashedPassword)){
                System.out.println("User logged in successfully");
            }
            else{
                System.out.println("Invalid credentials");
                loginUser();
            }
        }
        catch (NoSuchAlgorithmException e){
            System.out.println("Exception while logging user in");
        }
    }

    private static String generateCaptcha(){
        char chars[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
                'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C',
                'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '-', '|', '_', '+', '/', '$', '#', '@', '!', '*', '&', '%'};
        int i = 0;
        String tempCaptcha = "";
        Random random = new Random();
        while(i < 8){
            tempCaptcha += chars[random.nextInt(chars.length)];
            i++;
        }
        return tempCaptcha;
    }

    /**
     * Check if the user with 'username' already exists
     * @param username name for the user
     * @return True if already registered otherwise false
     */
    private static boolean checkIfUserExists(String username){
        try(FileReader fileReader = new FileReader(USER_FILE_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ){
            String line = bufferedReader.readLine();
            while(line != null){
                String savedUsername = line.split(":")[0];
                if (username.toLowerCase().equals(savedUsername)) {
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

    private static boolean validatePassword(String username, String password){
        try(FileReader fileReader = new FileReader(USER_FILE_PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ){
            String line = bufferedReader.readLine();
            while(line != null){
                String lineArr[] = line.split(":");
                /*The username matches, now check the password*/
                if (username.toLowerCase().equals(lineArr[0])) {
                    if(password.equals(lineArr[1])){
                        /*The password matches*/
                        return true;
                    }/*The password doesn't match*/
                    return false;
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

    /**
     * Todo: Find a source for this
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder string = new StringBuilder();
        for (byte b : bytes) {
            String character = Integer.toHexString(0xff & b).toUpperCase();
            if (character.length() == 1) {
                string.append('0');
            }
            string.append(character);
        }
        return string.toString();
    }
}
