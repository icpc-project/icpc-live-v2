package org.icpclive.webadmin.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;

public interface RefreshableContent<T extends Component & HasSize> extends Refreshable {

    T getContent();
}
