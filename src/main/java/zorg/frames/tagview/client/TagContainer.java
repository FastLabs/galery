package zorg.frames.tagview.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;



public class TagContainer extends Widget {

    class UiIdGenerator {
        private int _next_gen = 0;

        public String getNextId() {
            return ":" + Integer.toString(_next_gen++, 36);
        }
    }

    private final UiIdGenerator idGenerator = new UiIdGenerator();
    private Template template = GWT.create(Template.class);

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

    public interface Resources extends ClientBundle {
        @Source(Style.DEFAULT_CSS_RESOURCES)
        Style tagViewStyle();

        @Source("close_semitransparent.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None, height = 7, width = 7)
        ImageResource closeButton();

        @Source("close_gray.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.None, height = 7, width = 7)
        ImageResource closeButtonHover();
    }

    public interface Style extends CssResource {
        public static final String DEFAULT_CSS_RESOURCES = "zorg/frames/tagview/client/Tag.css";
        String tagElement();
        String tagText();
        String postfixIcon();
        String prefixIcon();
        String internalTagElement();
   }
    
    Style tagStyle;
    Resources resources;
    private DivElement rootElement;


    public TagContainer() {
        resources = GWT.create(Resources.class);
        tagStyle = resources.tagViewStyle();
        tagStyle.ensureInjected();
        rootElement = Document.get().createDivElement();
        rootElement.setAttribute("style", "display:inline");
        setElement(rootElement);
    }
    
    private EventListener listener = new EventListener() {
        public void onBrowserEvent(Event event) {
            
        }
    };

    private SpanElement buildTagElement (String text) {

        SpanElement tagElement = Document.get().createSpanElement();
        SafeHtmlBuilder cellContent = new SafeHtmlBuilder();
        cellContent.append(template.prefixIconFragment(tagStyle.prefixIcon()))
                .append(template.tagTextFragment(SafeHtmlUtils.fromSafeConstant(text), tagStyle.tagText()))
                .append(template.postfixIconFragment(tagStyle.postfixIcon()));
        tagElement.setClassName(tagStyle.tagElement());
        tagElement.setId(idGenerator.getNextId());
        tagElement.setInnerHTML(template.internalTagElement(cellContent.toSafeHtml(), tagStyle.internalTagElement()).asString());


        DOM.setEventListener(tagElement.<Element>cast(), listener);
        return tagElement;
    }

    public void removeAll () {
        NodeList nodes =  rootElement.getChildNodes();
        for(int i =0;i<rootElement.getChildCount(); i++) {
            nodes.getItem(i).removeFromParent();
        }
    }
    
    public void addItem(String item) {
        SpanElement element = buildTagElement(item);
        rootElement.appendChild(element);
    }
}
