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

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBHandler extends SQLiteOpenHelper {
    public DBHandler(Context context){
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table account(accountNo text primary key, bank text, accountHolder text, balance real);");
        db.execSQL("create table _transaction(date Date, accountNo text, type text, amount real);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists account");
        db.execSQL("drop table if exists _transaction");
    }

    public void addAccount(String accountNo, String bank, String accountHolder, double balance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("accountNo", accountNo);
        contentValues.put("bank", bank);
        contentValues.put("accountHolder", accountHolder);
        contentValues.put("balance", balance);

        db.insert("account", null, contentValues);
        db.close();
    }

    public void addTransaction(Date date, String accountNo, ExpenseType expenseType, double amount){
        String et = (expenseType == ExpenseType.EXPENSE) ? "expense": "income";

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        DateFormat df = new SimpleDateFormat("dd-mm-yyyy");

        contentValues.put("date", df.format(date));
        contentValues.put("accountNo", accountNo);
        contentValues.put("type", et);
        contentValues.put("amount", amount);

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
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)
                ));
            }while (cursor.moveToNext());
        }

        cursor.close();
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
                            new SimpleDateFormat("dd-mm-yyyy").parse(cursor.getString(1)),
                            cursor.getString(2),
                            (cursor.getString(3) == "expense") ? ExpenseType.EXPENSE: ExpenseType.INCOME,
                            cursor.getDouble(4)
                    ));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return transactions;
    }

    public void updateBalance(String account, double balance){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "update accout set balance = " + String.valueOf(balance) + "where accountNo = " + account,
                null);
    }
}
