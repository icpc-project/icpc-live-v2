package org.icpclive.webadmin;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.icpclive.datapassing.DataLoader;
import org.icpclive.webadmin.creepingline.CreepingLineView;
import org.icpclive.webadmin.locator.LocatorView;
import org.icpclive.webadmin.login.LoginService;
import org.icpclive.webadmin.login.LoginView;
import org.icpclive.webadmin.mainscreen.caption.CaptionView;
import org.icpclive.webadmin.mainscreen.news.BreakingNewsView;
import org.icpclive.webadmin.mainscreen.picture.PictureView;
import org.icpclive.webadmin.mainscreen.polls.PollView;
import org.icpclive.webadmin.mainscreen.split.SplitScreenView;
import org.icpclive.webadmin.mainscreen.standings.StandingsView;
import org.icpclive.webadmin.mainscreen.statistics.StatisticsView;
import org.icpclive.webadmin.mainscreen.team.TeamView;
import org.icpclive.webadmin.mainscreen.video.VideoView;
import org.icpclive.webadmin.pvp.PvPView;
import org.icpclive.webadmin.utils.LoopThread;
import org.icpclive.webadmin.utils.RefreshableContent;

import java.util.LinkedHashMap;
import java.util.Map;


@Route("")
@Push
public class MainView extends AppLayout {
    private final Logger log = LogManager.getLogger(MainView.class);
    private final Map<Tab, RefreshableContent<?>> contentByTab = new LinkedHashMap<>();


    public MainView() {
        final Tabs menuTabs = newMenuTabs();
        addToNavbar(moveTabsToCenter(menuTabs));
        Button logout = new Button("Logout");
        logout.setWidth("10rem");
        logout.addClickListener(event -> {
            LoginService.getInstance().logOutUser();
            UI.getCurrent().navigate(LoginView.class);
        });
        addToNavbar(logout);
        UI.getCurrent().setPollInterval(3000);
        DataLoader.iterateFrontend();
        LoopThread updater = new LoopThread(() -> {
            getUI().ifPresent(ui -> ui.access(() -> contentByTab.get(menuTabs.getSelectedTab()).refresh()));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("Main view updater thread was interrupted");
            }
        });
        updater.start();
        MainScreenContextListener.addThread(updater);
    }

    private Tabs newMenuTabs() {
        initContentByTab();

        final Tabs tabs = new Tabs();
        contentByTab.keySet().forEach(tabs::add);

        tabs.addSelectedChangeListener(event -> {
            final Tab selectedTab = event.getSelectedTab();
            setContent(contentByTab.get(selectedTab).getContent());
        });
        setContent(getFirst());
        return tabs;
    }

    private FlexLayout moveTabsToCenter(final Tabs tabs) {
        FlexLayout centeredLayout = new FlexLayout();
        centeredLayout.setSizeFull();
        centeredLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centeredLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        centeredLayout.add(tabs);
        return centeredLayout;
    }
    private Component getFirst() {
        return contentByTab.entrySet().iterator().next().getValue().getContent();
    }

    private void initContentByTab() {
        addContent("Captions", new CaptionView());
        addContent("Standings", new StandingsView());
        addContent("Statistics", new StatisticsView());
        addContent("Team view", new TeamView());
        addContent("Split screen", new SplitScreenView());
        addContent("Breaking news", new BreakingNewsView());
        addContent("Polls", new PollView());
        addContent("Creeping line", new CreepingLineView());
        addContent("Pictures", new PictureView());
        addContent("Video", new VideoView());
        addContent("PvP", new PvPView());
        addContent("Locator", new LocatorView());
    }


    private void addContent(final String tabText, final RefreshableContent<?> view) {
        view.getContent().setSizeFull();
        contentByTab.put(new Tab(tabText), view);
    }

}
