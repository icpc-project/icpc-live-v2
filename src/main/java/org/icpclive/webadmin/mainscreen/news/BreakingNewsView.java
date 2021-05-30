package org.icpclive.webadmin.mainscreen.news;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.icpclive.webadmin.utils.RefreshableContent;

public class BreakingNewsView extends HorizontalLayout implements RefreshableContent<HorizontalLayout> {
    private final BreakingNewsForm form;

    public BreakingNewsView() {
        Grid<BreakingNews> table = new BreakingNewsTable();
        VerticalLayout tableLayout = new VerticalLayout(table);
        form = new BreakingNewsForm();
        VerticalLayout formLayout = new VerticalLayout(form);

        table.asSingleSelect().addValueChangeListener(event -> form.update(event.getValue()));

        add(tableLayout, formLayout);
    }

    @Override
    public HorizontalLayout getContent() {
        return this;
    }

    @Override
    public void refresh() {
        form.refresh();
    }
}
