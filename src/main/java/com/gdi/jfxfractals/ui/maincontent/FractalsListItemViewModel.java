package com.gdi.jfxfractals.ui.maincontent;

import com.gdi.jfxfractals.model.FractalInfo;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FractalsListItemViewModel implements ViewModel {

    private FractalInfo fractalInfo;
    private StringProperty nameProperty = new SimpleStringProperty();
    private StringProperty starsProperty = new SimpleStringProperty();


    public FractalsListItemViewModel(FractalInfo fractalInfo) {
        this.fractalInfo = fractalInfo;
        nameProperty.set(fractalInfo.getName());

    }

    public FractalInfo getFractalInfo() {
        return fractalInfo;
    }

    public Property<String> titleProperty() {
        return nameProperty;
    }

    public Property<String> starsProperty() {
        return starsProperty;
    }
}
