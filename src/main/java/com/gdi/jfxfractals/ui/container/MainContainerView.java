package com.gdi.jfxfractals.ui.container;

import com.google.inject.Inject;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXSnackbar;
import de.saxsys.mvvmfx.Context;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectContext;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.notifications.NotificationObserver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MainContainerView implements FxmlView<MainContainerViewModel>, Initializable {

    @Inject
    // Notification Center
    private NotificationCenter notificationCenter;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private BorderPane root;

    private JFXSnackbar snackbar;

    @InjectViewModel
    private MainContainerViewModel viewModel;

    @InjectContext
    private Context context;

    private NotificationObserver observer;

    private static final Logger log = LoggerFactory.getLogger(MainContainerView.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
