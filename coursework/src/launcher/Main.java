package launcher;

import javax.swing.SwingUtilities;

import ui.BankForm;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankForm::new);
    }
}