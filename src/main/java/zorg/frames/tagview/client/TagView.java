package zorg.frames.tagview.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;

import java.util.Arrays;
import java.util.List;

/**
 * User: Oleg Bulavitchi
 */
public class TagView<T> extends Composite {
    interface TagViewUiBinder extends UiBinder<FlowPanel, TagView> {
    }

    private static TagViewUiBinder ourUiBinder = GWT.create(TagViewUiBinder.class);
    private List<T> availableItems;
    private List<T> selectedItems;
    private Cell<T> cellRenderer;
    private boolean acceptsRemovals = true;

    public interface Resources extends ClientBundle {
        @Source(Style.DEFAULT_CSS_RESOURCES)
        Style tagViewStyle();

        @Source("add_blue.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None, height = 8, width = 8)
        ImageResource addButton();
    }

    public interface Style extends CssResource {
        public static final String DEFAULT_CSS_RESOURCES = "zorg/frames/tagview/client/TagView.css";

        String tagContainer();

        String addLabelContainer();

        String addLabel();

        String addInput();

        String actionContainer();

        String addButton();

        String actionContainerEditMode();
    }

    private Resources resources;
    @UiField(provided = true)
    Style tagStyle;
    @UiField
    FlowPanel actionElement;
    @UiField
    TextBox addTagBox;
    @UiField
    TagContainer tagContainer;
    @UiField
    Label addElementLabel;
    @UiField
    Label addButton;
    private PopList<T> popList;
    FilterableListDataProvider<T> dataProvider;
    private static Resources DEFAULT_RESOURCES;

    public static Resources getDefaultResources() {
        if (DEFAULT_RESOURCES == null) {
            DEFAULT_RESOURCES = GWT.create(Resources.class);
        }
        return DEFAULT_RESOURCES;
    }

    public TagView(Cell<T> renderer, List<T> data) {
        this(renderer, getDefaultResources());
        this.dataProvider.setList(data);
    }

    private NoSelectionModel<T> selectionModel = new NoSelectionModel<T>();

    public TagView(Cell<T> renderer, Resources resources) {
        this.cellRenderer = renderer;
        this.tagStyle = resources.tagViewStyle();
        this.tagStyle.ensureInjected();
        this.dataProvider = new FilterableListDataProvider<T>();
        popList = new PopList<T>(this.dataProvider, renderer);
        initWidget(ourUiBinder.createAndBindUi(this));
        popList.setSelectionModel(selectionModel);
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Window.alert(selectionModel.getLastSelectedObject().toString());
            }
        });
    }

    @UiHandler("addTagBox")
    void onBlur(BlurEvent event) {
        actionElement.removeStyleName(tagStyle.actionContainerEditMode());
    }

    @UiHandler("addTagBox")
    void onFocus(FocusEvent event) {
        actionElement.addStyleName(tagStyle.actionContainerEditMode());
        popList.show(getElement().getAbsoluteLeft(), getElement().getAbsoluteBottom());
    }

    @UiHandler("addTagBox")
    void onKeyUpEvent(KeyUpEvent event) {
        switch (event.getNativeKeyCode()) {
            case KeyCodes.KEY_ENTER:
                if (addTagBox.getText() != null && !addTagBox.getText().equals("")) {
                    tagContainer.addItem(addTagBox.getText());
                    addTagBox.setFocus(false);
                    addTagBox.setText("");
                }
                break;
            case KeyCodes.KEY_ESCAPE:
                addTagBox.setFocus(false);
                break;
        }
    }

    @UiHandler({"addElementLabel", "addButton"})
    void onAddTag(ClickEvent event) {
        addTagBox.setFocus(true);
    }

    public void setVisibleTags(List<T> data) {
        this.tagContainer.removeAll();
        for (T element : data) {
            SafeHtmlBuilder textContent = new SafeHtmlBuilder();
            cellRenderer.render(new Cell.Context(0, 0, null), element, textContent);
            this.tagContainer.addItem(textContent.toSafeHtml().asString());
        }
    }
}