package org.icpclive.webadmin.mainscreen.properties;

import org.icpclive.webadmin.backup.BackUp;
import org.icpclive.webadmin.mainscreen.Person;

import java.util.Properties;

public class PersonProperties {
    private final BackUp<Person> backUp;
    private final String backUpFileName;
    private final long timeToShow;

    public PersonProperties(final Properties properties, final long latency) {
        this.backUpFileName = properties.getProperty("backup.persons");
        this.backUp = new BackUp<>(backUpFileName, Person.class);
        this.timeToShow = Long.parseLong(properties.getProperty("person.time")) + latency;
    }

    public BackUp<Person> getBackUp() {
        return backUp;
    }

    public String getBackUpFileName() {
        return backUpFileName;
    }

    public long getTimeToShow() {
        return timeToShow;
    }

}
