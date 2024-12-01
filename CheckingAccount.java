

// CheckingAccount.java this class is all the getters and setters to the MEMORY where the
//data is stored
public class CheckingAccount extends BankAccount {//this indicates this class is a child of BankAccount
    private double interestRate;
    private int accountID;


    public CheckingAccount(double interestRate) {//This shows the interest rate
        //it doesnt do anything unless this program stays on for a year
        this.accountID = -1;
        this.interestRate = interestRate;
    }
    public void setId(int id) {
        this.accountID = id;
    }
    public int getId() {
        return this.accountID;
    }
    public void applyInterest() {
        double interest = getBalance() * interestRate / 100;
        deposit(interest); // Use the superclass method
        // Instead of System.out.println, update GUI or log
    }
    public void processWithdrawal(double amount) throws InsufficientFundsException {
        if (getBalance() >= amount) {
            withdrawal(amount);
        } else {
            if (getBalance() - amount - 30 < 0) {
                throw new InsufficientFundsException("Insufficient funds. Overdraft would exceed account balance.");
            } else {
                withdrawal(amount + 30); // Overdraft fee
                // Update GUI here instead of System.out.println
            }
        }
    }
    public String displayAccount() {
        // Instead of printing to console, return the string or update the GUI
        return super.getAccountSummary() + "\n" +
                "Interest Rate: " + interestRate + "%";
    }

    public String getAccountSummary() { // Added for GUI output
        //I tried to make a GUI class but cant get it to work right
        //I'll putit in my github
        return super.getAccountSummary() + "\n" +
                "Interest Rate: " + interestRate + "%";
    }
}
