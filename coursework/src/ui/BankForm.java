package ui;

import model.*;
import db.DatabaseHelper;
import util.Validator;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BankForm extends JFrame {
    // Input fields
    private JTextField firstNameField, lastNameField, ninField, emailField, confirmEmailField;
    private JTextField phoneField, pinField, confirmPinField, depositField;
    private JComboBox<String> yearBox, monthBox, dayBox;
    private JComboBox<String> accountTypeBox, branchBox;
    private JTextArea summaryArea;
    private Map<String, JLabel> errorLabels; // inline error labels per field

    private Map<String, Account> accountMap;

    public BankForm() {
        setTitle("First Bank Uganda - Account Opening");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(850, 750);
        setLocationRelativeTo(null);

        // Initialize account types
        accountMap = new HashMap<>();
        accountMap.put("Savings", new SavingsAccount());
        accountMap.put("Current", new CurrentAccount());
        accountMap.put("Fixed Deposit", new FixedDepositAccount());
        accountMap.put("Student", new StudentAccount());
        accountMap.put("Joint", new JointAccount());

        // Build form panel using GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        errorLabels = new LinkedHashMap<>();

        int row = 0;
        // Helper to add a row with label, field, and error label
        // We'll create each row with a label, a field, and an error label (initially
        // empty)
        // using a panel to contain them in a flow layout.

        // We'll use a helper method that adds a row with a panel containing label,
        // field, and error label.
        // But for simplicity, we'll add each component individually, and store the
        // error label.

        // First Name
        addRow(formPanel, gbc, "First Name:", firstNameField = new JTextField(15), row++);
        // Last Name
        addRow(formPanel, gbc, "Last Name:", lastNameField = new JTextField(15), row++);
        // NIN
        addRow(formPanel, gbc, "NIN:", ninField = new JTextField(14), row++);
        // Email
        addRow(formPanel, gbc, "Email:", emailField = new JTextField(20), row++);
        // Confirm Email
        addRow(formPanel, gbc, "Confirm Email:", confirmEmailField = new JTextField(20), row++);
        // Phone
        addRow(formPanel, gbc, "Phone (+256):", phoneField = new JTextField(16), row++);
        // PIN
        addRow(formPanel, gbc, "PIN:", pinField = new JTextField(6), row++);
        // Confirm PIN
        addRow(formPanel, gbc, "Confirm PIN:", confirmPinField = new JTextField(6), row++);

        // DOB combo boxes
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        yearBox = new JComboBox<>();
        monthBox = new JComboBox<>();
        dayBox = new JComboBox<>();
        populateDOB();
        dobPanel.add(new JLabel("Date of Birth:"));
        dobPanel.add(yearBox);
        dobPanel.add(monthBox);
        dobPanel.add(dayBox);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(dobPanel, gbc);
        JLabel dobError = new JLabel();
        dobError.setForeground(Color.RED);
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        formPanel.add(dobError, gbc);
        errorLabels.put("DOB", dobError);
        row++;

        // Account Type
        String[] types = { "Savings", "Current", "Fixed Deposit", "Student", "Joint" };
        accountTypeBox = new JComboBox<>(types);
        addRow(formPanel, gbc, "Account Type:", accountTypeBox, row++);

        // Branch
        String[] branches = { "Kampala", "Gulu", "Mbarara", "Jinja", "Mbale" };
        branchBox = new JComboBox<>(branches);
        addRow(formPanel, gbc, "Branch:", branchBox, row++);

        // Opening Deposit
        addRow(formPanel, gbc, "Opening Deposit (UGX):", depositField = new JTextField(12), row++);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitBtn = new JButton("Submit");
        JButton resetBtn = new JButton("Reset");
        buttonPanel.add(submitBtn);
        buttonPanel.add(resetBtn);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);
        row++;

        // Summary area
        summaryArea = new JTextArea(10, 60);
        summaryArea.setEditable(false);
        summaryArea.setBorder(BorderFactory.createTitledBorder("Account Summary is Below:"));
        JScrollPane scroll = new JScrollPane(summaryArea);

        add(formPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        // Event listeners
        monthBox.addActionListener(e -> updateDays());
        accountTypeBox.addActionListener(e -> updateDepositHint());

        submitBtn.addActionListener(e -> handleSubmit());
        resetBtn.addActionListener(e -> resetForm());

        setVisible(true);
    }

    // Helper to add a row: label, field, and an error label
    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 2;
        panel.add(errorLabel, gbc);
        errorLabels.put(label, errorLabel); // use label as key
    }

    // Overloaded for combo boxes
    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JComboBox<?> combo, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(combo, gbc);
        JLabel errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 2;
        panel.add(errorLabel, gbc);
        errorLabels.put(label, errorLabel);
    }

    private void populateDOB() {
        int currentYear = Year.now().getValue();
        for (int y = currentYear; y >= 1900; y--)
            yearBox.addItem(String.valueOf(y));
        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        for (String m : months)
            monthBox.addItem(m);
        updateDays();
        // set defaults
        yearBox.setSelectedItem("2000");
        monthBox.setSelectedIndex(0);
        dayBox.setSelectedItem("1");
    }

    private void updateDays() {
        dayBox.removeAllItems();
        int year = Integer.parseInt((String) yearBox.getSelectedItem());
        int month = monthBox.getSelectedIndex() + 1;
        YearMonth ym = YearMonth.of(year, month);
        for (int d = 1; d <= ym.lengthOfMonth(); d++)
            dayBox.addItem(String.valueOf(d));
        dayBox.setSelectedItem("1");
    }

    private void updateDepositHint() {
        String selected = (String) accountTypeBox.getSelectedItem();
        if (selected != null && accountMap.containsKey(selected)) {
            double min = accountMap.get(selected).minimumDeposit();
            depositField.setToolTipText("Minimum: " + min);
        }
    }

    private void handleSubmit() {
        // Clear previous error labels
        errorLabels.values().forEach(l -> l.setText(""));
        // Reset borders (we'll set red border for fields with errors later)
        resetBorders();

        // Gather data
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String nin = ninField.getText().trim();
        String email = emailField.getText().trim();
        String confirmEmail = confirmEmailField.getText().trim();
        String phone = phoneField.getText().trim();
        String pin = pinField.getText().trim();
        String confirmPin = confirmPinField.getText().trim();
        String depositStr = depositField.getText().trim();

        // DOB
        int year = Integer.parseInt((String) yearBox.getSelectedItem());
        int month = monthBox.getSelectedIndex() + 1;
        int day = Integer.parseInt((String) dayBox.getSelectedItem());
        LocalDate dob = LocalDate.of(year, month, day);

        String accountType = (String) accountTypeBox.getSelectedItem();
        String branch = (String) branchBox.getSelectedItem();
        double deposit = 0;
        try {
            deposit = Double.parseDouble(depositStr);
        } catch (NumberFormatException ignored) {
        }

        // Validation
        Map<String, String> fieldErrors = new LinkedHashMap<>(); // field label -> error message

        if (!Validator.isValidName(firstName)) {
            fieldErrors.put("First Name:", "Must be 2-30 letters only.");
        }
        if (!Validator.isValidName(lastName)) {
            fieldErrors.put("Last Name:", "Must be 2-30 letters only.");
        }
        if (!Validator.isValidNIN(nin)) {
            fieldErrors.put("NIN:", "Exactly 10 uppercase alphanumeric characters.");
        }
        if (!Validator.isValidEmail(email)) {
            fieldErrors.put("Email:", "Invalid email format.");
        }
        if (!email.equals(confirmEmail)) {
            fieldErrors.put("Confirm Email:", "Must match Email.");
        }
        if (!Validator.isValidPhone(phone)) {
            fieldErrors.put("Phone (+256):", "Must be +256 followed by 9 digits.");
        }
        if (!Validator.isValidPIN(pin)) {
            fieldErrors.put("PIN:", "4-6 digits, not all identical.");
        }
        if (!pin.equals(confirmPin)) {
            fieldErrors.put("Confirm PIN:", "Must match PIN.");
        }

        // Age validation
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 18 || age > 75) {
            fieldErrors.put("DOB", "Age must be between 18 and 75.");
        }
        if ("Student".equals(accountType) && (age < 18 || age > 25)) {
            fieldErrors.put("DOB", "Student account requires age 18-25.");
        }

        if (accountType == null || accountType.isEmpty()) {
            fieldErrors.put("Account Type:", "Select an account type.");
        }
        if (branch == null || branch.isEmpty()) {
            fieldErrors.put("Branch:", "Select a branch.");
        }

        // Deposit minimum
        if (accountType != null && accountMap.containsKey(accountType)) {
            double min = accountMap.get(accountType).minimumDeposit();
            if (deposit < min) {
                fieldErrors.put("Opening Deposit (UGX):", "Minimum " + min + " for " + accountType + ".");
            }
        }

        // Display inline errors
        if (!fieldErrors.isEmpty()) {
            for (Map.Entry<String, String> e : fieldErrors.entrySet()) {
                JLabel label = errorLabels.get(e.getKey());
                if (label != null)
                    label.setText(e.getValue());
            }
            // Also highlight fields with red border (get field by key)
            highlightFields(fieldErrors);
            // Show summary dialog
            StringBuilder msg = new StringBuilder("Validation Errors:\n");
            for (String err : fieldErrors.values())
                msg.append("- ").append(err).append("\n");
            JOptionPane.showMessageDialog(this, msg.toString(), "Errors", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // All valid – save
        try {
            int currentYear = Year.now().getValue();
            String accNo = DatabaseHelper.generateAccountNumber(branch, currentYear);
            DatabaseHelper.saveAccount(accNo, firstName, lastName, nin, email, phone, pin, dob,
                    accountType, branch, deposit);

            String summary = String.format("ACC: %s | %s %s | %s | %s | DOB %s | %s | Deposit %.0f | %s",
                    accNo, firstName, lastName, accountType, branch,
                    dob.format(DateTimeFormatter.ISO_LOCAL_DATE), phone, deposit, email);
            summaryArea.setText(summary);
            JOptionPane.showMessageDialog(this, "Account created successfully!\n" + summary);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void highlightFields(Map<String, String> fieldErrors) {
        // Reset all borders first
        resetBorders();
        // Set red border for fields that have errors
        for (String key : fieldErrors.keySet()) {
            JComponent comp = getFieldComponent(key);
            if (comp != null)
                comp.setBorder(BorderFactory.createLineBorder(Color.RED));
        }
    }

    private JComponent getFieldComponent(String labelKey) {
        // Map from label text to component
        if (labelKey.equals("First Name:"))
            return firstNameField;
        if (labelKey.equals("Last Name:"))
            return lastNameField;
        if (labelKey.equals("NIN:"))
            return ninField;
        if (labelKey.equals("Email:"))
            return emailField;
        if (labelKey.equals("Confirm Email:"))
            return confirmEmailField;
        if (labelKey.equals("Phone (+256):"))
            return phoneField;
        if (labelKey.equals("PIN:"))
            return pinField;
        if (labelKey.equals("Confirm PIN:"))
            return confirmPinField;
        if (labelKey.equals("Opening Deposit (UGX):"))
            return depositField;
        if (labelKey.equals("Account Type:"))
            return accountTypeBox;
        if (labelKey.equals("Branch:"))
            return branchBox;
        // DOB is not a single component, we skip border for it
        return null;
    }

    private void resetBorders() {
        Component[] comps = { firstNameField, lastNameField, ninField, emailField, confirmEmailField,
                phoneField, pinField, confirmPinField, depositField, accountTypeBox, branchBox };
        for (Component c : comps) {
            if (c instanceof JComponent) {
                ((JComponent) c).setBorder(
                        UIManager.getBorder("TextField.border") != null ? UIManager.getBorder("TextField.border")
                                : BorderFactory.createLineBorder(Color.GRAY));
            }
        }
    }

    private void resetForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        ninField.setText("");
        emailField.setText("");
        confirmEmailField.setText("");
        phoneField.setText("");
        pinField.setText("");
        confirmPinField.setText("");
        depositField.setText("");
        yearBox.setSelectedItem("2000");
        monthBox.setSelectedIndex(0);
        updateDays();
        accountTypeBox.setSelectedIndex(0);
        branchBox.setSelectedIndex(0);
        summaryArea.setText("");
        errorLabels.values().forEach(l -> l.setText(""));
        resetBorders();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankForm::new);
    }
}