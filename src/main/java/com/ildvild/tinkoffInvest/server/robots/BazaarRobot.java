package com.ildvild.tinkoffInvest.server.robots;

import com.ildvild.tinkoffInvest.server.robots.exceptions.RobotStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.util.UUID;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "bazaar", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BazaarRobot extends AbstractRobot {

    public static final String ROBOT_ID = "09a7b94e-78eb-41a7-b673-03bccc9c8fa6";

    private final String accountId;

    public BazaarRobot(@Value("${bazaar.accountId}") String accountId) {
        super(accountId);
        this.accountId = accountId;
        start();//
    }

    @Override
    public String getName() {
        return "Базар";
    }

    @Override
    public UUID getId() {
        return UUID.fromString(ROBOT_ID);
    }

    @Override
    public void processCandle(String figi, HistoricCandle candle) throws RobotStateException {
        super.processCandle(figi, candle);
        // todo В разработке
    }
}
