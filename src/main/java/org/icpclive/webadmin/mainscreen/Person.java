package org.icpclive.webadmin.mainscreen;

public class Person {
    private String name;
    private String position;

    public Person(final String name, final String position) {
        this.name = name;
        this.position = position;
    }


    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(final String position) {
        this.position = position;
    }
}
