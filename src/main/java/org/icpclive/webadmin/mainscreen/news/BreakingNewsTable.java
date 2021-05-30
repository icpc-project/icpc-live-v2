package org.icpclive.webadmin.mainscreen.news;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.backend.player.widgets.old.ClockWidget;
import org.icpclive.events.ContestInfo;
import org.icpclive.events.EventsLoader;
import org.icpclive.events.PCMS.PCMSContestInfo;
import org.icpclive.events.RunInfo;
import org.icpclive.webadmin.MainScreenContextListener;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.LoopThread;

import java.util.concurrent.LinkedBlockingDeque;

@CssImport(themeFor = "vaadin-grid",
        value = "./styles/grid-news-styles.css")
class BreakingNewsTable extends Grid<BreakingNews> {
    private static final int LINE_COUNT = MainScreenService.getProperties().getBreakingNewsProperties().getRunCount();
    private static final Logger log = LogManager.getLogger(BreakingNewsView.class);
    private static final LinkedBlockingDeque<BreakingNews> news = new LinkedBlockingDeque<>();
    private static int lastShowedRun = 0;

    public BreakingNewsTable() {
        setItems(news);
        setVerticalScrollingEnabled(false);
        addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        addColumns();
        getColumns().forEach(column -> column.setClassNameGenerator(run -> run.getOutcome().toLowerCase()));
        Thread updater = newUpdaterThread();
        updater.start();
        MainScreenContextListener.addThread(updater);
    }


    private void addColumns() {
        addColumn(BreakingNews::getTeamId).setHeader("Team id");
        addColumn(BreakingNews::getTeam).setHeader("Team");
        addColumn(BreakingNews::getProblem).setHeader("Problem");
        addColumn(BreakingNews::getOutcome).setHeader("Outcome");
        addColumn(item -> ClockWidget.getTimeString(item.getTimestamp() / 1000)).setHeader("Time");
    }

    private String getProblemLetter(int id) {
        return "" + (char) (id + 'A');
    }

    private LoopThread newUpdaterThread() {
        return new LoopThread(() -> {
            ContestInfo contestInfo = loadContestInfo();
            if (contestInfo instanceof PCMSContestInfo) {
                return;
            }
            while (lastShowedRun <= contestInfo.getLastRunId()) {
                RunInfo run = contestInfo.getRun(lastShowedRun);
                if (run != null) {
                    BreakingNews newItem = new BreakingNews(run.getResult(), getProblemLetter(run.getProblemId()), run.getTeamId() + 1, run.getTime(), run.getId());
                    news.addFirst(newItem);
                }
                lastShowedRun++;
            }
            while (news.size() > LINE_COUNT) {
                news.pollLast();
            }
            update(() -> getDataProvider().refreshAll());
            for (BreakingNews item : news) {
                RunInfo run = contestInfo.getRun(item.getRunId());
                if (run != null) {
                    item.setOutcome(run.getResult());
                    item.setTimestamp(run.getTime() * 1000);
                }
            }
            update(() -> getDataProvider().refreshAll());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            log.error("Breaking news updater thread was interrupted", e);
        }
    });
}

    private void update(Runnable action) {
        getUI().ifPresent(ui -> ui.access(action::run));
    }

    private ContestInfo loadContestInfo() {
        ContestInfo contestInfo;
        do {
            contestInfo = EventsLoader.getInstance().getContestData();
        } while (contestInfo == null);
        return contestInfo;

    }
}
