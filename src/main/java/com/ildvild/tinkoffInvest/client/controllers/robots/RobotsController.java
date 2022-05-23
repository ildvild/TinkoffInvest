package com.ildvild.tinkoffInvest.client.controllers.robots;

import com.ildvild.tinkoffInvest.server.robots.AbstractRobot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component("clientRobotsController")
public class RobotsController {

    private final com.ildvild.tinkoffInvest.server.controllers.RobotsController serverRobotsController;

    private final List<com.ildvild.tinkoffInvest.server.robots.AbstractRobot> robots;

    public RobotsController(com.ildvild.tinkoffInvest.server.controllers.RobotsController serverRobotsController,
                            List<AbstractRobot> robots) {
        this.serverRobotsController = serverRobotsController;
        this.robots = robots;
    }

    public List<Robot> getRobots() {
        List<Robot> result = new ArrayList<>();
        robots.forEach(r -> result.add(convert(r)));
        return result;
    }

    public void update(Robot robot) {
        //
    }

    public Optional<Robot> get(UUID robotId) {
        return robots.stream().filter(r -> robotId.equals(r.getId())).map(RobotsController::convert).findFirst();
    }

    private static Robot convert(com.ildvild.tinkoffInvest.server.robots.AbstractRobot robot) {
        Robot r = new Robot();
        r.setId(robot.getId());
        r.setAccountId(robot.getAccountId());
        r.setName(robot.getName());
        r.setDescription(robot.getDescription());
        r.setState(robot.isWorking() ? State.WORKING : State.STOP);

        return r;
    }

    public void start(Robot robot) {
        serverRobotsController.start(robot.getId());
        robot.setState(State.WORKING);
    }

    public void stop(Robot robot) {
        serverRobotsController.stop(robot.getId());
        robot.setState(State.STOP);
    }
}
