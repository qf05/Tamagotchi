package ru.levspb666.tamagotchi.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

import ru.levspb666.tamagotchi.enums.PetsType;
import ru.levspb666.tamagotchi.utils.AlarmUtils;

//http://qaru.site/questions/16042145/sqlite-requires-having-a-unique-constraint-android-room-annotations
@Entity(tableName = "pet",
        indices = {@Index(value = "id", unique = true)})
public class Pet implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String type;
    private int lvl;
    private int hp;
    private int satiety;
    private int experience;
    @ColumnInfo(name = "is_live")
    private boolean isLive;
    @ColumnInfo(name = "is_ill")
    private boolean isIll;
    @ColumnInfo(name = "is_slip")
    private boolean isSlip;
    @ColumnInfo(name = "next_walk")
    private long nextWalk;
    @ColumnInfo(name = "next_sleep")
    private long nextSlip;
    @ColumnInfo(name = "next_shit")
    private long nextShit;
    @ColumnInfo(name = "wake_up")
    private long wakeUp;

    public Pet() {
    }

    @Ignore
    public Pet(long id, String name, String type, int lvl, int hp, int satiety, int experience, boolean live, boolean isIll, boolean isSlip, long nextWalk, long nextSlip, long nextShit, long wakeUp) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.lvl = lvl;
        this.hp = hp;
        this.satiety = satiety;
        this.experience = experience;
        this.isLive = live;
        this.isIll = isIll;
        this.isSlip = isSlip;
        this.nextWalk = nextWalk;
        this.nextSlip = nextSlip;
        this.nextShit = nextShit;
        this.wakeUp = wakeUp;
    }

    @Ignore
    public Pet(String name, PetsType type) {
        this.name = name;
        this.type = type.toString();
        this.lvl = 1;
        this.hp = 100;
        this.satiety = 90;
        this.experience = 0;
        this.isLive = true;
        this.isIll = false;
        this.isSlip = false;
        this.nextWalk = AlarmUtils.nextWalk();
        this.nextSlip = AlarmUtils.nextSleep();
        this.nextShit = AlarmUtils.nextShit();
        this.wakeUp = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getSatiety() {
        return satiety;
    }

    public void setSatiety(int satiety) {
        this.satiety = satiety;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        this.isLive = live;
    }

    public boolean isIll() {
        return isIll;
    }

    public void setIll(boolean ill) {
        isIll = ill;
    }

    public boolean isSlip() {
        return isSlip;
    }

    public void setSlip(boolean slip) {
        isSlip = slip;
    }

    public long getNextWalk() {
        return nextWalk;
    }

    public void setNextWalk(long nextWalk) {
        this.nextWalk = nextWalk;
    }

    public long getNextSlip() {
        return nextSlip;
    }

    public void setNextSlip(long nextSlip) {
        this.nextSlip = nextSlip;
    }

    public long getNextShit() {
        return nextShit;
    }

    public void setNextShit(long nextShit) {
        this.nextShit = nextShit;
    }

    public long getWakeUp() {
        return wakeUp;
    }

    public void setWakeUp(long wakeUp) {
        this.wakeUp = wakeUp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id == pet.id &&
                lvl == pet.lvl &&
                hp == pet.hp &&
                satiety == pet.satiety &&
                experience == pet.experience &&
                isLive == pet.isLive &&
                isIll == pet.isIll &&
                isSlip == pet.isSlip &&
                nextWalk == pet.nextWalk &&
                nextSlip == pet.nextSlip &&
                nextShit == pet.nextShit &&
                wakeUp == pet.wakeUp &&
                Objects.equals(name, pet.name) &&
                type.equals(pet.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, type, lvl, hp, satiety, experience, isLive, isIll, isSlip, nextWalk, nextSlip, nextShit, wakeUp);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", lvl=" + lvl +
                ", hp=" + hp +
                ", satiety=" + satiety +
                ", experience=" + experience +
                ", isLive=" + isLive +
                ", isIll=" + isIll +
                ", isSlip=" + isSlip +
                ", nextWalk=" + nextWalk +
                ", nextSlip=" + nextSlip +
                ", nextShit=" + nextShit +
                ", wakeUp=" + wakeUp +
                '}';
    }
}
