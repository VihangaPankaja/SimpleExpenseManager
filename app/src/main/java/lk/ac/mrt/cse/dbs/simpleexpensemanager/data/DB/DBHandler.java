package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHandler extends SQLiteOpenHelper {
    public DBHandler(Context context){
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table account(accountNo text , bank text, accountHolder text, balance real);");
        db.execSQL("create table _transaction(date text, accountNo text, type text, amount real);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists account");
        db.execSQL("drop table if exists _transaction");
    }

    public void addAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bank", account.getBankName());
        contentValues.put("accountHolder", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());

        db.insert("account", null, contentValues);
        db.close();
    }

    public void addTransaction(Transaction transaction){
        String et = (transaction.getExpenseType() == ExpenseType.EXPENSE) ? "expense": "income";

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        DateFormat df = new SimpleDateFormat("dd-mm-yyyy");

        contentValues.put("date", df.format(transaction.getDate()));
        contentValues.put("accountNo", transaction.getAccountNo());
        contentValues.put("type", et);
        contentValues.put("amount", transaction.getAmount());

        db.insert("_transaction", null, contentValues);
        db.close();
    }

    public ArrayList<Account> getAccounts(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from account", null);
        ArrayList<Account> accounts = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                accounts.add(new Account(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3)
                ));
            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accounts;
    }

    public ArrayList<Transaction> getTransactions(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from _transaction", null);
        ArrayList<Transaction> transactions = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                try {
                    transactions.add(new Transaction(
                            new SimpleDateFormat("dd-mm-yyyy").parse(cursor.getString(0)),
                            cursor.getString(1),
                            (cursor.getString(2) == "expense") ? ExpenseType.EXPENSE: ExpenseType.INCOME,
                            cursor.getDouble(3)
                    ));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return transactions;
    }

    public ArrayList<String> getAccountNOs(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select accountNo from account", null);
        ArrayList<String> accountNo = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                accountNo.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return accountNo;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from account where accountNo='" + accountNo+"'", null);

        Account account;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
        } else {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        cursor.close();
        db.close();

        return account;
    }

    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("account", "accountNo = ?", new String[]{accountNo});
        db.close();
    }

    public void updateBalance(String account, double balance){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update account set balance = " + String.valueOf(balance) + " where accountNo = '" + account +"'");
        db.close();
    }
}
