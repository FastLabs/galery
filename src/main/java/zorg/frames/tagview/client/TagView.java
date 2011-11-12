package zorg.frames.tagview.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * User: Oleg Bulavitchi
 */
public class TagView <T> extends Widget {
    interface TagViewUiBinder extends UiBinder<DivElement, TagView> {
    }

    private static TagViewUiBinder ourUiBinder = GWT.create(TagViewUiBinder.class);
    
    private List<T> availableItems;
    private List<T> selectedItems;

    private Cell<T> cellRenderer;

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
        public static final String DEFAULT_CSS_RESOURCES = "zorg/frames/tagview/client/TagView.css";
        String tagContainer();
        String tagElement();
        String tagText();
        String postfixIcon();
        String prefixIcon();
        String internalTagElement();
    }

    interface Template extends SafeHtmlTemplates {

    }
    
    private Resources resources;

    @UiField(provided = true)
    Style tagStyle;

    public TagView(Style style) {
        this.tagStyle  = style;
    }

    private static Resources DEFAULT_RESOURCES;

    public static Resources getDefaultResources() {
        if(DEFAULT_RESOURCES == null) {
            DEFAULT_RESOURCES = GWT.create(Resources.class);
        }
        return DEFAULT_RESOURCES;
    }

    public TagView() {
        this(getDefaultResources());

    }
    
    public TagView(Resources resources) {
        this.tagStyle = resources.tagViewStyle();
        this.tagStyle.ensureInjected();
        DivElement rootElement = ourUiBinder.createAndBindUi(this);
        setElement(rootElement);
    }
}