package ru.levspb666.tamagotchi.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.levspb666.tamagotchi.model.History;


@Dao
public interface HistoryDao {

    @Query("SELECT * FROM history")
    List<History> getAll();

    @Query("SELECT * FROM history WHERE pet_id = :petId")
    List<History> loadAllById(int petId);

    @Query("SELECT * FROM history WHERE action_type IN (:action)")
    List<History> loadAllByAction(String... action);

    @Insert
    long insert(History history);

    @Delete
    void delete(History history);
}
