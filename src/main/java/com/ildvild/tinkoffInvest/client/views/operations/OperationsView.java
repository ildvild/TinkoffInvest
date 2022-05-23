package com.ildvild.tinkoffInvest.client.views.operations;

import com.ildvild.tinkoffInvest.client.controllers.Account.AccountsController;
import com.ildvild.tinkoffInvest.client.controllers.operations.Operation;
import com.ildvild.tinkoffInvest.client.controllers.operations.OperationsController;
import com.ildvild.tinkoffInvest.client.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PageTitle("Сделки")
@Route(value = "operations", layout = MainLayout.class)
@UIScope
@SpringComponent
public class OperationsView extends Div implements BeforeEnterObserver {

    private Grid<Operation> grid = new Grid<>(Operation.class, false);

    private final AccountsController clientAccountsController;

    private final OperationsController clientOperationsController;


    public OperationsView(AccountsController clientAccountsController, OperationsController clientOperationsController) {
        this.clientAccountsController = clientAccountsController;
        this.clientOperationsController = clientOperationsController;

        addClassNames("operations-view");

        // Create UI
        setSizeFull();
        setupAccountsLayout();
        setupGrid();

    }

    private void setupAccountsLayout() {

    }

    private void setupGrid() {
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);

        add(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }
}
