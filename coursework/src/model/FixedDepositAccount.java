package model;

/**
 * Fixed deposit account type with minimum deposit of 1,000,000 UGX.
 */
public class FixedDepositAccount extends Account {
    public FixedDepositAccount() {
        type = "Fixed Deposit";
        minDeposit = 1000000;
    }

    @Override
    public double minimumDeposit() {
        return minDeposit;
    }
}