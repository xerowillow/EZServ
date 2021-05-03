package me.mineapi.ezserv.utils;

public class WhitelistPlayer {

    private String name;
    private String uuid;

    public WhitelistPlayer(String uuid, String name) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Player " + getName() + ", UUID: " + getUuid();
    }
}
