package racingcar.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import racingcar.dao.GameDAO;
import racingcar.dao.GameLogDAO;
import racingcar.dao.WinnersDAO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static racingcar.option.Option.MIN_TRIAL_COUNT;

@Service
public class SpringService {
    private List<Car> cars;
    private final MoveChance moveChance;
    private final GameDAO gameDAO;
    private final GameLogDAO gameLogDAO;
    private final WinnersDAO winnersDAO;

    public SpringService(final GameDAO gameDAO,final GameLogDAO gameLogDAO, final WinnersDAO winnersDAO) {
        this.gameDAO = gameDAO;
        this.gameLogDAO =gameLogDAO;
        this.winnersDAO = winnersDAO;
        this.moveChance = new RandomMoveChance();
    }

    public void setUpGame(String names) {
        this.cars = Stream.of(names.split(","))
                .map(Car::new)
                .collect(Collectors.toList());
    }

    public List<Car> findWinners() {
        int maxPosition = findMaxPosition();
        return cars.stream()
                .filter(car -> car.hasSamePositionWith(maxPosition))
                .collect(Collectors.toList());
    }

    private int findMaxPosition() {
        int maxPosition = 0;
        for (Car car : cars) {
            maxPosition = car.selectMaxPosition(maxPosition);
        }
        return maxPosition;
    }

    public void playOnce() {
        for (Car car : cars) {
            car.move(moveChance);
        }
    }

    public List<Car> getCars() {
        return Collections.unmodifiableList(cars);
    }

    public void play(int trialCount) {
        validateNotNegativeInteger(trialCount);
        int gameNumber = gameDAO.saveGame(trialCount);
        playMultipleTimes(trialCount);
        for (Car car : cars) {
            gameLogDAO.insert(gameNumber, car.getName(), car.getPosition());
        }
        for (Car car : findWinners()) {
            winnersDAO.insert(gameNumber, car.getName());
        }
    }

    private void validateNotNegativeInteger(int trialCount) {
        if (trialCount < MIN_TRIAL_COUNT) {
            throw new IllegalArgumentException("[ERROR] 시도횟수는 음수이면 안됩니다.");
        }
    }

    private void playMultipleTimes(int trialCount) {
        for (int i = 0; i < trialCount; i++) {
            playOnce();
        }
    }
}
