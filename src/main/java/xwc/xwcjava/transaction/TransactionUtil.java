package xwc.xwcjava.transaction;

import xwc.xwcjava.operation.IOperation;

import java.util.ArrayList;
import java.util.List;

public class TransactionUtil {
    public static Transaction defaultTransaction() {
        Transaction tx = new Transaction();
        tx.setRefBlockNum(0);
        tx.setRefBlockPrefix(0);
        tx.setExpiration("");
        tx.setOperations(new ArrayList<List<Object>>());
        tx.setExtensions(new ArrayList<Object>());
        tx.setSignatures(new ArrayList<String>());
        tx.setTransientExpiration(0);
        tx.setTransientOperations(new ArrayList<IOperation>());
        return tx;
    }
}
