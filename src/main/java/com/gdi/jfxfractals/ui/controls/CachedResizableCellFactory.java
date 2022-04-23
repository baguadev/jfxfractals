package com.gdi.jfxfractals.ui.controls;

import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.internal.viewloader.View;
import de.saxsys.mvvmfx.utils.viewlist.ViewListCell;
import de.saxsys.mvvmfx.utils.viewlist.ViewListCellFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

public class CachedResizableCellFactory  <V extends View<VM>, VM extends ViewModel> implements ViewListCellFactory<VM> {
    private Map<VM, ViewTuple<V, VM>> cache = new HashMap<>();

    private Callback<VM, ViewTuple<V, VM>> loadFactory;

    public CachedResizableCellFactory(Callback<VM, ViewTuple<V, VM>> loadFactory) {
        this.loadFactory = loadFactory;
    }

    @Override
    public ViewTuple<V, VM> map(VM viewModel) {
        if (!cache.containsKey(viewModel)) {
            final ViewTuple<V, VM> viewTuple = loadFactory.call(viewModel);
            cache.put(viewModel, viewTuple);
        }

        return cache.get(viewModel);
    }


    public static <V extends View<VM>, VM extends ViewModel> CachedResizableCellFactory<V, VM> create(
            Callback<VM, ViewTuple<V, VM>> callback) {
        return new CachedResizableCellFactory<>(callback);
    }

    public static <V extends FxmlView<VM>, VM extends ViewModel> CachedResizableCellFactory<V, VM> createForFxmlView(
            Class<V> viewType) {
        return create(vm -> FluentViewLoader.fxmlView(viewType).viewModel(vm).load());
    }

    public static <V extends JavaView<VM>, VM extends ViewModel> CachedResizableCellFactory<V, VM> createForJavaView(
            Class<V> viewType) {
        return create(vm -> FluentViewLoader.javaView(viewType).viewModel(vm).load());
    }

    @Override
    public ViewListCell<VM> call(ListView<VM> element) {
        return new ViewListCell<>() {
            {
                prefWidthProperty().bind(element.widthProperty().subtract(20)); // 1
                setMaxWidth(Control.USE_PREF_SIZE);
            }
            @Override
            public ViewTuple<? extends View, ? extends ViewModel> map(VM element) {
                return CachedResizableCellFactory.this.map(element);
            }

            @Override
            protected void updateItem(VM item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    Node currentNode = getGraphic();
                    Parent view = map(item).getView();

                    if (currentNode == null || !currentNode.equals(view)) {
                        setGraphic(view);
                    }
                    else
                    {
                        AnchorPane pane = new AnchorPane( view );
                        pane.prefWidthProperty().bind(element.widthProperty());
                        AnchorPane.setLeftAnchor(view, 0.0);
                        AnchorPane.setRightAnchor(view, 0.0);
                        setGraphic(pane);
                    }
                }
            }
        };
    }
}
