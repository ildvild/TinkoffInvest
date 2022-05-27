package com.ildvild.tinkoffInvest.client.views.robots;

import com.ildvild.tinkoffInvest.client.controllers.Account.Account;
import com.ildvild.tinkoffInvest.client.controllers.Account.AccountsController;
import com.ildvild.tinkoffInvest.client.controllers.robots.Robot;
import com.ildvild.tinkoffInvest.client.controllers.robots.RobotsController;
import com.ildvild.tinkoffInvest.client.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ildvild.tinkoffInvest.client.views.common.ComponentsHelper.*;
import static ru.tinkoff.piapi.contract.v1.AccountStatus.ACCOUNT_STATUS_OPEN_VALUE;

@PageTitle("Роботы")
@Route(value = "robots/:id?/:action?(edit)", layout = MainLayout.class)
@UIScope
@SpringComponent
public class RobotsView extends Div implements BeforeEnterObserver {

    private final String ROBOT_ID = "id";
    private final String ROBOT_EDIT_ROUTE_TEMPLATE = "robots/%s/edit";

    private Grid<Robot> grid = new Grid<>(Robot.class, false);
    private Div editorLayoutDiv = new Div();

    private Select<Account> account = new Select<>();

    private Button cancel = newCancelButton();
    private Button save = newSaveButton();
    private Button run = new Button("Запустить");
    private Button stop = new Button("Остановить");

    private Robot robot;

    private final RobotsController robotsController;
    private final AccountsController clientAccountsController;

    public RobotsView(RobotsController robotsController, AccountsController clientAccountsController) {
        this.robotsController = robotsController;
        this.clientAccountsController = clientAccountsController;
        addClassNames("robots-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        setupGrid();
        createEditorLayout(splitLayout);
        setupEditorLayout();
        splitLayout.setSplitterPosition(70);
        add(splitLayout);
    }

    private void setupGrid() {
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES);
        grid.addColumn("name").setHeader("Имя").setSortable(true).setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(Span::new, statusComponentUpdater)).setHeader("Статус").setSortable(true);

        grid.setItems(robotsController.getRobots());

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ROBOT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                UI.getCurrent().navigate(RobotsView.class);
            }
        });
    }

    private void setupEditorLayout() {
        editorLayoutDiv.setEnabled(false);

        cancel.addClickListener(e -> showInDevelopmentNotification());
        save.addClickListener(e -> showInDevelopmentNotification());

        run.addClickListener(e -> {
            Notification.show("Запуск робота [" + robot.getName() + "]...");
            robotsController.start(robot);
            run.setEnabled(robot.isStopped());
            stop.setEnabled(robot.isWorking());
            grid.getDataProvider().refreshItem(robot);
            Notification.show("Робот [" + robot.getName() + "] запущен!");
        });

        stop.addClickListener(e -> {
            Notification.show("Остановка робота [" + robot.getName() + "]...");
            robotsController.stop(robot);
            run.setEnabled(robot.isStopped());
            stop.setEnabled(robot.isWorking());
            grid.getDataProvider().refreshItem(robot);
            Notification.show("Робот [" + robot.getName() + "] остановлен!");
        });
    }

    private static final SerializableBiConsumer<Span, Robot> statusComponentUpdater = (span, robot) -> {
        String theme = String.format("badge %s", robot.isWorking() ? "success" : "error");
        span.getElement().setAttribute("theme", theme);
        span.setText(robot.getState().getName());
    };

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> robotId = event.getRouteParameters().get(ROBOT_ID).map(UUID::fromString);
        if (robotId.isPresent()) {
            Optional<Robot> robotFromBackend = robotsController.get(robotId.get());
            if (robotFromBackend.isPresent()) {
                populateForm(robotFromBackend.get());
            } else {
                refreshGrid();
                event.forwardTo(RobotsView.class);
            }
        } else {
            refreshGrid();
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        account.setEnabled(false);
        account.setLabel("Брокерский счёт");
        account.setItemLabelGenerator(Account::getName);
        account.setItemEnabledProvider(a -> ACCOUNT_STATUS_OPEN_VALUE == a.getStatus());
        Component[] fields = new Component[]{account};

        FormLayout formLayout = new FormLayout();
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout saveCancelButtonLayout = new HorizontalLayout();
        saveCancelButtonLayout.setClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        saveCancelButtonLayout.add(save, cancel);

        run.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        run.setWidthFull();

        stop.addThemeVariants(ButtonVariant.LUMO_ERROR);
        stop.setWidthFull();
        stop.setEnabled(false);

        VerticalLayout allButtonLayout = new VerticalLayout(saveCancelButtonLayout, run, stop);
        editorLayoutDiv.add(allButtonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        editorLayoutDiv.setEnabled(false);
        grid.select(null);
        account.setValue(null);
    }

    private void populateForm(Robot value) {
        this.robot = value;
        List<Account> accounts = clientAccountsController.getAccounts();
        account.setItems(accounts);
        Optional<Account> account = accounts.stream().filter(a -> this.robot.getAccountId().equals(a.getId())).findFirst();
        if (account.isPresent()) {
            this.account.setValue(account.get());
        } else {
            this.account.setValue(null);
        }
        editorLayoutDiv.setEnabled(true);
    }
}