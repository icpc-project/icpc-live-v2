package org.icpclive.datapassing;

import org.icpclive.webadmin.mainscreen.caption.Advertisement;
import org.icpclive.webadmin.mainscreen.MainScreenService;

import java.util.List;

/**
 * Created by Aksenov239 on 21.11.2015.
 */
public class AdvertisementData extends CachedData {
    public boolean isVisible;
    public Advertisement advertisement;
    final private Object advertisementLock = new Object();

    public AdvertisementData initialize() {
        AdvertisementData data = MainScreenService.getInstance().getAdvertisementData();
        synchronized (advertisementLock) {
            this.timestamp = data.timestamp;
            this.isVisible = data.isVisible;
            this.advertisement = data.advertisement == null ? new Advertisement("") : new Advertisement(data.advertisement.getAdvertisement());
            this.delay = data.delay;
        }
        return this;
    }

    public void recache() {
        Data.cache.refresh(AdvertisementData.class);
    }

    public void update() {
        boolean change = false;
        synchronized (advertisementLock) {
            long time = timestamp + MainScreenService.getProperties().getAdvertisementProperties().getTime();
            if (System.currentTimeMillis() > time) {
                //System.err.println("Big idle for advert");
                isVisible = false;
                change = true;
            }
        }
        if (change) {
            recache();
        }
    }

    public String toString() {
        return isVisible ? "Advertisement \"" + advertisement.getAdvertisement() + "\"" : "No advertisement now";
    }

    public String checkOverlays() {
//        if (MainScreenData.getMainScreenData().teamData.isVisible) {
//            return MainScreenData.getMainScreenData().teamData.getOverlayError();
//        }
        return null;
    }

    public void hide() {
        synchronized (advertisementLock) {
            timestamp = System.currentTimeMillis();
            isVisible = false;
        }
    }

    public String setAdvertisementVisible(boolean visible, Advertisement advertisement) {
        if (visible) {
            String outcome = checkOverlays();
            if (outcome != null) {
                return outcome;
            }
        }
        synchronized (advertisementLock) {
            timestamp = System.currentTimeMillis();
            isVisible = visible;
            this.advertisement = advertisement;
        }
        recache();
        return null;
    }

    public List<Advertisement> getContainer() {
        return MainScreenService.getProperties().getAdvertisementProperties().getBackUp().getData();
    }

    public void removeAdvertisement(Advertisement advertisement) {
        MainScreenService.getProperties().getAdvertisementProperties().getBackUp().removeItem(advertisement);
    }

    public void addAdvertisement(Advertisement advertisement) {
        MainScreenService.getProperties().getAdvertisementProperties().getBackUp().addItem(advertisement);
    }
}
