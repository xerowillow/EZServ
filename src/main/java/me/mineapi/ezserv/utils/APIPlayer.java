package me.mineapi.ezserv.utils;

public class APIPlayer {

    private String name;
    private String id;

    public APIPlayer(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Player " + getName() + ", UUID: " + getId();
    }
}
