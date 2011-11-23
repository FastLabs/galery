package zorg.frames.tagview.client;


import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

import java.util.ArrayList;
import java.util.List;

public class FilterableListDataProvider<T> extends ListDataProvider<T> {
    public static interface Filter <T> {
        boolean match(T value, RegExp regExp);
    }

    private List<T> originalData;

    public FilterableListDataProvider() {
        this(new ArrayList<T>(), null);
        this.originalData = getList();
    }

    public FilterableListDataProvider(List<T> listToWrap) {
        super(listToWrap);
        this.originalData = getList();
    }

    public FilterableListDataProvider(ProvidesKey<T> keyProvider) {
        super(keyProvider);
        this.originalData = getList();
    }

    public FilterableListDataProvider(List<T> listToWrap, ProvidesKey<T> keyProvider) {
        super(listToWrap, keyProvider);
        this.originalData = getList();
    }

    public void filterData(Filter <T> filter, RegExp regExp) {
        List<T> newData = new ArrayList<T>();
        for(T element: this.originalData) {
            if(filter.match(element, regExp)) {
                newData.add(element);
            }
        }
        super.setList(newData);
        super.flush();
    }

    public void reset() {
        super.setList(originalData);
        super.flush();
    }



    @Override
    public void setList(List<T> listToWrap) {
        super.setList(listToWrap);
        this.originalData = super.getList();
    }
}
