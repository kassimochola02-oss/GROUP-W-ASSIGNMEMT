package model;

/**
 * Joint account type with minimum deposit of 100,000 UGX.
 */
public class JointAccount extends Account {
    public JointAccount() {
        type = "Joint";
        minDeposit = 100000;
    }

    @Override
    public double minimumDeposit() {
        return minDeposit;
    }
}