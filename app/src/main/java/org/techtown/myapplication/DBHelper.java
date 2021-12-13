package org.techtown.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Tododata.db";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS TodoList (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, timer TEXT NOT NULL, number TEXT NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    //SELECT
    public ArrayList<TodoItem> getTodoList(String textview){
        ArrayList<TodoItem> todoItems = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoList ORDER BY id DESC", null);
        if(cursor.getCount() !=0){
            while(cursor.moveToNext()){
                int id= cursor.getInt(cursor.getColumnIndex("id"));
                String title= cursor.getString(cursor.getColumnIndex("title"));
                String timer= cursor.getString(cursor.getColumnIndex("timer"));
                String number= cursor.getString(cursor.getColumnIndex("number"));
                if(number.equals(textview) == true) {
                    TodoItem todoItem = new TodoItem();
                    todoItem.setId(id);
                    todoItem.setTitle(title);
                    todoItem.setTimer(timer);
                    todoItem.setNumber(number);
                    todoItems.add(todoItem);
                }
            }
        }
        cursor.close();
        return todoItems;
    }

    //Insert문 (할일 목록 데이터에 넣음)
    public void InsertTodo(String _title, String _timer, String _number){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO TodoList (title, timer, number) VALUES('" + _title + "','"+ _timer + "','"+ _number + "');");
    }

    //Update
    public void UpdateTodo(String _title, String _timer, String _beforetitle, String _beforetimer, String _number){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TodoList SET title='" + _title + "',timer = '"+ _timer + "' WHERE title = '" + _beforetitle +"' AND timer ='" + _beforetimer + "' AND number = '" + _number + "'");
    }

    //Delete
    public void deleteTodo(String _title, String _timer, String _number){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM TodoList WHERE title = '" + _title + "' AND timer = '" + _timer + "' AND number = '" + _number + "'");
    }
}
