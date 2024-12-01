// InsufficientFundsException.java
// Thi needed to be in here or the program doesn't work
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException() {
        super();
    }

    public InsufficientFundsException(String message) {
        super(message);
    }
}