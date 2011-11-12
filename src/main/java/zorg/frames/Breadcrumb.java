package zorg.frames;


import apple.laf.JRSUIConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;

public class Breadcrumb extends Widget {

    interface Template extends SafeHtmlTemplates {

    }

    public interface Resources extends ClientBundle {

    }

    public interface Style extends CssResource {

    }

    private static final Template template = GWT.create(Template.class);
    private static final Resources resources = GWT.create(Resources.class);


    public Breadcrumb() {
        DivElement rootElement = Document.get().createDivElement();
        setElement(rootElement);
    }


}
