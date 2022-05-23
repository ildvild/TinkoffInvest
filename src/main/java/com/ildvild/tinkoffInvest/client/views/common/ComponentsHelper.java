package com.ildvild.tinkoffInvest.client.views.common;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ComponentsHelper {

    public static Button newCancelButton() {
        return new Button("Отмена");
    }

    public static Button newSaveButton() {
        return new Button("Сохранить");
    }

    public static Button newRefreshButton() {
        return new Button("Обновить");
    }

    public static HorizontalLayout createTickerLayout(String iconUrl, String name, String ticker) {
        Avatar icon = new Avatar();
        icon.setImage(iconUrl);
        icon.setName(name);

        Span tickerSpan = new Span(ticker);

        Span nameSpan = new Span(name);
        nameSpan.addClassName("row-item-name");
        nameSpan.setWidthFull();

        VerticalLayout verticalLayout = new VerticalLayout(tickerSpan, nameSpan);
        verticalLayout.setClassName("row-item-vertical-layout");

        HorizontalLayout horizontalLayout = new HorizontalLayout(icon, verticalLayout);
        horizontalLayout.setClassName("row-item-horizontal-layout");

        return horizontalLayout;
    }

    public static void showErrorNotification(String text) {
        Notification notification = new Notification(text);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_END);
        notification.open();
    }

    public static void showInDevelopmentNotification() {
        Notification notification = new Notification("В разработке");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_END);
        notification.open();
    }
}
