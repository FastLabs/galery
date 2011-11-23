package zorg.frames.tagview.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;


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

    public TagView(Style style) {
        this.tagStyle = style;
    }

    private static Resources DEFAULT_RESOURCES;

    public static Resources getDefaultResources() {
        if (DEFAULT_RESOURCES == null) {
            DEFAULT_RESOURCES = GWT.create(Resources.class);
        }
        return DEFAULT_RESOURCES;
    }

    public TagView(Cell<T> renderer) {
        this(renderer, getDefaultResources());

    }

    public TagView(Cell<T> renderer, Resources resources) {
        this.cellRenderer = renderer;
        this.tagStyle = resources.tagViewStyle();
        this.tagStyle.ensureInjected();
        initWidget( ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("addTagBox")
    void onBlur(BlurEvent event) {
        actionElement.removeStyleName(tagStyle.actionContainerEditMode());
    }
    @UiHandler("addTagBox")
    void onFocus(FocusEvent event) {
        actionElement.addStyleName(tagStyle.actionContainerEditMode());
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

   /* @Override
    public void onBrowserEvent(Event event) {
        EventTarget eventTarget = event.getEventTarget();
        if (!Element.is(eventTarget)) {
            return;
        }

        Element element = eventTarget.cast();
        Element target = element;
        String message = "";
        if (event.getType().equals("click")) {
            while (target.getParentElement() != null && !target.getClassName().equalsIgnoreCase(tagStyle.tagContainer())) {
                if (target.getClassName().contains(tagStyle.actionContainer())) {
                    onEdit();
                    break;
                } else if (target.getClassName().contains(tagStyle.postfixIcon())) {
                    onRemove(target);
                    break;
                }
                target = target.getParentElement();
            }
        } else {
            Window.alert(event.getType());
            super.onBrowserEvent(event);
        }

    }*/


    private void onRemove(Element element) {
        Element toBeRemoved = element.getParentElement().getParentElement();
        //String id = toBeRemoved.getId();
        toBeRemoved.removeFromParent();
        //Window.alert("removed" + id);
    }
}