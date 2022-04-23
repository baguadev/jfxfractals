package com.gdi.jfxfractals.ui.maincontent;

import com.gdi.jfxfractals.model.FractalInfo;
import com.gdi.jfxfractals.renderimpl.LorenzAttractorRenderer;
import com.gdi.jfxfractals.renderimpl.MandelbrotRenderer;
import com.gdi.jfxfractals.renderimpl.MandelbrotRenderer2;
import com.gdi.jfxfractals.renderimpl.MandelbrotShaderRenderer;
import com.gdi.jfxfractals.ui.scope.FractalSettingsScope;
import com.google.inject.Inject;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ScopeProvider(scopes = {FractalSettingsScope.class})
public class MainContentViewModel implements ViewModel {

    public static final String SELECT_FRACTAL = "SELECT_FRACTAL";

    private static final Logger log = LoggerFactory.getLogger(MainContentViewModel.class);

    private ObservableList<FractalsListItemViewModel> fractalsList = FXCollections.observableArrayList();
    private ObjectProperty<FractalsListItemViewModel> selectedFractal = new SimpleObjectProperty<>();

    @Inject
    // Notification Center
    private NotificationCenter notificationCenter;

    @InjectScope
    private FractalSettingsScope fractalSettingsScope;

    public void initialize() {

        initFractalsList();
        selectedFractal.addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                try {
                    FractalInfo link = newValue.getFractalInfo();
                    publish(SELECT_FRACTAL, link);

                } catch (Exception e) {
                    log.error("Error getting link info:", e);
                }
            }
        });

    }

    private void initFractalsList() {
        FractalInfo fractalInfo = new FractalInfo("Sample fractal", "Sample description", MandelbrotRenderer::new);
        FractalInfo fractalInfo1 = new FractalInfo("Mandelbrot fractal", "Mandelbrot Fractal", MandelbrotRenderer2::new);
        FractalInfo fractalInfo2 = new FractalInfo("Mandelbrot shaders", "Mandelbrot Fractal with shader", MandelbrotShaderRenderer::new);
        FractalInfo fractalInfo3 = new FractalInfo("Lorenz Attractor", "Lorenz Attractor", LorenzAttractorRenderer::new);
        FractalInfo fractalInfo4 = new FractalInfo("Diffusion aggregation", "Diffusion aggregation", LorenzAttractorRenderer::new);
        fractalsList.add(new FractalsListItemViewModel(fractalInfo));
        fractalsList.add(new FractalsListItemViewModel(fractalInfo1));
        fractalsList.add(new FractalsListItemViewModel(fractalInfo2));
        fractalsList.add(new FractalsListItemViewModel(fractalInfo3));
        fractalsList.add(new FractalsListItemViewModel(fractalInfo4));
    }

    public ObservableList<FractalsListItemViewModel> getFractalsList() {
        return fractalsList;
    }

    public void setFractalsList(ObservableList<FractalsListItemViewModel> fractalsList) {
        this.fractalsList = fractalsList;
    }

    public FractalsListItemViewModel getSelectedFractal() {
        return selectedFractal.get();
    }

    public ObjectProperty<FractalsListItemViewModel> selectedFractalProperty() {
        return selectedFractal;
    }

    public void setSelectedFractal(FractalsListItemViewModel selectedFractal) {
        this.selectedFractal.set(selectedFractal);
    }

    public FractalSettingsScope getFractalSettingsScope()
    {
        return fractalSettingsScope;
    }
}
