


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class VegasBankGUI extends JFrame {

    private LinkedList<CheckingAccount> accounts;
    private Random random;

    private JTextField firstNameField, lastNameField, depositField, accountIDField, amountField;
    private JTextArea outputArea;
    private DefaultTableModel tableModel;
    private JTable accountsTable;

    public VegasBankGUI(LinkedList<CheckingAccount> accounts) {
        this.accounts = accounts;
        this.random = new Random();
//how big the window is
        setTitle("Vegas Bank");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text input fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Initial Deposit:"));
        depositField = new JTextField();
        inputPanel.add(depositField);
        inputPanel.add(new JLabel("Account ID:"));
        accountIDField = new JTextField();
        accountIDField.setEditable(false);
        inputPanel.add(accountIDField);
        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);
        add(inputPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Account ID", "First Name", "Last Name", "Balance"};
        tableModel = createHyperlinkTableModel(columnNames);
        accountsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        add(scrollPane, BorderLayout.EAST);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton createButton = new JButton("Create Account");
        createButton.addActionListener(new CreateAccountListener());
        buttonPanel.add(createButton);
        JButton displayButton = new JButton("Display Account");
        displayButton.addActionListener(new DisplayAccountListener());
        buttonPanel.add(displayButton);
        JButton printAllButton = new JButton("Print All Accounts");
        printAllButton.addActionListener(new PrintAllAccountsListener());
        buttonPanel.add(printAllButton);

        // Account Actions Panel
        JPanel accountActionsPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(new DepositListener());
        accountActionsPanel.add(depositButton);
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new WithdrawListener());
        accountActionsPanel.add(withdrawButton);
        JButton saveButton = new JButton("Save Accounts");
        saveButton.addActionListener(new SaveAccountsListener());
        buttonPanel.add(saveButton);

        JButton loadButton = new JButton("Load Accounts");
        loadButton.addActionListener(new LoadAccountsListener());
        buttonPanel.add(loadButton);


        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(accountActionsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // This is the bottom scroll pane to show whats happening
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        add(outputScrollPane, BorderLayout.SOUTH);
        outputScrollPane.setPreferredSize(new Dimension(400, 400)); // Width, Height

        // hyperlinked account numbers to select the account to
        //adjust for deposit withdraw
        accountsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = accountsTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    int accountID = (int) accountsTable.getValueAt(row, 0);
                    accountIDField.setText(String.valueOf(accountID));
                }
            }
        });

        setVisible(true);
    }


    // random account number
    private int generateAccountID(Random random, LinkedList<CheckingAccount> accounts) {
        int accountID;
        do {
            accountID = random.nextInt(1000000); // Generate a 6-digit ID
        } while (accountIDExists(accountID, accounts));
        return accountID;
    }

    // duplicate account number exception
    private boolean accountIDExists(int accountID, LinkedList<CheckingAccount> accounts) {
        for (CheckingAccount account : accounts) {
            if (account.getAccountID() == accountID) {
                return true;
            }
        }
        return false;
    }

    // if you know the id this will search for and return to the
    //id text input to modify that account
    private CheckingAccount findAccount(int accountID, LinkedList<CheckingAccount> accounts) {
        for (CheckingAccount account : accounts) {
            if (account.getAccountID() == accountID) {
                return account;
            }
        }
        return null;
    }

    private int findRowByAccountID(int accountID) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(accountID)) {
                return i;
            }
        }
        return -1;
    }
    //The action listener for the save accounts to file button
    class SaveAccountsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Accounts");
            int userSelection = fileChooser.showSaveDialog(VegasBankGUI.this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileToSave)))) {
                    for (CheckingAccount account : accounts) {
                        out.println(account.getId() + "," + account.getFirstName() + "," + account.getLastName() + "," + account.getBalance());
                    }
                    JOptionPane.showMessageDialog(VegasBankGUI.this, "Accounts saved successfully.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(VegasBankGUI.this, "File save failed: " + ex.getMessage());
                }
            }
        }
    }
    //action listener for the load accounts from file button
    class LoadAccountsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load Accounts");
            int userSelection = fileChooser.showOpenDialog(VegasBankGUI.this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = fileChooser.getSelectedFile();
                accounts.clear();
                try (Scanner scanner = new Scanner(fileToLoad)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] parts = line.split(",");
                        if (parts.length == 4) {
                            int id = Integer.parseInt(parts[0]);
                            String firstName = parts[1];
                            String lastName = parts[2];
                            double balance = Double.parseDouble(parts[3]);

                            CheckingAccount account = new CheckingAccount(0.05);
                            account.setAccountID(id); // Use setAccountID() here
                            account.setFirstName(firstName);
                            account.setLastName(lastName);
                            account.deposit(balance);
                            accounts.add(account);

                            Object[] row = {id, firstName, lastName, balance};
                            tableModel.addRow(row);
                        }
                    }
                    JOptionPane.showMessageDialog(VegasBankGUI.this, "Accounts loaded successfully.");
                } catch (IOException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(VegasBankGUI.this, "File load failed: " + ex.getMessage());
                }
            }
        }
    }
    // ActionListener for Create Account button
    private class CreateAccountListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                double initialDeposit = Double.parseDouble(depositField.getText());

                int accountID = generateAccountID(random, accounts);

                CheckingAccount myAccount = new CheckingAccount(0.05);
                myAccount.setFirstName(firstName);
                myAccount.setLastName(lastName);
                myAccount.setAccountID(accountID);
                myAccount.deposit(initialDeposit);

                accounts.add(myAccount);

                accountIDField.setText(String.valueOf(accountID));
                outputArea.append("Account created successfully. Account ID: " + accountID + "\n");

                // Clear fields for a new account
                firstNameField.setText("");
                lastNameField.setText("");
                depositField.setText("");
                Object[] row = {accountID, firstName, lastName, initialDeposit};
                tableModel.addRow(row);

            } catch (NumberFormatException ex) {
                outputArea.append("Invalid input for initial deposit.\n");
            }
        }
    }

    // ActionListener for Deposit button
    private class DepositListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int accountID = Integer.parseInt(accountIDField.getText());
                double amount = Double.parseDouble(amountField.getText());

                CheckingAccount selectedAccount = findAccount(accountID, accounts);
                if (selectedAccount == null) {
                    outputArea.append("Account not found.\n");
                    return;
                }

                selectedAccount.deposit(amount);
                outputArea.append("Deposit successful.\n");

                // Update the table
                int row = findRowByAccountID(accountID);
                if (row != -1) {
                    tableModel.setValueAt(selectedAccount.getBalance(), row, 3);
                }

                // Clear amount field
                amountField.setText("");
            } catch (NumberFormatException ex) {
                outputArea.append("Invalid input for account ID or amount.\n");
            }
        }
    }

    // ActionListener for Withdraw button
    private class WithdrawListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int accountID = Integer.parseInt(accountIDField.getText());
                double amount = Double.parseDouble(amountField.getText());

                CheckingAccount selectedAccount = findAccount(accountID, accounts);
                if (selectedAccount == null) {
                    outputArea.append("Account not found.\n");
                    return;
                }

                selectedAccount.processWithdrawal(amount);
                outputArea.append("Withdrawal processed.\n");

                // Update the table
                int row = findRowByAccountID(accountID);
                if (row != -1) {
                    tableModel.setValueAt(selectedAccount.getBalance(), row, 3);
                }

                // Clear amount field
                amountField.setText("");
            } catch (NumberFormatException | InsufficientFundsException ex) {
                outputArea.append("Invalid input for account ID or amount.\n");
            }
        }
    }

    // ActionListener for Display Account button which displays
    //in the bottom scrollpane text output area
    private class DisplayAccountListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int accountID = Integer.parseInt(accountIDField.getText());

                CheckingAccount selectedAccount = findAccount(accountID, accounts);
                if (selectedAccount == null) {
                    outputArea.append("Account not found.\n");
                    return;
                }

                outputArea.append(selectedAccount.getAccountSummary() + "\n");
            } catch (NumberFormatException ex) {
                outputArea.append("Invalid input for account ID.\n");
            }
        }
    }

    // ActionListener for Print All Accounts button
    //prints all the accounts to the scroll pane text output area
    private class PrintAllAccountsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (accounts.isEmpty()) {
                outputArea.append("No accounts found.\n");
                return;
            }
            for (CheckingAccount account : accounts) {
                outputArea.append(account.getAccountSummary() + "\n");
                outputArea.append("--------------------\n");
            }
        }
    }
    private DefaultTableModel createHyperlinkTableModel(String[] columnNames) {
        return new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) { // Account ID column
                    return String.class;
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
    public static void main(String[] args) {
        LinkedList<CheckingAccount> accounts = new LinkedList<>();
        SwingUtilities.invokeLater(() -> new VegasBankGUI(accounts));
    }



}
