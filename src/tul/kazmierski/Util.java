package tul.kazmierski;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static tul.kazmierski.Main.oppositeMoves;
import static tul.kazmierski.Main.usedMemoryKb;

public abstract class Util {
    public static Comparator<RankedState> rankedStateComparator = (o1, o2) -> {
        if(o1.rank == o2.rank) return 0;
        else return o1.rank > o2.rank ? 1 : -1;
    };

    public static Dimensions parseDimensions(String dimensionsArg) {
        String[] dimensions = dimensionsArg.split(" ");

        if (dimensions.length != 2)
            throw new IllegalArgumentException("Dimensions must be in the format 'width height'");
        // optionally enable MxN boards, instead of just NxN
        // NOTE: this would also require changes in the checkIfSolvable function
        if (!dimensions[0].equals(dimensions[1]))
            throw new IllegalArgumentException("Width must be equal to height");

        return new Dimensions(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[0]));
    }

    public static ArrayList<Integer> parseInitialBoard(ArrayList<String> rowsArg, Dimensions dimensions) {
        if (rowsArg.size() != dimensions.height)
            throw new IllegalArgumentException("The number of lines does not match the given height");

        ArrayList<Integer> initialBoard = new ArrayList<>();
        for (String row : rowsArg) {
            String[] splitRow = row.split(" ");
            for (String numAsChar : splitRow)
                initialBoard.add(Integer.parseInt(numAsChar));
        }
        return initialBoard;
    }

    public static ArrayList<Integer> generateSolvedBoard(ArrayList<Integer> initialBoard) {
        int max = Collections.max(initialBoard);
        ArrayList<Integer> tmpSolvedBoard = new ArrayList<>();
        for (int i = 1; i <= max; i++) tmpSolvedBoard.add(i);
        tmpSolvedBoard.add(0);
        return tmpSolvedBoard;
    }

    public static int countInversions(ArrayList<Integer> board) {
        int inversions = 0;
        for (int i = 0; i < board.size(); i++) {
            int val = board.get(i);
            if(val == 0)
                continue;

            for (var j = (i + 1); j < board.size(); j++) {
                if (board.get(j) < val && board.get(j) != 0) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    /*
      If N is odd, then puzzle instance is solvable if number of inversions is even in the input state.
      If N is even, puzzle instance is solvable if
        1) the blank is on an even row counting from the bottom (second-last, fourth-last, etc.) and number of inversions is odd.
        2) the blank is on an odd row counting from the bottom (last, third-last, fifth-last, etc.) and number of inversions is even.
      For all other cases, the puzzle instance is not solvable.
      Note: subconditions 1) and 2) can be flipped, i.e. blank on ODD row counting from the TOP and number of inversions is odd (this doesn't change)
    */
    public static boolean checkIfSolvable(ArrayList<Integer> board) {
        int inversions = countInversions(board);

        if (Main.dimensions.width % 2 == 0) { // N is even
            int zeroIndex = board.indexOf(0);
            int zeroRow = (zeroIndex / Main.dimensions.height) + 1;
            boolean zeroEvenRow = zeroRow % 2 == 0;

            if(zeroEvenRow)
                return inversions % 2 == 0; // num of inversions is even
            else
                return inversions % 2 == 1; // num of inversions is odd
        } else { // N is odd
            return inversions % 2 == 0;
        }
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

    public static void setUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); //garbage collector frees up space
        usedMemoryKb = (runtime.totalMemory() - runtime.freeMemory()) / 1024L; //calculate memory
    }

    private static Move[] generateRandomMovesOrder() {
        ArrayList<Move> randomMoves = new ArrayList<>(4);

        randomMoves.add(Move.UP);
        randomMoves.add(Move.DOWN);
        randomMoves.add(Move.LEFT);
        randomMoves.add(Move.RIGHT);

        Collections.shuffle(randomMoves);
        return randomMoves.toArray(new Move[0]);
    }

    public static boolean checkIfSolved(ArrayList<Integer> board) {
        return board.equals(Main.solvedBoard);
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

    public static int getIndex(ArrayList<Integer> arr) {
        return arr.indexOf(0);
//        int index = -1;
//        for (int i = 0; i < arr.size(); ++i) {
//            if (arr.get(i) == 0) {
//                index = i;
//                break;
//            }
//        }
//        return index;
    }

    //TODO make move a "swap map", i.e. int pair and adjust the function below
    public static Move[] getValidMoves(ArrayList<Integer> board, Move parentMove) {
        int zeroIndex = getIndex(board);
        int zeroRow = (zeroIndex / Main.dimensions.height) + 1;
        int zeroColumn = (zeroIndex % Main.dimensions.width) + 1;

        ArrayList<Move> validMoves = new ArrayList<>();
        for (Move move : Main.movesOrder) {
            if(move == oppositeMoves.get(parentMove))
                continue;
            switch (move) {
                case LEFT:
                    if (zeroColumn != 1)
                        validMoves.add(Move.LEFT);
                    break;
                case RIGHT:
                    if (zeroColumn != Main.dimensions.width)
                        validMoves.add(Move.RIGHT);
                    break;
                case UP:
                    if (zeroRow != 1)
                        validMoves.add(Move.UP);
                    break;
                case DOWN:
                    if (zeroRow != Main.dimensions.height)
                        validMoves.add(Move.DOWN);
                    break;
            }
        }

        return validMoves.toArray(new Move[0]);
    }

    //TODO make move a "swap map", i.e. int pair and adjust the function below
    //ordering starts from 1, not 0
    public static ArrayList<Integer> applyMove(ArrayList<Integer> board, Move move) {
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

        ArrayList<Integer> newBoard = new ArrayList<>(board);

        newBoard.set(zeroIndex, newBoard.get(newZeroIndex));
        newBoard.set(newZeroIndex, 0);

        return newBoard;
    }

    private static void printRed(String str) {
        System.out.print("\033[0;31m" + str + "\033[0m");
    }

    public static void printBoard(ArrayList<Integer> board) {
        int max = Collections.max(board);
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
                int digits = board.get(index) == 0 ? 1 : (int) (Math.log10(board.get(index)) + 1);
                for (int l = maxDigits - digits; l > 0; l--)
                    System.out.print(" ");
                if (board.get(index) == 0)
                    printRed("0");
                else
                    System.out.print(board.get(index));
                System.out.print("║");
            }
            int digits = board.get(index) == 0 ? 1 : (int) (Math.log10(board.get(index)) + 1);
            for (int l = maxDigits - digits; l > 0; l--)
                System.out.print(" ");
            if (board.get(index) == 0)
                printRed("0");
            else
                System.out.print(board.get(index));
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
        ArrayList<ArrayList<Integer>> boards = new ArrayList<>();

        while (currentState != null) {
            boards.add(currentState.board);
            currentState = currentState.parent;
        }

        System.out.println("Board states:");
        for (int i = boards.size() - 1; i >= 0; i--) {
            printBoard(boards.get(i));
        }
    }
}