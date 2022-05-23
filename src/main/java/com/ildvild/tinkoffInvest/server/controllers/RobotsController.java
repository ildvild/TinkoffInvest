package com.ildvild.tinkoffInvest.server.controllers;

import com.ildvild.tinkoffInvest.server.robots.Robot;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component("serverRobotsController")
public class RobotsController {

    @Getter
    private final List<Robot> robots;

    private final OrdersController ordersController;

    public RobotsController(List<Robot> robots, OrdersController ordersController) {
        this.robots = robots;
        this.ordersController = ordersController;
        ordersController.subscribeOrders(robots.stream().map(Robot::getAccountId).collect(Collectors.toList()));
    }

    public void start(UUID robotId) {
        Optional<Robot> robot = robots.stream().filter(r -> robotId.equals(r.getId())).findFirst();
        if (robot.isPresent()) {
            robot.get().start();
        } else {
            throw new RuntimeException("Робот не найден!");
        }
    }

    public void startAll() {
        robots.forEach(Robot::start);
    }

    public void stop(UUID robotId) {
        Optional<Robot> robot = robots.stream().filter(r -> robotId.equals(r.getId())).findFirst();
        if (robot.isPresent()) {
            robot.get().stop();
        } else {
            throw new RuntimeException("Робот не найден!");
        }
    }

    public void stopAll() {
        robots.forEach(Robot::stop);
    }

}
