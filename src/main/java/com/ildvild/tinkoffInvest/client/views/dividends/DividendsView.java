package com.ildvild.tinkoffInvest.client.views.dividends;

import com.ildvild.tinkoffInvest.client.controllers.dividends.Dividend;
import com.ildvild.tinkoffInvest.client.controllers.dividends.DividendsController;
import com.ildvild.tinkoffInvest.client.views.MainLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import static com.ildvild.tinkoffInvest.client.views.common.ComponentsHelper.createTickerLayout;

@PageTitle("Дивиденды")
@Route(value = "dividends", layout = MainLayout.class)
@UIScope
@SpringComponent
public class DividendsView extends Div implements AfterNavigationObserver {

    private final DividendsController dividendsController;

    private Grid<Dividend> grid = new Grid<>(Dividend.class, false);

    public DividendsView(DividendsController dividendsController) {
        this.dividendsController = dividendsController;
        addClassName("dividends-view");

        // Create UI
        setSizeFull();
        setupGrid();
    }

    private void setupGrid() {
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.addComponentColumn(dividend -> createTickerLayout(dividend.getInstrument().getIcon(),
                dividend.getInstrument().getName(), dividend.getInstrument().getTicker()))
                .setHeader("Название").setAutoWidth(true).setSortable(true).setComparator(d -> d.getInstrument().getTicker());
        grid.addColumn(Dividend::getDividendNet).setHeader("Сумма").setSortable(true)
                .setTextAlign(ColumnTextAlign.END);
        grid.addColumn(Dividend::getLastBuyDate).setHeader("Дата").setSortable(true);

        add(grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        grid.setItems(dividendsController.getDividendsByDefaultFigies());
    }
}
