package ru.levspb666.tamagotchi.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import ru.levspb666.tamagotchi.dao.PetDao;
import ru.levspb666.tamagotchi.model.Pet;

// http://qaru.site/questions/152627/room-schema-export-directory-is-not-provided-to-the-annotation-processor-so-we-cannot-export-the-schema
@Database(entities = {Pet.class}, version = 1, exportSchema = false)
public abstract class DataBase extends RoomDatabase {

    private static DataBase DB_INSTANCE;

    public abstract PetDao petDao();
// http://qaru.site/questions/14467653/android-room-persistent-appdatabaseimpl-does-not-exist

    // миграция базы
    // https://startandroid.ru/ru/courses/dagger-2/27-course/architecture-components/540-urok-12-migracija-versij-bazy-dannyh.html
    public static DataBase getAppDatabase(Context context) {
        if (DB_INSTANCE == null) {
            DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DataBase.class, "database-name")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            //Room.inMemoryDatabaseBuilder(context.getApplicationContext(),AppDatabase.class).allowMainThreadQueries().build();
        }
        return DB_INSTANCE;
    }

    public static void destroyInstance() {
        DB_INSTANCE = null;
    }
}
