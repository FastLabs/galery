package zorg.frames.tagview.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel;

public class PopList <T> extends Composite {

    private static class Popup extends PopupPanel {
        private static Popup instance;
        private Popup (PopListResources.PopListStyle style) {
            super(true, false);
            setStyleName(style.containerStyle());
            setStyleName(getContainerElement(), style.contentStyle());
        }

        public static void show(PopList list, int x, int y) {
            if(instance == null) {
                instance = new Popup(list.resources.cellListStyle());
            }
            instance.setPopupPosition(x, y);
            if(instance.getWidget() == null) {
                instance.add(list);
            }
            instance.show();
        }

        public static void hidePopup() {
            instance.hide();
        }
    }

    interface PopListUiBinder extends UiBinder<HTMLPanel, PopList> {
    }

    private static PopListUiBinder ourUiBinder = GWT.create(PopListUiBinder.class);

    @UiField (provided = true)
    CellList<T> itemList;
    @UiField (provided = true)
    PopListResources resources;

    public PopList(ListDataProvider<T> dataProvider, Cell<T> renderer) {
        resources = GWT.create(PopListResources.class);
        resources.cellListStyle().ensureInjected();
        itemList = new CellList<T>(renderer, resources);
        initWidget(ourUiBinder.createAndBindUi(this));
        dataProvider.addDataDisplay(itemList);
    }

    public void setSelectionModel(SelectionModel<T> selectionModel) {
        itemList.setSelectionModel(selectionModel);
    }

    public void show(int x, int y ) {
        Popup.show(this, x, y);
    }

    public void hide() {
      Popup.hidePopup();
    }
}