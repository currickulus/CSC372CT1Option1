import java.util.Scanner; //Gets input from user
import java.util.LinkedList; //Will store data as a linked list
import java.util.Random; //randomly generates a number for the account
import java.io.FileWriter; //stores data to file
import java.io.IOException; //this prevents file writing problems just in case
import java.time.format.DateTimeFormatter; //Formats the date and time to get the filename
import java.time.LocalDateTime; //Takes the computer date where the file is created

public class VegasBank { //main class link to file
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in); //provides definition for user input
        LinkedList<CheckingAccount> accounts = new LinkedList<>(); //determines the linked list like an array definition
        Random random = new Random(); //indicates a random number generator

       //this displays a menu to select from
        while (true) { //as long as its not closed
            System.out.println("\nAccount Management Menu:");//menu title
            System.out.println("1. Create account");//choice 1
            System.out.println("2. Select account");//choice 2
            System.out.println("3. Print all accounts");//choice 3
            System.out.println("4. Save accounts to file and close");//save and exit choice
            System.out.println("5. Exit");//exit choice
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();//integer declarartion that associates with methods activated
            input.nextLine(); //makes a new line to start new things

            switch (choice) {
                case 1://These are the actual switches that call the methods selected
                    createAccount(input, random, accounts);
                    break;
                case 2://choice 2 method activation selectAccount method
                    selectAccount(input, accounts);
                    break;
                case 3:
                    printAllAccounts(accounts);//calls print AllAccounts
                    break;
                case 4:
                    saveAccountsToFile(accounts); // Calls save to file
                    input.close();
                    return;
                case 5:
                    return; // Exit the program
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Generate a unique random account ID
    private static int generateAccountID(Random random, LinkedList<CheckingAccount> accounts) {
        int accountID;
        do {
            accountID = random.nextInt(1000000); // Generate a 6-digit ID
        } while (accountIDExists(accountID, accounts));
        return accountID;
    }

    // Check if an account ID already exists just in case the random number generator hit the same number more than once
    private static boolean accountIDExists(int accountID, LinkedList<CheckingAccount> accounts) {
        for (CheckingAccount account : accounts) {
            if (account.getAccountID() == accountID) {
                return true;
            }
        }
        return false;
    }

    // Create a new account theres no loop here it goes step by step until it has all 3 components to create an account
    private static void createAccount(Scanner input, Random random, LinkedList<CheckingAccount> accounts) {
        System.out.print("Enter first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter last name: ");
        String lastName = input.nextLine();

        System.out.print("Enter initial deposit amount: ");
        double initialDeposit = input.nextDouble();
        input.nextLine(); // Consume newline

        // Generate a random account ID
        int accountID = generateAccountID(random, accounts);

        // Create a CheckingAccount object
        CheckingAccount myAccount = new CheckingAccount(0.05);
        myAccount.setFirstName(firstName);
        myAccount.setLastName(lastName);
        myAccount.setAccountID(accountID);

        // Make the initial deposit
        myAccount.deposit(initialDeposit);

        // Add account to the linked list
        accounts.add(myAccount);

        System.out.println("Account created successfully. Account ID: " + accountID);
    }

    // Select an account and commit withdrawls and deposits
    private static void selectAccount(Scanner input, LinkedList<CheckingAccount> accounts) {
        System.out.print("Enter account ID: ");
        int accountID = input.nextInt();
        input.nextLine(); // Consume newline

        CheckingAccount selectedAccount = findAccount(accountID, accounts);
        if (selectedAccount == null) {
            System.out.println("Account not found.");
            return;
        }
//This is the loop for this method it can be repeated until the
        //this whole program is similar to what a teller would use at the bank
        while (true) {
            System.out.println("\nAccount Operations:");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Display account information");
            System.out.println("4. Back to main menu");
            System.out.print("Enter your choice: ");
//these are inherent to this method only
            int choice = input.nextInt();
            input.nextLine(); // Consume newline

            switch (choice) {
                case 1://select a deposit to the current account
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = input.nextDouble();
                    input.nextLine(); // Consume newline
                    selectedAccount.deposit(depositAmount);
                    System.out.println("Deposit successful.");
                    break;
                case 2://select withdrawal to the current account
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawalAmount = input.nextDouble();
                    input.nextLine(); // Consume newline
                    try {
                        selectedAccount.processWithdrawal(withdrawalAmount);
                    } catch (InsufficientFundsException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 3://displays the current account value
                    selectedAccount.displayAccount();
                    break;
                case 4://returns to the main menu on program open
                    return;
                default://the user has chosen poorly
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Find an account by ID one must know the account number to change.
    //Probably should make this print the account number whent the first and last name are searched
    //Thats alot more methods than this assignment requires.
    private static CheckingAccount findAccount(int accountID, LinkedList<CheckingAccount> accounts) {
        for (CheckingAccount account : accounts) {
            if (account.getAccountID() == accountID) {
                return account;
            }
        }
        return null;
    }

    // Print all accounts this is how one can find the account to change
    private static void printAllAccounts(LinkedList<CheckingAccount> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        for (CheckingAccount account : accounts) {
            account.displayAccount();
            System.out.println("--------------------");
        }
    }

    // Save accounts to a file with the date in the filename
    private static void saveAccountsToFile(LinkedList<CheckingAccount> accounts) {
        //this is how the file gets it's name theres gonna be a date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        //this calls the date where the date comes from
        LocalDateTime now = LocalDateTime.now();
        //now
        String filename = "accounts_" + dtf.format(now) + ".txt";
//list all the names and account numbers and values in MEMORY to print to the file.
        try (FileWriter writer = new FileWriter(filename)) {
            for (CheckingAccount account : accounts) {
                writer.write(account.getAccountID() + "," +
                        account.getFirstName() + "," +
                        account.getLastName() + "," +
                        account.getBalance() + "\n");
            }
            System.out.println("Accounts saved to file: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving accounts to file: " + e.getMessage());
        }
    }
}
