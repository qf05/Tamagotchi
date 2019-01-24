package ru.levspb666.tamagotchi.model;

import java.util.Objects;

public class History {
    private long id;
    private long date;
    private String action;
    private long petId;

    public History() {
    }

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
