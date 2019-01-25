package ru.levspb666.tamagotchi.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.levspb666.tamagotchi.model.Pet;

@Dao
public interface PetDao {
    @Query("SELECT * FROM pet")
    List<Pet> getAll();

    @Query("SELECT * FROM pet WHERE id IN (:petId)")
    List<Pet> loadAllById(long[] petId);

    @Query("SELECT * FROM pet WHERE id LIKE :id LIMIT 1")
    Pet findById(long id);

    @Query("UPDATE pet SET name = :name WHERE id = :id")
    void changePetName(long id, String name);

    @Query("UPDATE pet SET lvl = :lvl WHERE id = :id")
    void changePetLvl(long id, int lvl);

    @Insert
    long insert(Pet pet);

    @Update
    void update(Pet pet);

    @Delete
    void delete(Pet pet);
}
