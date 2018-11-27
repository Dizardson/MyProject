package com.example.dizar.myproject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static java.util.Calendar.*;


public class UsersActivity extends Activity {
    private static int event;
    public static String TEXT;
    public static String TITLE;

    EditText titleBox;
    EditText textBox;
    TextView dateBox;
    Button saveButton;
    Button deleteButton;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    long noteId = 0;
    Calendar dateAndTime = getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        event = 0 ;
        titleBox = (EditText) findViewById(R.id.title);
        textBox = (EditText) findViewById(R.id.text);
        dateBox = (TextView) findViewById(R.id.date);
        saveButton = (Button) findViewById(R.id.savebutton);
        deleteButton = (Button) findViewById(R.id.deletebutton);
        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();
        Bundle extars = getIntent().getExtras();
        if (extars != null) {
            noteId = extars.getLong("id");
        }
        if (noteId > 0){
            userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE + " WHERE " +
                                    DatabaseHelper.COLUMN_ID + " =?", new String[]{String.valueOf(noteId)});
            userCursor.moveToFirst();
            titleBox.setText(userCursor.getString(1));
            textBox.setText(userCursor.getString(2));
            dateBox.setText(userCursor.getString(3));
            userCursor.close();
        } else {
            deleteButton.setVisibility(View.GONE);
        }
    }

    public void save(View view){
        if (titleBox.getText().length() != 0) {
            if (textBox.getText().length() != 0) {
                ContentValues cv = new ContentValues();

                cv.put(DatabaseHelper.COLUMN_TITLE, titleBox.getText().toString());
                cv.put(DatabaseHelper.COLUMN_TEXT, textBox.getText().toString());
                cv.put(DatabaseHelper.COLUMN_DATETIME, dateBox.getText().toString());
                if (noteId > 0) {
                    db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(noteId), null);
                } else {
                    db.insert(DatabaseHelper.TABLE, null, cv);
                }
                gohome();
            } else {
                Toast toastText = Toast.makeText(this, getString(R.string.stringText), Toast.LENGTH_LONG);
                toastText.setGravity(Gravity.CENTER, 0, 0);
                toastText.show();
            }
        } else {
            Toast toastTitle = Toast.makeText(this, getString(R.string.stringTitle), Toast.LENGTH_LONG);
            toastTitle.setGravity(Gravity.CENTER, 0, 0);
            toastTitle.show();
        }
        if (event == 1) {
            TITLE = titleBox.getText().toString();
            TEXT = textBox.getText().toString();
            startAlarm(dateAndTime);
        }
    }
    public void delete(View view){
        db.delete(DatabaseHelper.TABLE, "_id =?", new String[]{String.valueOf(noteId)});
        cancelAlarm();
        gohome();
    }
    private void gohome(){
        db.close();
        Intent intent = new Intent(this, MainWindow.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
    public void setAll(View view){
        event = 1;
        setTime(view);
        setDate(view);
    }

    public void setDate(View view){
        new DatePickerDialog(UsersActivity.this, d,
                dateAndTime.get(YEAR),
                dateAndTime.get(MONTH),
                dateAndTime.get(DAY_OF_MONTH)).show();
    }
    public void setTime(View view){
        new TimePickerDialog(UsersActivity.this, t,
                dateAndTime.get(HOUR_OF_DAY),
                dateAndTime.get(MINUTE), true).show();
    }

    private void setInitialDateTime() {
        dateBox.setText(DateUtils.formatDateTime(this, dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_TIME));
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(MINUTE, minute);
            dateAndTime.set(SECOND, 0);
           // updateTimeText(dateAndTime);
            setInitialDateTime();
        }
    };

    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    /*public void updateTimeText(Calendar dateAndTime){
        String timeText ="Alarm set for: ";
        timeText += java.text.DateFormat.getTimeInstance().format(dateAndTime.getTime());
    }*/
    private void startAlarm(Calendar dateAndTime){

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(this,0,  intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateAndTime.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}
