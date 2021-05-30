package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.Person;

import java.util.List;

public class PersonData extends CachedData {
    public PersonData() {
        isVisible = new boolean[2];
        labelValue = new Person[2];
        exclusiveTimestamp = new long[2];
    }

    public CachedData initialize() {
        PersonData data = MainScreenService.getInstance().getPersonData();
        for (int id = 0; id < 2; id++) {
            synchronized (labelsLock[id]) {
                timestamp = Math.max(data.getTimestamp(id), timestamp);
                this.exclusiveTimestamp[id] = data.getTimestamp(id);
                this.isVisible[id] = data.isVisible(id);
                this.labelValue[id] = data.getLabelValue(id) == null ? new Person("", "") : data.getLabelValue(id);
            }
        }
        this.delay = data.delay;

        return this;
    }

    private void recache() {
        Data.cache.refresh(PersonData.class);
    }

    public void hide() {
        for (int id = 0; id < labelsLock.length; id++) {
            synchronized (labelsLock[id]) {
                isVisible[id] = false;
                exclusiveTimestamp[id] = System.currentTimeMillis();
                timestamp = Math.max(timestamp, exclusiveTimestamp[id]);
            }
        }
        recache();
    }

    public String checkOverlays() {
//        if (MainScreenData.getMainScreenData().teamData.isVisible) {
//            return MainScreenData.getMainScreenData().teamData.getOverlayError();
//        }
        return null;
    }

    public String setLabelVisible(boolean visible, Person label, int id) {
        if (visible) {
            String outcome = checkOverlays();
            if (outcome != null) {
                return outcome;
            }
        }
        //System.err.println("Set visible " + visible + " " + labelsValues[id] + " " + label);
        synchronized (labelsLock[id]) {
            exclusiveTimestamp[id] = System.currentTimeMillis();
            timestamp = exclusiveTimestamp[id];
            isVisible[id] = visible;
            labelValue[id] = label;
        }
        recache();
        return null;
    }

    public void update() {
        boolean change = false;
//        System.err.println(labelsTimestamps[0] + " " + timeToShow + " " + System.currentTimeMillis());
        for (int id = 0; id < 2; id++) {
            synchronized (labelsLock[id]) {
                if (exclusiveTimestamp[id] + MainScreenService.getProperties().getPersonProperties().getTimeToShow() < System.currentTimeMillis()) {
                    isVisible[id] = false;
                    change = true;
                    timestamp = System.currentTimeMillis();
                }
            }
        }
        if (change) {
            recache();
        }
    }

    public String labelsStatus() {
        String result = "";
        for (int i = 0; i < labelsLock.length; i++) {
            synchronized (labelsLock[i]) {
                result += getTimestamp(i) + "\n" + isVisible(i) + "\n" + getLabelValue(i).toString() + " " + "\n";
            }
        }
        return result;
    }

    public String labelStatus(int id) {
        synchronized (labelsLock[id]) {
            if (getLabelValue(id) == null) {
                setLabelVisible(false, null, id);
            }
            return getTimestamp(id) + "\n" + isVisible(id) + "\n" + (getLabelValue(id) != null ? getLabelValue(id).getName() : "");
        }
    }

    public long getTimestamp(int id) {
        synchronized (labelsLock[id]) {
            return exclusiveTimestamp[id];
        }
    }

    public boolean isVisible(int id) {
        synchronized (labelsLock[id]) {
            return isVisible[id];
        }
    }

    public Person getLabelValue(int id) {
        synchronized (labelsLock[id]) {
            return labelValue[id];
        }
    }

    public List<Person> getContainer() {
        return MainScreenService.getProperties().getPersonProperties().getBackUp().getData();
    }

    public void removePerson(Person person) {
        MainScreenService.getProperties().getPersonProperties().getBackUp().removeItem(person);
    }

    public void addPerson(Person person) {
        MainScreenService.getProperties().getPersonProperties().getBackUp().addItem(person);
    }

    final private Object[] labelsLock = {new Object(), new Object()};

    public long[] exclusiveTimestamp;
    public boolean[] isVisible;
    public Person[] labelValue;
}
