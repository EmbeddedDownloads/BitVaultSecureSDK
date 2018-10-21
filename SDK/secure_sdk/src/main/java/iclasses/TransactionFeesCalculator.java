package iclasses;

/**
 * Created by vvdn on 11/21/2017.
 */

public interface TransactionFeesCalculator {
    public void transactionFees(long fees);

    public void transacionFeesCalculatorFailed(String error);
}
