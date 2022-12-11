package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DB.DBHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistenceAccountDAO implements AccountDAO {
    DBHandler DB;

    public PersistenceAccountDAO(DBHandler db){
        DB = db;
    }

    @Override
    public List<String> getAccountNumbersList() {
        return DB.getAccountNOs();
    }

    @Override
    public List<Account> getAccountsList() {
        return DB.getAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return DB.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        DB.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        DB.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = DB.getAccount(accountNo);

        switch (expenseType){
            case EXPENSE:
                DB.updateBalance(accountNo, account.getBalance() - amount);
                break;

            case INCOME:
                DB.updateBalance(accountNo, account.getBalance() + amount);
        }
    }
}
