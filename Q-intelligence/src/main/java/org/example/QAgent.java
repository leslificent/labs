package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class QAgent {
    private static final int SIZE = 5;
    private static final int TARGET_ROW = 0;
    private static final int TARGET_COL = 0;
    private static final int START_ROW = 4;
    private static final int START_COL = 4;

    private static final int[][] MOVES = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
    };

    private static final double LEARNING_RATE = 0.8;
    private static final double EXPLORATION_RATE = 0.2;

    private static final int[][] R = new int[SIZE * SIZE][SIZE * SIZE];
    private static final double[][] Q = new double[SIZE * SIZE][SIZE * SIZE];

    public static void main(String[] args) {
        initializeR();
        trainAgent();
        simulatePath();
    }

    private static void initializeR() {
        for (int i = 0; i < SIZE * SIZE; i++) {
            for (int j = 0; j < SIZE * SIZE; j++) {
                R[i][j] = -1; // За замовчуванням всі клітинки мають бути недійсними
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int state = i * SIZE + j;
                for (int[] move : MOVES) {
                    int newRow = i + move[0];
                    int newCol = j + move[1];
                    if (isValid(newRow, newCol)) {
                        int newState = newRow * SIZE + newCol;
                        if (newRow == TARGET_ROW && newCol == TARGET_COL) {
                            R[state][newState] = 100; // Нагорода при досягненні цілі
                        } else {
                            R[state][newState] = 0;
                        }
                    }
                }
            }
        }
    }


    private static boolean isValid(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private static void trainAgent() {
        int startState = START_ROW * SIZE + START_COL; // Початковий стан агента

        for (int i = 0; i < 10; i++) {
            int currentState = startState;
            int number = 0;
            while (currentState != TARGET_ROW * SIZE + TARGET_COL) {
                int nextState = chooseAction(currentState);

                double maxQ = getMaxQ(nextState);
                Q[currentState][nextState] = R[currentState][nextState] + LEARNING_RATE * maxQ;

                currentState = nextState;
                number+=1;
            }
            System.out.printf("Епоха навчання №"+ i + "\nЦіль досягнута за " + number + " кроків\n");

        }

    }

    private static void simulatePath() {
        int state = START_ROW * SIZE + START_COL;
        int steps = 0;
        System.out.printf("Стартова позиція: (%d, %d)\n", state / SIZE, state % SIZE);
        while (state != TARGET_ROW * SIZE + TARGET_COL) {
            System.out.printf("Кінь у точці (%d, %d)\n", state / SIZE, state % SIZE);
            state = getBestNextState(state);
            steps++;
        }
        System.out.printf("Кінь досягнув цілі у (%d, %d)\n", TARGET_ROW, TARGET_COL);
        System.out.printf("Загальна кількість кроків: %d\n", steps);
    }

    private static int getBestNextState(int state) {
        int bestState = -1;
        double maxQ = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < SIZE * SIZE; i++) {
            if (R[state][i] != -1 && Q[state][i] > maxQ) {
                maxQ = Q[state][i];
                bestState = i;
            }
        }
        return bestState != -1 ? bestState : state;
    }

    private static int chooseAction(int state) {
        Random random = new Random();
        if (random.nextDouble() < EXPLORATION_RATE) {
            return getRandomNextState(state);
        } else {
            return getBestNextState(state);
        }
    }

    private static int getRandomNextState(int state) {
        Random random = new Random();
        List<Integer> validStates = new ArrayList<>();
        for (int i = 0; i < SIZE * SIZE; i++) {
            if (R[state][i] != -1) {
                validStates.add(i);
            }
        }
        return validStates.get(random.nextInt(validStates.size()));
    }

    private static double getMaxQ(int state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < SIZE * SIZE; i++) {
            if (R[state][i] != -1 && Q[state][i] > maxQ) {
                maxQ = Q[state][i];
            }
        }
        return maxQ;
    }
}