package model;

/**
 * Student account type with minimum deposit of 10,000 UGX.
 * Available for ages 18-25.
 */
public class StudentAccount extends Account {
    public StudentAccount() {
        type = "Student";
        minDeposit = 10000;
    }

    @Override
    public double minimumDeposit() {
        return minDeposit;
    }
}