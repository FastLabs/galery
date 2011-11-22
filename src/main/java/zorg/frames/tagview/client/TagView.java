package zorg.frames.tagview.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
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
public class TagView<T> extends Widget {
    interface TagViewUiBinder extends UiBinder<SimplePanel, TagView> {
    }

    private static TagViewUiBinder ourUiBinder = GWT.create(TagViewUiBinder.class);

    private List<T> availableItems;
    private List<T> selectedItems;
    private Cell<T> cellRenderer;
    private final UiIdGenerator idGenerator = new UiIdGenerator();
    private boolean acceptsRemovals = true;

    public interface Resources extends ClientBundle {
        @Source(Style.DEFAULT_CSS_RESOURCES)
        Style tagViewStyle();

        @Source("close_semitransparent.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None, height = 7, width = 7)
        ImageResource closeButton();

        @Source("close_gray.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None, height = 7, width = 7)
        ImageResource closeButtonHover();

        @Source("add_blue.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None, height = 8, width = 8)
        ImageResource addButton();
    }

    public interface Style extends CssResource {
        public static final String DEFAULT_CSS_RESOURCES = "zorg/frames/tagview/client/TagView.css";

        String tagContainer();

        String tagElement();

        String tagText();

        String postfixIcon();

        String prefixIcon();

        String internalTagElement();

        String addLabelContainer();

        String addLabel();

        String addInput();

        String actionContainer();

        String addButton();

        String actionContainerEditMode();
    }

    class UiIdGenerator {
        private int _next_gen = 0;

        public String getNextId() {
            return ":" + Integer.toString(_next_gen++, 36);
        }
    }

    interface Template extends SafeHtmlTemplates {
        @Template("<span class='{1}'>{0}</span>")
        SafeHtml internalTagElement(SafeHtml tagFragments, String style);

        @Template("<span class='{0}'></span>")
        SafeHtml prefixIconFragment(String style);

        @Template("<span class='{1}'>{0}</span>")
        SafeHtml tagTextFragment(SafeHtml text, String style);

        @Template("<span class='{0}'></span>")
        SafeHtml postfixIconFragment(String style);

    }


    private Template template = GWT.create(Template.class);
    private Resources resources;

    @UiField(provided = true)
    Style tagStyle;
    @UiField
    FlowPanel actionElement;
    @UiField
    TextBox addTagBox;

    public TagView(Style style) {
        this.tagStyle = style;
    }

    private static Resources DEFAULT_RESOURCES;
    private SimplePanel rootElement;

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
        rootElement = ourUiBinder.createAndBindUi(this);
        setElement(rootElement.getElement());
        this.sinkEvents(Event.ONCLICK);
    }
    @UiHandler("addTagBox")
    void onBlur(BlurEvent event) {
        actionElement.removeStyleDependentName(tagStyle.actionContainerEditMode());
    }

    public void setVisibleTags(List<T> data) {
        actionElement.removeFromParent();
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        for (T element : data) {
            SafeHtmlBuilder cellContent = new SafeHtmlBuilder();
            SafeHtmlBuilder textContent = new SafeHtmlBuilder();
            cellRenderer.render(new Cell.Context(0, 0, null), element, textContent);
            cellContent.append(template.prefixIconFragment(tagStyle.prefixIcon()))
                    .append(template.tagTextFragment(textContent.toSafeHtml(), tagStyle.tagText()))
                    .append(template.postfixIconFragment(tagStyle.postfixIcon()));

            SpanElement tagElement = Document.get().createSpanElement();
            tagElement.setClassName(tagStyle.tagElement());
            tagElement.setId(idGenerator.getNextId());
            tagElement.setInnerHTML(template.internalTagElement(cellContent.toSafeHtml(), tagStyle.internalTagElement()).asString());
            rootElement.getElement().appendChild(tagElement);
        }
        rootElement.getElement().appendChild(actionElement.getElement());
    }

    @Override
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

    }


    private void onEdit() {
        actionElement.addStyleName(tagStyle.actionContainerEditMode());
        addTagBox.setFocus(true);
        //editElement.a

    }

    private void onRemove(Element element) {
        Element toBeRemoved = element.getParentElement().getParentElement();
        //String id = toBeRemoved.getId();
        toBeRemoved.removeFromParent();
        //Window.alert("removed" + id);
    }
}