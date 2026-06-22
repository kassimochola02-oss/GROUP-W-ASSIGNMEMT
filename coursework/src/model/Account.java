package model;

/**
 * Abstract base class for bank account types.
 */
public abstract class Account {
    protected String type;
    protected double minDeposit;

    /**
     * Gets the account type.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the minimum deposit required for this account type.
     */
    public abstract double minimumDeposit();
}