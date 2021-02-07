package uebungsblatt3.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//Zurück-Button mittels parentActivity MainActivity im Manifest

public class ManageTaskActivity extends AppCompatActivity {
    //AppCompatActivity: Basis Klasse von Activitys, die neue Features auf älteren Android Devicen verwenden wollen
    //Androidx stellt backward-compatibilität sicher, bietet weitere features und Libraries
    //android für die vorwärtskompatibilität

    Intent intent;
    AppDatabase database;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Zugriff auf den Code der Elternklasse
        setContentView(R.layout.activity_manage_task); //Basis von jeder Activity, auf dem baut alles weitere auf -- Root View -- Body Tag bei html

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "task_db")
                .allowMainThreadQueries() //Deaktiviert die Hauptthread Abfrage für Room - Room stellt nämlich sicher, dass auf die DB im Hauptthread niemals zugegriffen wird, da dies den Hauptthread sperren und eine ANR auslösen kann
                .build();

        intent = getIntent();
        position = -1;

        if(intent.hasExtra("selectedTask")){
            getParameters();
        }
    }

    private void getParameters() {
        position = intent.getIntExtra("selectedTask", 0);
        Task task = database.getTaskDao().getAllTasks().get(position);

        EditText editText = findViewById(R.id.editText_title);
        editText.setText(task.title);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.dueDate);
        DatePicker datePicker = findViewById(R.id.datePicker);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Spinner spinner = findViewById(R.id.spinner_priority);
        spinner.setSelection(task.priority);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Optionsmenü
        MenuInflater inflater = getMenuInflater(); //Objekt, welches ein Menü-Item anhand eines XML erstellt
        inflater.inflate(R.menu.save, menu); //macht das Item sichtbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Methode zum Handeln von Click Events im Menü
        if(item.getItemId() == R.id.save){ //Wenn auf den Save Button geklickt wird

            Task task = new Task();

            if(position < 0){ //Wenn Task noch nicht existiert
                configTask(task); //Task-Daten setzen
                database.getTaskDao().addTask(task); //speichern in der DB
            }

            else{
                task = database.getTaskDao().getAllTasks().get(position); //ausgewählten Task laden
                configTask(task); //neuen Daten setzen
                database.getTaskDao().updateTask(task); //Task updaten
            }

            goBack(); //zurück zur MainActivity
        }
        return super.onOptionsItemSelected(item);
    }

    private void configTask(Task task) {
        EditText editTextTitle = findViewById(R.id.editText_title);
        Spinner spinnerPriority = findViewById(R.id.spinner_priority);
        DatePicker datePicker = findViewById(R.id.datePicker);

        setTitle(task, editTextTitle);

        setPriority(task, spinnerPriority);

        setDueDate(task, datePicker);
    }

    private void setDueDate(Task task, DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();
        Date date = new GregorianCalendar(year,month,day).getTime();
        task.dueDate = date;
    }

    private void setPriority(Task task, Spinner spinnerPriority) {
        int pos = spinnerPriority.getSelectedItemPosition();
        task.priority = pos;
    }

    private void setTitle(Task task, EditText editTextTitle) {
        String title = editTextTitle.getText().toString();

        if(title.equals("")) {
            task.title = "KEIN TITEL";
        }
        else{
            task.title = title;
        }
    }

    private void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}