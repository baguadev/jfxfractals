package com.gdi.jfxfractals.ui.container;

import com.gdi.jfxfractals.ui.scope.MenuScope;
import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ScopeProvider;
import de.saxsys.mvvmfx.ViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ScopeProvider(scopes = {MenuScope.class})
public class MainContainerViewModel implements ViewModel {
    private static final Logger log = LoggerFactory.getLogger(MainContainerViewModel.class);

    @InjectScope
    MenuScope menuScope;


}
