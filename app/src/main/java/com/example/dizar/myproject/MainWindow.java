package com.example.dizar.myproject;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainWindow extends Activity {


    ListView listNotes;
    EditText noteFilter;
    DatabaseHelper databaseHelper;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    SQLiteDatabase db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        listNotes = (ListView) findViewById(R.id.list);
        noteFilter = (EditText) findViewById(R.id.search);
        listNotes.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        databaseHelper = new DatabaseHelper(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            db = databaseHelper.getReadableDatabase();
            userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE, null);
            String[] headers = new String[]{DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_DATETIME};
            userAdapter = new SimpleCursorAdapter(this, R.layout.two_line,
                    userCursor, headers, new int[]{R.id.text1, R.id.text2}, 0);
            if(!noteFilter.getText().toString().isEmpty()){
                userAdapter.getFilter().filter(noteFilter.getText().toString());
            }
            noteFilter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    userAdapter.getFilter().filter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            userAdapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {
                    if (constraint == null || constraint.length() == 0){
                        return db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE, null);
                    }
                    else {
                        return db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE + " WHERE " +
                                DatabaseHelper.COLUMN_TITLE + " LIKE ?", new String[]{"%" + constraint.toString() + "%"});
                    }
                }
            });
            listNotes.setAdapter(userAdapter);
        }
        catch (SQLException ex){}

    }

    public void add(View view){
        Intent intent = new Intent(this, UsersActivity.class);
        startActivity(intent);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
        userCursor.close();
    }
}
