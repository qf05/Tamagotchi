package ru.levspb666.tamagotchi.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

// https://habr.com/post/336196/
@Entity(foreignKeys = @ForeignKey(
        onDelete = ForeignKey.CASCADE,
        entity = Pet.class,
        parentColumns = "id",
        childColumns = "pet_id"),
        indices = {@Index("pet_id")})
public class History {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long date;
    @ColumnInfo(name = "action_type")
    private String action;
    @ColumnInfo(name = "pet_id")
    private long petId;

    public History() {
    }

    @Ignore
    public History(long date, String type, long petId) {
        this.date = date;
        this.action = type;
        this.petId = petId;
    }

    public long getDate() {
        return date;
    }

    public String getAction() {
        return action;
    }

    public long getPetId() {
        return petId;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPetId(long petId) {
        this.petId = petId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        History history = (History) o;
        return date == history.date &&
                petId == history.petId &&
                Objects.equals(action, history.action);
    }

    @Override
    public int hashCode() {

        return Objects.hash(date, action, petId);
    }

    @Override
    public String toString() {
        return "History{" +
                "date=" + date +
                ", type=" + action +
                ", petId=" + petId +
                '}';
    }
}
