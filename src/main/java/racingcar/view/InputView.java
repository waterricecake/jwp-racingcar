package racingcar.view;

import java.util.Scanner;
import java.util.regex.Pattern;

import static racingcar.option.Option.MAX_TRIAL_COUNT_LENGTH;

public class InputView {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Pattern NOT_INTEGER_PATTERN = Pattern.compile("\\D+");

    public static String inputCarNames() {
        System.out.println("경주할 자동차 이름을 입력하세요(이름은 쉼표(,)를 기준으로 구분).");
        return scanner.nextLine();
    }

    public static int inputTrialCount() {
        System.out.println("\n시도할 회수는 몇회인가요?");
        String input = scanner.nextLine();
        validateInteger(input);
        validateTrialCountUnderMaxInteger(input);
        return Integer.parseInt(input);
    }

    private static void validateInteger(String input) {
        if (NOT_INTEGER_PATTERN.matcher(input).matches()) {
            throw new IllegalArgumentException("시도 횟수는 정수여야 합니다");
        }
    }

    private static void validateTrialCountUnderMaxInteger(String input) {
        if (input.length() >= MAX_TRIAL_COUNT_LENGTH) {
            throw new IllegalArgumentException("시도 횟수는 999999999이하여야 합니다.");
        }
    }
}
