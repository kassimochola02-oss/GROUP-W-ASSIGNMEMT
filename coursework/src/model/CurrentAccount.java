package model;

/**
 * Current account type with minimum deposit of 200,000 UGX.
 */
public class CurrentAccount extends Account {
    public CurrentAccount() {
        type = "Current";
        minDeposit = 200000;
    }

    @Override
    public double minimumDeposit() {
        return minDeposit;
    }
}