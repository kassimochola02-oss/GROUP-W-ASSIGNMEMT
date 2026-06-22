package model;

/**
 * Savings account type with minimum deposit of 50,000 UGX.
 */
public class SavingsAccount extends Account {
    public SavingsAccount() {
        type = "Savings";
        minDeposit = 50000;
    }

    @Override
    public double minimumDeposit() {
        return minDeposit;
    }
}