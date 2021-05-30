package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.events.TeamInfo;

public class SplitScreenData extends CachedData {
    public SplitScreenData() {
        for (int i = 0; i < 4; i++) {
            controllerDatas[i] = new TeamData();
            isAutomatic[i] = true;
        }
    }

    @Override
    public SplitScreenData initialize() {
        SplitScreenData data = MainScreenService.getInstance().getSplitScreenData();
        this.controllerDatas = data.controllerDatas;
        this.isAutomatic = data.isAutomatic;

        return this;
    }

    public void recache() {
        Data.cache.refresh(SplitScreenData.class);
    }

    public synchronized String setInfoVisible(int controllerId, boolean visible, String type, TeamInfo teamInfo) {
        return controllerDatas[controllerId].setInfoManual(visible, type, teamInfo, false);
    }

    public synchronized boolean isVisible(int controllerId) {
        return controllerDatas[controllerId].isVisible();
    }

    public synchronized TeamInfo getTeam(int controllerId) {
        return controllerDatas[controllerId].getTeam();
    }

    public synchronized String infoStatus(int controllerId) {
        return controllerDatas[controllerId].infoStatus();
    }

    public synchronized int getTeamId(int contollerId) {
        return controllerDatas[contollerId].getTeamId();
    }

    public TeamData[] controllerDatas = new TeamData[4];
    public boolean[] isAutomatic = new boolean[4];
    public long[] timestamps = new long[4];
}
