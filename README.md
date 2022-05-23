# Клиент для Тинькофф Инвестиции

Клиент для Тинькофф Инвестиции представляет собой платфлому для запуска торговых роботов.
Демо интерфейса доступно в папке `screenshot`

## Возможности

- Создание и запуск торговых роботов
- Отображение портфеля и другой биржевой информации
- Получение общей информации через бота telegram

## Требования

- Java 11 и выше
- Gradle

## Стек технологий

- [Vaadin]
- [Telegram]
- [Tinkoff]

## Торговые роботы
- `Купить, усреднить, продать` - робот покупает первую бумагу по текущей цене, при падении средней цены более, чем на заданный процент, усредняет его, а при повышении цены бумаги более, чем на заданный процент, продает её.

## Настройка робота
На данный момент настройка осуществляется через файл `src/main/resources/robots-config.properties`

## Создание нового робота
Для создания нового робота необходимо реализовать интерфейс и его методы `com.ildvild.tinkoffInvest.server.robots.Robot`

Пример робота - `com.ildvild.tinkoffInvest.server.robots.BuyAverageAndSell`

## Тестирование робота на исторических данных
Для тестирования робота необходимо создать потомка робота в пакете `\historic` с переопределенными методами и написать Unit-тест

Пример робота - `com.ildvild.tinkoffInvest.server.robots.historic.BuyAverageAndSellHistoric`
Пример теста - `com.ildvild.tinkoffInvest.server.robots.historic.BuyAverageAndSellHistoricTest#testBuyAverageAndSell`

## Сборка и запуск проекта

Скачайте себе исходники и выполните команду сборки

```sh
Генерация необходимых файлов
./gradlew clean vaadinPrepareFrontend
```

Далее запустите, выполнив команду
```sh
./gradlew bootRun
```
Также запуск проекта можно осуществить через Inellij IDEA

Перед первоначальным запуском укажите валидный токен в файле `src/main/resources/server-config.properties`

Запуск робота осуществляется через пользовательский интерфейс во вкладке `Роботы` по кнопке `Старт` 

## Интеграция с Telegram

1. Создайте своего бота с помощью стандартных средств Telegram
2. Выполните настройку в файле `src/main/resources/telegram-config.properties`
    ```
    #Включение/выключение телеграмм бота
    telegrambots.enabled = true 
    ```
    ```
    #Имя бота
    telegram-bot-name=TinkoffInvestClient
    ```
    ```
    #Токен бота
    telegram-bot-token=5300548211:AAEznXTHFuJQfrLly_XJTPSgnegYs2s_kMc
    ```

Поддерживаются следующие команды
- `/robots` - получение статуса роботов
- `/portfolio` - получение краткой информации о портфелях, на которых торгуют роботы

Расширение команд производится в методе `com.ildvild.tinkoffInvest.server.telegram.TinkoffInvestBot#onUpdateReceived`

## Структура проекта

Проект логически разделен на клиентскую часть и серверную.

Клиентская часть содержит классы UI и контроллеры к ним
- `src/main/java/com/ildvild/tinkoffInvest/client/controllers` - контроллеры
- `src/main/java/com/ildvild/tinkoffInvest/client/views` - UI

Серверная часть содержит контроллеры для интеграции с Tinkoff Invest API, модель данных для создания роботов и классы для интеграции с telegram
- `src/main/java/com/ildvild/tinkoffInvest/server/controllers` - контроллеры
- `src/main/java/com/ildvild/tinkoffInvest/server/robots` - торговые роботы
- `src/main/java/com/ildvild/tinkoffInvest/server/telegram` - telegram

## Лицензия

Apache 2.0

   [Vaadin]: <https://vaadin.com/>
   [Telegram]: <https://github.com/rubenlagus/TelegramBots/tree/master/telegrambots-abilities>
   [Tinkoff]: <https://github.com/Tinkoff/investAPI>


