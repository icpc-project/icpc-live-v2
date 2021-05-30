package org.icpclive.webadmin.mainscreen.picture;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;
import org.icpclive.webadmin.mainscreen.MainScreenService;
import org.icpclive.webadmin.utils.UploaderView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PictureView extends UploaderView<Picture, Image> {
    public PictureView() {
        super(MainScreenService.getInstance().getPictureData(), "picture");
    }

    @Override
    protected Image newItemComponent() {
        return new Image();

    }

    @Override
    protected String setVisibleItem(final Picture item) {
        return MainScreenService.getInstance().getPictureData().setVisible(item);
    }

    @Override
    protected void hideContent() {
        MainScreenService.getInstance().getPictureData().hide();
    }

    @Override
    protected void setCaption(final String caption, final Picture item) {
        MainScreenService.getInstance().getPictureData().setNewCaption(caption, item);
    }

    @Override
    protected void setSource(final Image itemComponent, final Picture lastItem) {
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
    protected List<Picture> getContainer() {
        return MainScreenService.getInstance().getPictureData().getContainer();
    }

    @Override
    protected String getCaption(final Picture item) {
        return item.getCaption();
    }

    @Override
    protected Picture newItem(final String value, final Path toAbsolutePath) {
        return new Picture(value, toAbsolutePath.toString());
    }

    @Override
    protected void addItem(final Picture item) {
        MainScreenService.getInstance().getPictureData().addPicture(item);
    }
}
