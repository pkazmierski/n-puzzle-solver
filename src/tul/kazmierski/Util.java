package tul.kazmierski;

import java.util.Arrays;

public class Util {
    public static int[] solvedBoard = null;

    public static void generateSolvedBoard(int[] initialBoard) {
        //TODO implement
    }

    public static boolean checkIfSolvable(int[] board) {
        //TODO implement
        return false;
    }

    public static Move[] parseMovesOrder(String movesOrder) {
        //TODO implement
        return null;
    }

    public static boolean checkIfSolved(int[] board) {
        return Arrays.equals(board, solvedBoard);
    }

    public static Move[] reconstructSolution(State solvedState) {
        //TODO implement
        return null;
    }

    public static Move[] getValidMoves(int[] board, Move[] movesOrder) {
        //TODO implement
        return null;
    }

    public static int[] applyMove(int[] board, Move move) {
        //TODO implement
        return null;
    }
}
