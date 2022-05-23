package com.ildvild.tinkoffInvest.client.views.settings;

import com.ildvild.tinkoffInvest.client.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import static com.ildvild.tinkoffInvest.client.views.common.ComponentsHelper.*;

@PageTitle("Настройки")
@Route(value = "settings", layout = MainLayout.class)
@Uses(Icon.class)
@UIScope
public class SettingsView extends Div {

    private TextField token = new TextField("Токен");
    private TextField telegramBotToken = new TextField("Токен telegram");

    private Button cancel = newCancelButton();
    private Button save = newSaveButton();

    public SettingsView() {
        addClassName("settings-view");

        add(createFormLayout());
        add(createButtonLayout());

        cancel.addClickListener(e -> showInDevelopmentNotification());
        save.addClickListener(e -> showInDevelopmentNotification());
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        token.setErrorMessage("Укажите токен");
        telegramBotToken.setErrorMessage("Укажите токен");
        formLayout.add(token, telegramBotToken);
        formLayout.setEnabled(false); //todo В разработке
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }
}
