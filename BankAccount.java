// BankAccount.java
public class BankAccount {//the checking account class inherits this class
    private String firstName;
    private String lastName;
    private int accountID;
    private double balance;

    public BankAccount() {//This is the declaration that stores the value of the bank account
        this.balance = 0;
    }

    public void deposit(double amount) {//If a deposit is made this method adds to the balance
        this.balance += amount;
    }

    public void withdrawal(double amount) {//If a withdrawal is made this method deducts from the balance
        this.balance -= amount;
    }

    // Setters and Getters
    public String getFirstName() {//gets first name
        return firstName;
    }

    public void setFirstName(String firstName) {//sets first name
        this.firstName = firstName;
    }

    public String getLastName() {//gets last name
        return lastName;
    }

    public void setLastName(String lastName) {//sets last name
        this.lastName = lastName;
    }

    public int getAccountID() {//this comes from the random number generator in VegasBank
        return accountID;
    }

    public void setAccountID(int accountID) {//This sets the account ID
        this.accountID = accountID;
    }

    public double getBalance() {//This method returns the balance called in VegasBank
        //well it's in here but everything is done in there
        return balance;
    }
//This gets printed to the scrollpane in vegas bank Gui
    public String getAccountSummary() {  // Added for GUI output
        return "Account Summary:\n" +
                "Name: " + firstName + " " + lastName + "\n" +
                "Account ID: " + accountID + "\n" +
                "Balance: $" + balance;
    }

    public void accountSummary() {//This is returned in VegasBank the main console program
        System.out.println("Account Summary:");
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Account ID: " + accountID);
        System.out.println("Balance: $" + balance);
    }
}
