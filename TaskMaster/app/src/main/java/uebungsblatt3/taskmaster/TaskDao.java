package uebungsblatt3.taskmaster;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


//Room basiert auf dem DAO (Data Access Object) Design Pattern
//Entitäten sind Java Klassen, mit 1:1 Mapping der Eigenschaften zu Tabellen-Spalten
//Im Dao werden die CRUD Methoden spezifiziert
@Dao
public interface TaskDao {

    @Query("SELECT * FROM Task")
    public List <Task> getAllTasks();

    @Insert
    public void addTask (Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM Task WHERE id=:position")
    Task getOneTask(int position);
}
