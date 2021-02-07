package uebungsblatt3.taskmaster;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Task.class}, version = 1)
@TypeConverters(RoomConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TaskDao getTaskDao();
}

//Datenbank wird abstrakt implementiert und erbt von RoomDatabase
//Alle Enitäten werden angegebenen --> entitie = {Task.class}
//Version der Datenbank wird mitangegeben
