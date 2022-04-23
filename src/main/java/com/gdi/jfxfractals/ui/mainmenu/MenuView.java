package com.gdi.jfxfractals.ui.mainmenu;

import com.gdi.jfxfractals.ui.container.MainContainerViewModel;
import de.saxsys.mvvmfx.FxmlView;
import com.google.inject.Inject;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.notifications.NotificationCenter;
import de.saxsys.mvvmfx.utils.notifications.NotificationObserver;
import de.saxsys.mvvmfx.utils.notifications.WeakNotificationObserver;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MenuView implements FxmlView<MenuViewModel> {

    private static final Logger log = LoggerFactory.getLogger(MenuView.class);

    @Inject
    Stage primaryStage;

    @InjectViewModel
    private MenuViewModel viewModel;

    @Inject
    // Notification Center
    private NotificationCenter notificationCenter;

    @FXML
    private MenuBar menuBar;

    @FXML
    private VBox menuPane;

    @FXML
    private StackPane optionsBurger;

    @FXML
    private JFXHamburger titleBurger;


    private boolean isMenuShowed;


    private JFXPopup toolbarPopup;

    private NotificationObserver observer;

    public void initialize() {
        log.debug("init main menu view");
        hideMenuBar();
        viewModel.subscribe(MenuViewModel.SHOW_HIDE_MENU, (k, v) -> showHideMenuBar());

        observer = (key, payload) -> runHamburgerAnimation((Boolean)payload[0]);
        //notificationCenter.subscribe(MainContainerViewModel.SHOW_HIDE_DRAWER, new WeakNotificationObserver(observer));
    }

    private void runHamburgerAnimation(Boolean isOpen) {
        final Transition animation = titleBurger.getAnimation();
        animation.setRate(isOpen.booleanValue() ? 1 : -1);
        animation.play();
    }

    private void showHideMenuBar() {
        if (isMenuShowed) {
            hideMenuBar();
        } else {
            showMenuBar();
        }
    }

    private void showMenuBar() {
        menuPane.getChildren().add(0, menuBar);
        isMenuShowed = true;
    }

    private void hideMenuBar() {
        menuPane.getChildren().remove(menuBar);
        isMenuShowed = false;
    }

    public void quit(ActionEvent actionEvent) {
        primaryStage.close();
    }

    public void about(ActionEvent actionEvent) {
    }

    public void hamburgerClicked(MouseEvent mouseEvent) {
        viewModel.notifyShowHideSidePane();
    }

    public void optionsBurgerClicked(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ToolbarMenuPopup.fxml"));
        try {
            toolbarPopup = new JFXPopup(loader.load());
            toolbarPopup.show(optionsBurger,
                    JFXPopup.PopupVPosition.TOP,
                    JFXPopup.PopupHPosition.RIGHT,
                    -12,
                    15);
        }
        catch (IOException e) {
            log.error("Error clicking options burger:", e);
        }
    }

}
