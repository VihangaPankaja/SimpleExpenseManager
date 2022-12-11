package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DB.DBHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistenceTransactionDAO implements TransactionDAO {
    DBHandler DB;

    public PersistenceTransactionDAO(DBHandler db){
        DB = db;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        DB.addTransaction(transaction);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return DB.getTransactions();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> tmp = DB.getTransactions();
        int size = tmp.size();
        if (size <= limit){
            return tmp;
        }
        return tmp.subList(size - limit, size);
    }
}
