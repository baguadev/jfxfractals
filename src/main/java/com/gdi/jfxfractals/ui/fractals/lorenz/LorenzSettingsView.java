package com.gdi.jfxfractals.ui.fractals.lorenz;

import com.gdi.jfxfractals.common.util.GridUtil;
import com.gdi.jfxfractals.ui.fractals.lorenz.controls.LorenzConfig;
import com.gdi.jfxfractals.ui.fractals.lorenz.controls.LorenzConfigList;
import com.gdi.jfxfractals.ui.fractals.mandelbrot.MandelbrotSettingsViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.net.URL;
import java.util.ResourceBundle;

public class LorenzSettingsView implements FxmlView<LorenzSettingsViewModel>, Initializable {

    @FXML
    GridPane grdPaneControls;

    @FXML
    LorenzConfigList configList;

    @InjectViewModel
    private LorenzSettingsViewModel viewModel;

    @FXML
    Button butAdd;

    @FXML
    Button butClear;

    @FXML
    Button butCalculate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        configList.getItems().addListener((ListChangeListener<LorenzConfig>) c -> {
            while (c.next()) {
                for (LorenzConfig cfg : c.getRemoved()) {
                    cfg.stop();
              //      processingLoop.updatables.remove(cfg);
     //               chart.data.remove(cfg.series);
                    viewModel.removeConfig(cfg);
                }

                for (LorenzConfig cfg : c.getAddedSubList()) {
           //         chart.data.add(cfg.series);
       //             processingLoop.updatables.add(cfg);
                    viewModel.addConfig(cfg);
                }
            }
        });

        grdPaneControls.getColumnConstraints().addAll(GridUtil.createColumn(33), GridUtil.createColumn(33), GridUtil.createColumn(33));
        grdPaneControls.getRowConstraints().addAll(GridUtil.createRow(Priority.ALWAYS), GridUtil.createRow());
    }

    public void addConfigSettings(ActionEvent actionEvent) {
        configList.getItems().add(new LorenzConfig());
    }

    public void clearSettings(ActionEvent actionEvent) {
        configList.getItems().clear();
    }

    public void calculateParams(ActionEvent actionEvent) {
        for (LorenzConfig cfg : configList.getItems()) {
            cfg.start();
        }
    }
}
