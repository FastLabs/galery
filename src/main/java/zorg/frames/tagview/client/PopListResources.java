package zorg.frames.tagview.client;

import com.google.gwt.user.cellview.client.CellList;

public interface PopListResources extends CellList.Resources {

    @Override
    @Source({"PopList.css"})
    PopListStyle cellListStyle();

    interface PopListStyle extends CellList.Style {
        String containerStyle();
        String contentStyle();
        String listContainer();
    }


}
