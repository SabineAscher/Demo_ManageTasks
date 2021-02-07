package uebungsblatt3.taskmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.WindowInsetsAnimation;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskListener {
    //AppCompatActivity: Basis Klasse von Activitys, die neue Features auf älteren Android Devicen verwenden wollen
    //Androidx stellt backward-compatibilität sicher, bietet weitere features und Libraries
    //android für die vorwärtskompatibilität

    AppDatabase database;
    TaskAdapter taskAdapter;
    protected List<Task> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//Zugriff auf den Code der Elternklasse
        setContentView(R.layout.activity_main);//Basis von jeder Activity, auf dem baut alles weitere auf -- Root View -- Body Tag bei html

        //Room ist eine Abstraktion der SQLite Datenbank, ähnlich ORM und vereinfacht das Arbeiten mit SQL Datenbanken
        //Room muss als dependency in gradle.build
        //Room arbeitet mit Annotationen
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "task_db")
                .allowMainThreadQueries() ////Deaktiviert die Hauptthread Abfrage für Room - Room stellt nämlich sicher, dass auf die DB im Hauptthread niemals zugegriffen wird, da dies den Hauptthread sperren und eine ANR auslösen kann
                .build();


        //RecyclerView -- Verbindung zwischen Darstellung von Elementen (Layout) und Datenhaltung (Adapter)
        RecyclerView taskList = findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(this)); //Konfig. der RV durch LayoutManager, Zuständig für Anordnung der Elemente. Ein Container fragt bei einer Neudarstellung immer den LM, wie er seine Elemente anordnen soll
        taskAdapter = new TaskAdapter(database.getTaskDao(), this); //this referenziert auf das Click Interface, muss mitgegeben werden weil vorgegeben im Konstruktor
        taskList.setAdapter(taskAdapter);
        taskAdapter.loadTasks();

        //Trennlinien zwischen den einzelnen Tasks
        taskList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        //Klasse um swipe und drag&drop  der RV hinzuzufügen
        //Konfig. auf welche Arten von Interaktionen reagiert wird und wie
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                // Position in der Datenquelle abfragen
                int position = viewHolder.getAdapterPosition();
                Task myTask = taskAdapter.getTaskAtPosition(position); //liefert Task an der bestimmten position in der Liste

                // löschen durchführen, dazu muss im Adapter eine Löschen Operation implementiert werden
                taskAdapter.deleteTask(myTask);

                // Nachdem Löschen in der Datenbank müssen die Daten im Adapter neu geladen werden
                // ansonsten laden sich die Einträge in der RecyclerView nicht neu
                // nach dem Neuladen muss im Adapter die Methode notifyDataSetChanged(); aufgerufen werden

                //Coordinator Layout für Snackbar --> für Positionierung an oberster Ebene
                CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
                //ConstraintLayout constLayout = findViewById(R.id.Constraint);
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "This task has been deleted", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        // Instanz der RecyclerView übergeben
        touchHelper.attachToRecyclerView(taskList);
    }

    public void newTask(View view){
        Intent intent = new Intent(this, ManageTaskActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onTaskClick(int position) {
        Intent intent = new Intent(MainActivity.this, ManageTaskActivity.class);
        intent.putExtra("selectedTask", position);
        startActivity(intent);
    }
}