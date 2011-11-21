package zorg.frames.tagview.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;


import java.util.List;

/**
 * User: Oleg Bulavitchi
 */
public class TagView<T> extends Widget {
    interface TagViewUiBinder extends UiBinder<DivElement, TagView> {
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
            return ":" +Integer.toString(_next_gen++, 36);
        }
    }

    interface Template extends SafeHtmlTemplates {
        //tag element templates
       // @Template("<span id='{0}' class='{2}'>{1}</span>")
       // SafeHtml tagElement(String id, SafeHtml interTagElement, String style);

        @Template("<span class='{1}'>{0}</span>")
        SafeHtml internalTagElement(SafeHtml tagFragments, String style);

        @Template("<span class='{0}'></span>")
        SafeHtml prefixIconFragment(String style);

        @Template("<span class='{1}'>{0}</span>")
        SafeHtml tagTextFragment(SafeHtml text, String style);

        @Template("<span class='{0}'></span>")
        SafeHtml postfixIconFragment(String style);

        //add control element
        //    @Template("<span class='{1}'>{0} <input type='text' class='{2}'/> </span>")
        //    SafeHtml actionContainer(SafeHtml content, String style, String inputStyle);
        @Template("<input type='text' class='{0}'/>")
        SafeHtml input(String inputStyle);

        @Template("<span class='{1}'> <span class='{2}'></span> <span class='{3}'>{0}</span></span>")
        SafeHtml addLabelContainer(SafeHtml label, String addLabelContainer, String addButtonStyle, String addLabelStyle);

    }


    private Template template = GWT.create(Template.class);
    private Resources resources;

    @UiField(provided = true)
    Style tagStyle;

    public TagView(Style style) {
        this.tagStyle = style;
    }

    private static Resources DEFAULT_RESOURCES;
    private SpanElement actionElement;
    private DivElement rootElement;
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
        setElement(rootElement);
        rootElement.appendChild(getActionElement());
        this.sinkEvents(Event.ONCLICK|Event.ONFOCUS);
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
            rootElement.appendChild(tagElement);
        }
       rootElement.appendChild(actionElement);

    }

    private Element getActionElement() {
        if (actionElement == null) {
            actionElement = Document.get().createSpanElement();
            actionElement.setClassName(tagStyle.actionContainer());
            SafeHtmlBuilder action = new SafeHtmlBuilder();
            SafeHtml addLabel = template.addLabelContainer(SafeHtmlUtils.fromSafeConstant("Add more")
                    , tagStyle.addLabelContainer()
                    , tagStyle.addButton()
                    , tagStyle.addLabel());
            action.append(addLabel).append(template.input(tagStyle.addInput()));
            actionElement.setInnerHTML(action.toSafeHtml().asString());
        }
        return actionElement;
    }

    @Override
    public void onBrowserEvent(Event event) {
        EventTarget eventTarget = event.getEventTarget();
        if(!Element.is(eventTarget)) {
            return;
        }
        
        Element element = eventTarget.cast();
        Element target = element;
        String message = "";
        while (target.getParentElement() != null && !target.getClassName().equalsIgnoreCase(tagStyle.tagContainer())) {
          if(target.getClassName().contains(tagStyle.actionContainer())) {
            onEdit();
            break;
          } else
          if(target.getClassName().contains(tagStyle.postfixIcon()))  {
              onRemove(target);
              break;
          }
          target = target.getParentElement();
    //    if(element == getActionElement()) {

        //super.onBrowserEvent(event);
        //        Window.alert("Add element ");
        }
       // Window.alert("" + message);
    }


    
    private void onEdit() {
        getActionElement().addClassName(tagStyle.actionContainerEditMode());
        Element editElement = getActionElement().getLastChild().cast();
        editElement.focus();
        editElement.a

    }
    
    private void onRemove(Element element) {
        Element toBeRemoved = element.getParentElement().getParentElement();
        String id = toBeRemoved.getId();
        toBeRemoved.removeFromParent();
        Window.alert("removed" + id);
    }
}