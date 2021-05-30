package org.icpclive.webadmin.mainscreen.video;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.UploaderView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class VideoView extends UploaderView<Video, VideoView.VideoPlayer> {


    public VideoView(){
        super(MainScreenService.getInstance().getVideoData(), "video");
    }

    @Override
    protected VideoPlayer newItemComponent() {
        return new VideoPlayer();
    }

    @Override
    protected String setVisibleItem(final Video item) {
        return MainScreenService.getInstance().getVideoData().setVisible(item);
    }

    @Override
    protected void hideContent() {
        MainScreenService.getInstance().getVideoData().hide();
    }

    @Override
    protected void setCaption(final String caption, final Video item) {
        MainScreenService.getInstance().getVideoData().setNewCaption(caption, item);
    }

    @Override
    protected void setSource(final VideoPlayer itemComponent, final Video lastItem) {
        itemComponent.setSrc(new StreamResource("", () -> {
            try {
                return Files.newInputStream(Paths.get(lastItem.getPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }));
    }

    @Override
    protected List<Video> getContainer() {
        return  MainScreenService.getInstance().getVideoData().getContainer();
    }

    @Override
    protected String getCaption(final Video item) {
        return item.getCaption();
    }

    @Override
    protected Video newItem(final String caption, final Path toAbsolutePath) {
        return new Video(caption, toAbsolutePath.toString());
    }

    @Override
    protected void addItem(final Video item) {
        MainScreenService.getInstance().getVideoData().addVideo(item);
    }

    @Tag("video")
    public static class VideoPlayer extends HtmlContainer implements ClickNotifier<Image> {

        public VideoPlayer() {
            getElement().setProperty("controls", true);
        }

        public void setSrc(AbstractStreamResource src) {
            getElement().setAttribute("src", src);
        }
    }
}
