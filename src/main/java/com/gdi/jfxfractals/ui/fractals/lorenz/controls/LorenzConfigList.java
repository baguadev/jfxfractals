package com.gdi.jfxfractals.ui.fractals.lorenz.controls;

import com.gdi.jfxfractals.common.fx.control.EmptySelectionModel;
import com.gdi.jfxfractals.common.fx.field.DoubleField;
import com.gdi.jfxfractals.common.fx.field.IntegerField;
import com.gdi.jfxfractals.common.util.GridUtil;
import com.gdi.jfxfractals.renderimpl.lorenz.math.Function;
import com.gdi.jfxfractals.renderimpl.lorenz.math.Series;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class LorenzConfigList extends ListView<LorenzConfig> {
    public LorenzConfigList() {
        setCellFactory(l -> new ConfigCell());
        setSelectionModel(new EmptySelectionModel<>());
    }

    private class ConfigCell extends ListCell<LorenzConfig> {
        private final ColorPicker color = new ColorPicker();
        private final DoubleField sigma = new DoubleField(-10000, 10000, Function.DEFAULT_SIGMA), rho = new DoubleField(-10000, 10000, Function.DEFAULT_RHO), beta = new DoubleField(-10000, 10000, Function.DEFAULT_BETA),
                x0 = new DoubleField(-10000, 10000, 0), y0 = new DoubleField(-10000, 10000, 0), z0 = new DoubleField(-10000, 10000, 0), h = new DoubleField(-10, 10, 0.001);
        private final IntegerField points = new IntegerField(0, 10_000_000), speed = new IntegerField(1, 10000);
        private final Button connect = new Button();
        private final ProgressBar progressBar = new ProgressBar();

        private final GridPane pane = new GridPane();

        public ConfigCell() {
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            this.color.valueProperty().addListener((v, oldV, newV) -> getItem().series.setColor(newV));

            this.sigma.valueProperty().addListener((v, oldV, newV) -> getItem().sigma = newV.doubleValue());
            this.rho.valueProperty().addListener((v, oldV, newV) -> getItem().rho = newV.doubleValue());
            this.beta.valueProperty().addListener((v, oldV, newV) -> getItem().beta = newV.doubleValue());
            this.x0.valueProperty().addListener((v, oldV, newV) -> getItem().x0 = newV.doubleValue());
            this.y0.valueProperty().addListener((v, oldV, newV) -> getItem().y0 = newV.doubleValue());
            this.z0.valueProperty().addListener((v, oldV, newV) -> getItem().z0 = newV.doubleValue());
            this.h.valueProperty().addListener((v, oldV, newV) -> getItem().h = newV.doubleValue());

            this.points.valueProperty().addListener((v, oldV, newV) -> getItem().points = newV.intValue());
            this.speed.valueProperty().addListener((v, oldV, newV) -> getItem().speed = newV.intValue());

            this.connect.setOnAction(e -> {
                Series s = getItem().series;
                if (s.connect) {
                    s.connect = false;
                    this.connect.setText("Connect");
                } else {
                    s.connect = true;
                    this.connect.setText("Disconnect");
                }
            });

            Button remove = new Button("Remove");
            remove.setOnAction(e -> getItems().remove(getIndex()));

            remove.setMaxWidth(Double.MAX_VALUE);
            remove.setPrefWidth(100);
            this.connect.setMaxWidth(Double.MAX_VALUE);
            this.connect.setPrefWidth(100);
            this.color.setMaxWidth(Double.MAX_VALUE);
            this.color.setPrefWidth(100);

            this.progressBar.setMaxWidth(Double.MAX_VALUE);

            this.pane.add(new Label("Color:"), 0, 0);
            this.pane.add(this.color, 1, 0);
            this.pane.add(this.connect, 3, 0);
            this.pane.add(remove, 5, 0);

            this.pane.addRow(1, new Label("Not:"), this.h, new Label("Points:"), this.points, new Label("Speed:"), this.speed);
            this.pane.addRow(2, new Label("σ:"), this.sigma, new Label("ρ:"), this.rho, new Label("β:"), this.beta);
            this.pane.addRow(3, new Label("x0:"), this.x0, new Label("y0:"), this.y0, new Label("z0:"), this.z0);

            this.pane.add(this.progressBar, 0, 4, 6, 1);

            this.pane.setVgap(5);
            this.pane.setHgap(5);

            this.pane.getColumnConstraints().addAll(GridUtil.createColumn(10), GridUtil.createColumn(30), GridUtil.createColumn(10), GridUtil.createColumn(30), GridUtil.createColumn(10), GridUtil.createColumn(30));
            this.pane.getRowConstraints().addAll(GridUtil.createRow(), GridUtil.createRow(), GridUtil.createRow(), GridUtil.createRow(), GridUtil.createRow());
        }

        @Override
        protected void updateItem(LorenzConfig item, boolean empty) {
            super.updateItem(item, empty);

            // Unbind previous value
            this.progressBar.progressProperty().unbind();

            if (empty || item == null) {
                setGraphic(null);
            } else {
                // Update content
                this.connect.setText(item.series.connect ? "Disconnect" : "Connect");
                this.color.setValue(item.series.getColor());
                this.sigma.setValue(item.sigma);
                this.rho.setValue(item.rho);
                this.beta.setValue(item.beta);
                this.x0.setValue(item.x0);
                this.y0.setValue(item.y0);
                this.z0.setValue(item.z0);
                this.h.setValue(item.h);
                this.points.setValue(item.points);
                this.speed.setValue(item.speed);
                this.progressBar.progressProperty().bind(item.progressListener.progressProperty());

                setGraphic(this.pane);
            }
        }
    }
}
