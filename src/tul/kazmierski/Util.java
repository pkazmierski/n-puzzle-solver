package tul.kazmierski;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class Util {

    public static Dimensions parseDimensions(String dimensionsArg) {
        String[] dimensions = dimensionsArg.split(" ");

        if (dimensions.length != 2)
            throw new IllegalArgumentException("Dimensions must be in the format 'width height'");
        // optionally enable MxN boards, instead of just NxN
//        if (!dimensions[0].equals(dimensions[1]))
//            throw new IllegalArgumentException("Width must be equal to height");

        return new Dimensions(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[0]));
    }

    public static int[] parseInitialBoard(ArrayList<String> rowsArg, Dimensions dimensions) {
        if (rowsArg.size() != dimensions.height)
            throw new IllegalArgumentException("The number of lines does not match the given height");

        ArrayList<Integer> initialBoard = new ArrayList<>();
        for (String row : rowsArg) {
            String[] splitRow = row.split(" ");
            for (String numAsChar : splitRow)
                initialBoard.add(Integer.parseInt(numAsChar));
        }
        return initialBoard.stream().mapToInt(i -> i).toArray();
    }

    public static int[] generateSolvedBoard(int[] initialBoard) {
        int max = Collections.max(Arrays.stream(initialBoard).boxed().collect(Collectors.toList()));
        ArrayList<Integer> tmpSolvedBoard = new ArrayList<>();
        for (int i = 1; i <= max; i++) tmpSolvedBoard.add(i);
        tmpSolvedBoard.add(0);
        return tmpSolvedBoard.stream().mapToInt(i -> i).toArray();
    }

    public static boolean checkIfSolvable(int[] board) {
        //TODO implement
        return true;
    }

    public static Move[] parseMovesOrder(String movesOrder) {
        if (!movesOrder.equals("R") && movesOrder.length() != 4)
            throw new IllegalArgumentException("The moves order must be \"R\" or 4 characters long");

        if (movesOrder.equals("R"))
            return generateRandomMovesOrder();
        else {
            char[] split = movesOrder.toCharArray();
            Move[] moves = new Move[4];
            for (int i = 0; i < 4; i++) {
                char c = split[i];
                switch (c) {
                    case 'U':
                        moves[i] = Move.UP;
                        break;
                    case 'D':
                        moves[i] = Move.DOWN;
                        break;
                    case 'L':
                        moves[i] = Move.LEFT;
                        break;
                    case 'R':
                        moves[i] = Move.RIGHT;
                        break;
                    default:
                        throw new IllegalArgumentException("The move must be either 'U', 'D', 'R' or 'L'. Got '" + c + "'");
                }
            }
            return moves;
        }
    }

    private static Move[] generateRandomMovesOrder() {
        Move[] randomMoves = new Move[4];
        for (int i = 0; i < 4; i++)
            randomMoves[i] = randomEnum(Move.class);
        return randomMoves;
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        Random random = new Random();
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static boolean checkIfSolved(int[] board) {
        return Arrays.equals(board, Main.solvedBoard);
    }

    public static Move[] reconstructSolution(State solvedState) {
        assert solvedState != null;
        assert solvedState.moveToExecute == null;

        State currentState = solvedState;
        ArrayList<Move> solutionMoves = new ArrayList<>();

        while (currentState.parent != null) {
            solutionMoves.add(currentState.moveToExecute);
            currentState = currentState.parent;
        }

        Collections.reverse(solutionMoves);

        return solutionMoves.toArray(new Move[0]);
    }

    public static int getIndex(int[] arr) {
        int index = -1;
        for (int i = 0; i < arr.length; ++i) {
            if (0 == arr[i]) {
                index = i;
                break;
            }
        }
        return index;
    }

    //TODO make move a "swap map", i.e. int pair and adjust the function below
    public static Move[] getValidMoves(int[] board) {
        int zeroIndex = getIndex(board);
        int zeroRow = (zeroIndex / Main.dimensions.height) + 1;
        int zeroColumn = (zeroIndex % Main.dimensions.width) + 1;

        ArrayList<Move> validMoves = new ArrayList<>();
        if (zeroRow != 1)
            validMoves.add(Move.UP);
        if (zeroRow != Main.dimensions.height)
            validMoves.add(Move.DOWN);
        if (zeroColumn != 1)
            validMoves.add(Move.LEFT);
        if (zeroColumn != Main.dimensions.width)
            validMoves.add(Move.RIGHT);

        return validMoves.toArray(new Move[0]);
    }

    //TODO make move a "swap map", i.e. int pair and adjust the function below
    //ordering starts from 1, not 0
    public static int[] applyMove(int[] board, Move move) {
        int zeroIndex = getIndex(board);
        int zeroRow = (zeroIndex / Main.dimensions.height) + 1;
        int zeroColumn = (zeroIndex % Main.dimensions.width) + 1;

        switch (move) {
            case UP:
                zeroRow -= 1;
                break;
            case DOWN:
                zeroRow += 1;
                break;
            case RIGHT:
                zeroColumn += 1;
                break;
            case LEFT:
                zeroColumn -= 1;
                break;
        }

        int newZeroIndex = (zeroRow - 1) * Main.dimensions.height + zeroColumn - 1;

        int[] newBoard = board.clone();
        newBoard[zeroIndex] = newBoard[newZeroIndex];
        newBoard[newZeroIndex] = 0;

        return newBoard;
    }

    private static void printRed(String str) {
        System.out.print("\033[0;31m" + str + "\033[0m");
    }

    public static void printBoard(int[] board) {
        int max = Collections.max(Arrays.stream(board).boxed().collect(Collectors.toList()));
        int maxDigits = (int) (Math.log10(max) + 1);
        int index = 0;

        System.out.print("╔");
        for (int k = 1; k < Main.dimensions.width; k++) {
            for (int l = maxDigits; l > 0; l--)
                System.out.print("═");
            System.out.print("╦");
        }
        for (int l = maxDigits; l > 0; l--)
            System.out.print("═");
        System.out.print("╗\n");

        for (int j = 1; j <= Main.dimensions.height; j++) {
            System.out.print("╠");
            for (int k = 1; k < Main.dimensions.width; k++, index++) {
                int digits = board[index] == 0 ? 1 : (int) (Math.log10(board[index]) + 1);
                for (int l = maxDigits - digits; l > 0; l--)
                    System.out.print(" ");
                if (board[index] == 0)
                    printRed("0");
                else
                    System.out.print(board[index]);
                System.out.print("║");
            }
            int digits = board[index] == 0 ? 1 : (int) (Math.log10(board[index]) + 1);
            for (int l = maxDigits - digits; l > 0; l--)
                System.out.print(" ");
            if (board[index] == 0)
                printRed("0");
            else
                System.out.print(board[index]);
            System.out.print("╣\n");
            index++;
        }

        System.out.print("╚");
        for (int k = 1; k < Main.dimensions.width; k++) {
            for (int l = maxDigits; l > 0; l--)
                System.out.print("═");
            System.out.print("╩");
        }
        for (int l = maxDigits; l > 0; l--)
            System.out.print("═");
        System.out.print("╝\n");
    }

    public static void visualizeSolution(State solvedState) {
        assert solvedState.moveToExecute == null;

        State currentState = solvedState;
        ArrayList<int[]> boards = new ArrayList<>();

        while (currentState != null) {
            boards.add(currentState.board);
            currentState = currentState.parent;
        }

        System.out.println("Board states:\n");
        for (int i = boards.size() - 1; i >= 0; i--) {
            printBoard(boards.get(i));
        }
    }
}