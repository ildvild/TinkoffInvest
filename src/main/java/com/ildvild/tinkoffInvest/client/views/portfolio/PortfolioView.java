package com.ildvild.tinkoffInvest.client.views.portfolio;

import com.ildvild.tinkoffInvest.client.controllers.Account.Account;
import com.ildvild.tinkoffInvest.client.controllers.Account.AccountsController;
import com.ildvild.tinkoffInvest.client.controllers.portfolio.Portfolio;
import com.ildvild.tinkoffInvest.client.controllers.portfolio.PortfolioController;
import com.ildvild.tinkoffInvest.client.controllers.positions.Position;
import com.ildvild.tinkoffInvest.client.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

import static com.ildvild.tinkoffInvest.client.views.common.ComponentsHelper.*;
import static com.ildvild.tinkoffInvest.client.views.common.FormatHelper.getPriceFormat;
import static com.ildvild.tinkoffInvest.client.views.common.FormatHelper.getQuantityFormat;
import static ru.tinkoff.piapi.contract.v1.AccountStatus.ACCOUNT_STATUS_OPEN_VALUE;

@Slf4j
@PageTitle("Портфель")
@Route(value = "portfolio", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@UIScope
@SpringComponent
public class PortfolioView extends Div {

    private Grid<Position> grid = new Grid<>(Position.class, false);
    private Label expectedYieldLabel = new Label();

    private final AccountsController accountsController;
    private final PortfolioController portfolioController;

    public PortfolioView(AccountsController accountsController, PortfolioController portfolioController) {
        this.accountsController = accountsController;
        this.portfolioController = portfolioController;

        addClassNames("portfolio-view");

        // Create UI
        setSizeFull();
        setupAccountsLayout();
        setupGrid();
    }

    private void setupAccountsLayout() {
        Select<Account> account = new Select<>();
        account.setPlaceholder("Выберите брокерский счёт");
        account.setItemLabelGenerator(Account::getName);
        account.setWidth("50%");
        account.getElement().getStyle().set("margin-left", "var(--lumo-space-s)");
        account.setErrorMessage("Выберите брокерский счёт");
        account.setItems(accountsController.getAccounts());
        account.setItemEnabledProvider(a -> ACCOUNT_STATUS_OPEN_VALUE == a.getStatus());
        account.addValueChangeListener(a -> refreshData(a.getValue()));

        Button button = newRefreshButton();
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getElement().getStyle().set("margin-right", "var(--lumo-space-s)");
        button.addClickListener(e -> {
            if (account.isEmpty()) {
                showErrorNotification("Выберите брокерский счёт");
            } else {
                refreshData(account.getValue());
            }
        });

        expectedYieldLabel.setWidth("30%");
        expectedYieldLabel.setText("Относительная доходность: -%");

        HorizontalLayout layout = new HorizontalLayout(account, expectedYieldLabel, button);

        add(layout);
    }

    private void setupGrid() {
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.addComponentColumn(position -> createTickerLayout(position.getInstrument().getIcon(),
                position.getInstrument().getName(), position.getInstrument().getTicker()))
                .setHeader("Название").setAutoWidth(true).setSortable(true).setComparator(d -> d.getInstrument().getTicker());
        grid.addColumn(new NumberRenderer<>(Position::getQuantity, getQuantityFormat()))
                .setHeader("Доступно").setAutoWidth(true)
                .setComparator(Position::getQuantity).setSortable(true).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(new NumberRenderer<>(Position::getCurrentPrice, getPriceFormat()))
                .setHeader("Цена").setAutoWidth(true)
                .setComparator(Position::getCurrentPrice).setSortable(true).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(Position::getCurrency)
                .setHeader("Валюта").setAutoWidth(true).setSortable(true);
        grid.addColumn(new NumberRenderer<>(Position::getAveragePositionPrice, getPriceFormat()))
                .setHeader("Средняя цена").setAutoWidth(true).setSortable(true)
                .setComparator(Position::getAveragePositionPrice).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(new NumberRenderer<>(Position::getExpectedYield, getPriceFormat()))
                .setHeader("Доход").setAutoWidth(true)
                .setComparator(Position::getExpectedYield).setSortable(true).setTextAlign(ColumnTextAlign.END);

        add(grid);
    }

    private void refreshData(@NotNull Account account) {
        Portfolio portf = portfolioController.getPortfolio(account.getId());
        expectedYieldLabel.setText("Относительная доходность: " + getPriceFormat().format(portf.getExpectedYield()) + "%");
        grid.setItems(portf.getPositions());
    }

}
