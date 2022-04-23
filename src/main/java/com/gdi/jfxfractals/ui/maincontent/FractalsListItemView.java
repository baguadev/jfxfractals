package com.gdi.jfxfractals.ui.maincontent;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FractalsListItemView  implements FxmlView<FractalsListItemViewModel> {

    @InjectViewModel
    private FractalsListItemViewModel viewModel;

    @FXML
    public Label label;

    @FXML
    public VBox mRoot;


    public void initialize(){
        label.textProperty().bindBidirectional(viewModel.titleProperty());
    }

}
