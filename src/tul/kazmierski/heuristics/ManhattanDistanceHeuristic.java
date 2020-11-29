package tul.kazmierski.heuristics;

import tul.kazmierski.Heuristic;
import tul.kazmierski.Main;

import java.util.ArrayList;

public class ManhattanDistanceHeuristic implements Heuristic {
    @Override
    public int getRank(ArrayList<Integer> board) {
        int manhattanSum = 0;
        for(int i = 0; i < board.size(); i++) {
            int currentBoardValue = board.get(i);
            int solvedBoardValue = Main.solvedBoard.get(i);
            if(currentBoardValue != solvedBoardValue && currentBoardValue != 0)
                manhattanSum += calculateManhattanDistance(board, i, currentBoardValue);
        }
        return manhattanSum;
    }

    private static int calculateManhattanDistance(ArrayList<Integer> board, int index, int value) {
        int currentRow = (index / Main.dimensions.height) + 1;
        int currentColumn = (index % Main.dimensions.width) + 1;

        int targetIndex = Main.solvedBoard.indexOf(value);
        int targetRow = (targetIndex / Main.dimensions.height) + 1;
        int targetColumn = (targetIndex % Main.dimensions.width) + 1;

        return Math.abs(targetColumn - currentColumn) + Math.abs(targetRow - currentRow);
    }
}
