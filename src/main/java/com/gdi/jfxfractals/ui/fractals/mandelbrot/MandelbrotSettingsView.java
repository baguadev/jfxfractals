package com.gdi.jfxfractals.ui.fractals.mandelbrot;


import com.google.inject.Inject;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MandelbrotSettingsView  implements FxmlView<MandelbrotSettingsViewModel>, Initializable {

    @InjectContext
    Context context;

    @Inject
    Stage primaryStage;

    @InjectViewModel
    private MandelbrotSettingsViewModel viewModel;

    @FXML
    Slider iterations;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        iterations.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    viewModel.setMaxIter(newValue);
                    //l.setText("value: " + newValue);
                });

    }


}
