package com.ildvild.tinkoffInvest.client.views.debug;

import com.ildvild.tinkoffInvest.client.views.MainLayout;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Отладка")
@Route(value = "debug", layout = MainLayout.class)
public class DebugView extends VerticalLayout {

    public DebugView() {
        add(new Paragraph("В разработке..."));

        //todo реализовать просмотр лога
    }

}
