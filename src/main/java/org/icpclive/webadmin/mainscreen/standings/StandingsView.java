package org.icpclive.webadmin.mainscreen.standings;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.webadmin.utils.RefreshableContent;

public class StandingsView extends VerticalLayout implements RefreshableContent<VerticalLayout> {
    private final ClockDemonstration clock;
    private final Standings standings;
    private final QueueDemonstration queue;
    public StandingsView() {
        clock = new ClockDemonstration();
        standings = new Standings();
        queue = new QueueDemonstration();
        add(clock);
        add(standings);
        add(queue);
    }

    @Override
    public VerticalLayout getContent() {
        return this;
    }

    @Override
    public void refresh() {
        clock.refresh();
        standings.refresh();
        queue.refresh();
    }
}
