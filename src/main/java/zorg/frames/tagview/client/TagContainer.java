package zorg.frames.tagview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.*;
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
            EventTarget eventTarget = event.getEventTarget();
            if (!Element.is(eventTarget)) {
                return;
            }
            if ("click".equals(event.getType())) {
                Element target = eventTarget.cast();
                while (target.getParentElement() != null && !target.getClassName().equalsIgnoreCase(tagStyle.tagElement())) {
                    target = target.getParentElement();
                }
                removeItemById(target.getId());
            }
        }
    };

    private SpanElement buildTagElement(String text) {
        SpanElement tagElement = Document.get().createSpanElement();
        SafeHtmlBuilder cellContent = new SafeHtmlBuilder();
        cellContent.append(template.prefixIconFragment(tagStyle.prefixIcon())).append(template.tagTextFragment(SafeHtmlUtils.fromSafeConstant(text), tagStyle.tagText())).append(template.postfixIconFragment(tagStyle.postfixIcon()));
        tagElement.setClassName(tagStyle.tagElement());
        tagElement.setId(idGenerator.getNextId());
        tagElement.setInnerHTML(template.internalTagElement(cellContent.toSafeHtml(), tagStyle.internalTagElement()).asString());
        DOM.setEventListener(tagElement.<com.google.gwt.user.client.Element>cast(), listener);
        DOM.sinkEvents(tagElement.<com.google.gwt.user.client.Element>cast(), Event.ONCLICK);
        return tagElement;
    }

    public void removeAll() {
        traverseNodes(deleteNodeOperation);
    }

    static interface ElementOperation {
        void operate(Element element);
    }

    static interface ElementCriteria {
        boolean isMeet(Element element);
    }

    private ElementOperation deleteNodeOperation = new ElementOperation() {
        public void operate(Element element) {
            element.removeFromParent();
        }
    };

    static abstract class ConditionalOperation implements ElementOperation, ElementCriteria {
        public void operate(Element element) {
            if (isMeet(element)) {
                onConditionMeet(element);
            }
        }

        abstract protected void onConditionMeet(Element element);
    }

    static class DeleteElementById extends ConditionalOperation {
        private String id;
        private static DeleteElementById instance;

        static DeleteElementById whenElementId(String id) {
            if (instance == null) {
                instance = new DeleteElementById(id);
            } else {
                instance.id = id;
            }
            return instance;
        }

        private DeleteElementById(String id) {
            this.id = id;
        }

        public boolean isMeet(Element element) {
            if (id == null && element.getId() == null) {
                return true;
            }
            return id.equals(element.getId());
        }

        @Override
        protected void onConditionMeet(Element element) {
            element.removeFromParent();
        }
    }

    private void traverseNodes(ElementOperation operation) {
        NodeList nodes = rootElement.getChildNodes();
        for (int i = 0; i < rootElement.getChildCount(); i++) {
            operation.operate(nodes.getItem(i).<Element>cast());
        }
    }

    private void removeItemById(String id) {
        traverseNodes(DeleteElementById.whenElementId(id));
    }

    public void addItem(String item) {
        SpanElement element = buildTagElement(item);
        rootElement.appendChild(element);
    }
}