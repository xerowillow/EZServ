package me.mineapi.ezserv.utils;

import javafx.beans.property.SimpleStringProperty;

public class ServerProperty {
    SimpleStringProperty property;
    SimpleStringProperty value;

    public ServerProperty(String property, String value) {
        this.property = new SimpleStringProperty(property);
        this.value = new SimpleStringProperty(value);
    }

    public String getProperty() {
        return property.get();
    }

    public void setProperty(String property) {
        this.property.set(property);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
