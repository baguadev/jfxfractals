package com.gdi.jfxfractals.ui.maincontent;


import com.gdi.jfxfractals.model.FractalInfo;
import com.gdi.jfxfractals.renderer.IFractalRender;
import com.gdi.jfxfractals.renderer.RenderManager;
import com.gdi.jfxfractals.service.RendererContext;
import com.gdi.jfxfractals.ui.controls.CachedResizableCellFactory;
import com.gdi.jfxfractals.ui.controls.DFXInputHandler;
import com.gdi.jfxfractals.ui.fractals.SettingsManager;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXListView;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.eclipse.fx.drift.DriftFXSurface;
import org.eclipse.fx.drift.Placement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static com.gdi.jfxfractals.ui.maincontent.MainContentViewModel.SELECT_FRACTAL;

public class MainContentView implements FxmlView<MainContentViewModel>, Initializable {

    @InjectViewModel
    private MainContentViewModel viewModel;

    @InjectContext
    Context context;

    @Inject
    Stage primaryStage;

    @Inject
    // Notification Center
    private NotificationCenter notificationCenter;

    @Inject
    private RendererContext rendererContext;

    @Inject
    private RenderManager renderManager;

    @Inject
    SettingsManager settingsManager;

    @FXML
    JFXListView<FractalsListItemViewModel> fractalsListView;

    @FXML
    AnchorPane renderCanvasPane;

    @FXML
    AnchorPane fractalInfoPane;

    DriftFXSurface driftFXSurface;

    DFXInputHandler mouseHandler;

    private static final Logger log = LoggerFactory.getLogger(MainContentView.class);
    private CachedResizableCellFactory<FractalsListItemView, FractalsListItemViewModel> cellFactory;
    BorderPane layoutPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        viewModel.subscribe(SELECT_FRACTAL, (key, value) -> {
            FractalInfo fractalInfo = (FractalInfo) value[0];
            IFractalRender fractalRender = fractalInfo.getRenderer();

            Platform.runLater(() -> {
                driftFXSurface = new DriftFXSurface();
                fractalRender.setDriftFxSurface(driftFXSurface);
                driftFXSurface.requestFocus();
                rendererContext.setFractalRender(fractalRender);
                layoutPane.setCenter(null);
                layoutPane.setCenter(driftFXSurface);
                renderManager.applyRenderer(fractalRender);
                fractalInfoPane.getChildren().clear();
                fractalInfoPane.getChildren().add(settingsManager.getSettingsContent(fractalRender,context));
            });
        });
        Platform.runLater(() -> {
            layoutPane = new BorderPane();
            driftFXSurface = new DriftFXSurface();
            driftFXSurface.setPlacementStrategy(Placement.CENTER);
            renderCanvasPane.setBackground(null);
            layoutPane.setCenter(driftFXSurface);
            renderCanvasPane.getChildren().addAll(layoutPane);
            AnchorPane.setBottomAnchor(layoutPane, 0.0);
            AnchorPane.setTopAnchor(layoutPane, 0.0);
            AnchorPane.setLeftAnchor(layoutPane, 0.0);
            AnchorPane.setRightAnchor(layoutPane, 0.0);
            rendererContext.setRenderSurface(driftFXSurface);
            driftFXSurface.requestFocus();
        });
        rendererContext.setPrimaryStage(primaryStage);

        fractalsListView.setItems(viewModel.getFractalsList());
        cellFactory = CachedResizableCellFactory.createForFxmlView(FractalsListItemView.class);
        fractalsListView.setCellFactory(cellFactory);
        viewModel.selectedFractalProperty().bind(fractalsListView.getSelectionModel().selectedItemProperty());

    }
}
