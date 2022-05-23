package com.ildvild.tinkoffInvest.server.telegram;

import com.ildvild.tinkoffInvest.server.controllers.OperationsController;
import com.ildvild.tinkoffInvest.server.controllers.RobotsController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tinkoff.piapi.core.models.Portfolio;

@Slf4j
@Component
@PropertySource(value = "classpath:telegram-config.properties")
public class TinkoffInvestBot extends TelegramLongPollingBot {

    public static final String START_COMMAND = "/start";
    public static final String STOP_COMMAND = "/stop";
    public static final String PORTFOLIO_COMMAND = "/portfolio";
    public static final String ROBOTS_COMMAND = "/robots";

    @Value("${telegram-bot-name}")
    private String botName;

    @Value("${telegram-bot-token}")
    private String botToken;

    private final RobotsController serverRobotsController;
    private final OperationsController serverOperationsController;

    public TinkoffInvestBot(RobotsController serverRobotsController, OperationsController serverOperationsController) {
        this.serverRobotsController = serverRobotsController;
        this.serverOperationsController = serverOperationsController;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String command = update.getMessage().getText();
            switch (command) {
                case START_COMMAND:
                    //sendMessage(update.getMessage().getChatId(), "Оповещение об операциях запущено!");
                    break;
                case STOP_COMMAND:
                    //sendMessage(update.getMessage().getChatId(), "Оповещение об операциях остановлено!");
                    break;
                case ROBOTS_COMMAND:
                    sendMessage(update.getMessage().getChatId(), getRobotsInfo());
                    break;
                case PORTFOLIO_COMMAND:
                    sendMessage(update.getMessage().getChatId(), getPortfolioInfo());
                    break;
            }
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRobotsInfo() {
        StringBuilder result = new StringBuilder("Роботы:\n");
        serverRobotsController.getRobots().forEach(r -> result.append(r.getName()).append(": [").append(r.getState().getName()).append("]\n"));
        return result.toString();
    }

    private String getPortfolioInfo() {
        StringBuilder result = new StringBuilder("Портфель:\n");
        serverRobotsController.getRobots().forEach(r -> {
            Portfolio portfolio = serverOperationsController.getPortfolio(r.getAccountId());
            portfolio.getPositions().forEach(p->
                    result.append(p.getFigi() + " [" + p.getQuantity() + "]\n")
            );
        });
        return result.toString();
    }
}
