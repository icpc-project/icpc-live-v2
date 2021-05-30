package org.icpclive.webadmin.mainscreen.caption;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.mainscreen.Person;
import org.icpclive.webadmin.utils.DemonstratorController;

import java.util.ArrayList;
import java.util.List;

public class PersonController extends DemonstratorController<Person> {

    public PersonController() {
        super(new String[]{"left person", "right person"},new String[]{"Name", "Caption"}, Person.class, 2, MainScreenService.getInstance().getPersonData().getContainer());
    }

    @Override
    protected void recache() {
        //
    }

    @Override
    protected void setItemValue(final Person item, final List<String> values) {
        item.setName(values.get(0));
        item.setPosition(values.get(1));
    }

    @Override
    protected void removeItem(final Person item) {
        MainScreenService.getInstance().getPersonData().removePerson(item);
    }

    @Override
    protected void addItem(final List<String> values) {
        MainScreenService.getInstance().getPersonData().addPerson(new Person(values.get(0), values.get(1)));
    }

    @Override
    protected List<String> getTextFieldValuesFromItem(final Person item) {
        List<String> fields = new ArrayList<>();
        fields.add(item.getName());
        fields.add(item.getName());
        return fields;
    }

    @Override
    protected String getStatus(final int id) {
        String[] statusState = MainScreenService.getInstance().getPersonData().labelStatus(id).split(System.lineSeparator());
        return "true".equals(statusState[1]) ? "Show " + statusState[2] : "Nothing is shown";
    }

    @Override
    protected String setVisible(final boolean visible, final Person item, int id) {
        return MainScreenService.getInstance().getPersonData().setLabelVisible(visible, item, id);
    }
}
