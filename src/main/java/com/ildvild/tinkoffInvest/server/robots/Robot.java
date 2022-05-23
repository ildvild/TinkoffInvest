package com.ildvild.tinkoffInvest.server.robots;

import com.ildvild.tinkoffInvest.server.robots.exceptions.RobotStateException;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Общий интервейс, описывающий логику робота
 */
public interface Robot {

    /**
     * Уникальный ид робота
     */
    default UUID getId() {
        return null;
    }

    /**
     * Ид брокерского счета
     */
    String getAccountId();

    /**
     * Название робота
     */
    String getName();

    /**
     * Описание робота
     */
    String getDescription();

    /**
     * Состояние работы робота
     */
    State getState();

    /**
     * Запуск робота
     */
    void start();

    /**
     * Остановка робота
     */
    void stop();

    /**
     * Робот запущен
     */
    boolean isWorking();

    /**
     * Робот остановлен
     */
    boolean isStopped();

    /**
     * Получение прибыли портфеля, привязанного к роботу
     */
    BigDecimal getPortfolioExpectedYield(String figi, HistoricCandle candle) throws RobotStateException;

    /**
     * Торговля
     */
    default void processCandle(String figi, Candle candle) throws RobotStateException {
        //реализовать в роботе для торговли
    }

    /**
     * Торговля на исторических свечах
     */
    default void processCandle(String figi, HistoricCandle candle) throws RobotStateException {
        //реализовать в роботе для тестирования на исторических данных
    }
}
