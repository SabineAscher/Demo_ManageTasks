package uebungsblatt3.taskmaster;

import androidx.room.TypeConverter;

import java.util.Date;

public class RoomConverter {


        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }
        //if-else
        //value = null ist die Bedingung
        // wenn(?) true dann null sonst new Date

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    //if-else
    //date = null ist die Bedingung
    // wenn(?) true dann null sonst date.getTime()

}
