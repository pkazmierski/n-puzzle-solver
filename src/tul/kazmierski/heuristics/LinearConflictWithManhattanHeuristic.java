package tul.kazmierski.heuristics;

import tul.kazmierski.Heuristic;
import tul.kazmierski.Main;

import java.util.*;

import static tul.kazmierski.heuristics.ManhattanDistanceHeuristic.calculateManhattanDistance;

public class LinearConflictWithManhattanHeuristic implements Heuristic {
    @Override
    public int getRank(ArrayList<Integer> board) {
        int manhattanSum = 0;
        for (int i = 0; i < board.size(); i++) {
            int currentBoardValue = board.get(i);
            int solvedBoardValue = Main.solvedBoard.get(i);
            if (currentBoardValue != solvedBoardValue && currentBoardValue != 0)
                manhattanSum += calculateManhattanDistance(board, i, currentBoardValue);
        }

        int linearConflicts = countAllLinearConflicts(board);
        return manhattanSum + 2 * linearConflicts;
    }

    //Two tiles tj and tk are in a linear conflict if:
    // tj and tk are in the same line,
    // the goal positions of tj and tk are both in that line,
    // tj is to the right of tk
    // and goal position of tj is to the left of the goal position of tk.
    // tj = target, tk = base
    public static int countAllLinearConflicts(ArrayList<Integer> board) {
//        Set<Map.Entry<Integer, Integer>> linearConflicts = new HashSet<>();
        int conflictCounter = 0;

        //rows
        for (int i = 1; i <= Main.dimensions.height; i++) {
            //column of the base tile (left-hand operand)
            for (int j = 1; j < Main.dimensions.width; j++) {
                int baseIndex = (i - 1) * Main.dimensions.height + j - 1;
                int baseValue = board.get(baseIndex);
                int baseGoalIndex = Main.solvedBoard.indexOf(baseValue);
                int baseGoalRow = (baseGoalIndex / Main.dimensions.height) + 1;

                //column of the target tile (right-hand operand)
                for (int k = j + 1; k <= Main.dimensions.width; k++) {
                    int targetIndex = (i - 1) * Main.dimensions.height + k - 1;
                    int targetValue = board.get(targetIndex);
                    int targetGoalIndex = Main.solvedBoard.indexOf(targetValue);
                    int targetGoalRow = (targetGoalIndex / Main.dimensions.height) + 1;

                    if (baseGoalRow == targetGoalRow && targetGoalIndex < baseGoalIndex)
                        conflictCounter++;
//                        linearConflicts.add(new AbstractMap.SimpleEntry<Integer, Integer>(baseIndex, targetIndex));
                }
            }
        }
        
        //columns
        for (int i = 1; i <= Main.dimensions.width; i++) {
            //row of the base tile (left-hand operand)
            for (int j = 1; j < Main.dimensions.height; j++) {
                int baseIndex = (j - 1) * Main.dimensions.width + i - 1;
                int baseValue = board.get(baseIndex);
                int baseGoalIndex = Main.solvedBoard.indexOf(baseValue);
                int baseGoalColumn = (baseGoalIndex % Main.dimensions.width) + 1;

                //row of the target tile (right-hand operand)
                for (int k = j + 1; k <= Main.dimensions.height; k++) {
                    int targetIndex = (k - 1) * Main.dimensions.width + i - 1;
                    int targetValue = board.get(targetIndex);
                    int targetGoalIndex = Main.solvedBoard.indexOf(targetValue);
                    int targetGoalColumn = (targetGoalIndex % Main.dimensions.width) + 1;

                    if (baseGoalColumn == targetGoalColumn && targetGoalIndex < baseGoalIndex)
                        conflictCounter++;
//                        linearConflicts.add(new AbstractMap.SimpleEntry<Integer, Integer>(baseIndex, targetIndex));
                }
            }
        }
        
        
//        return linearConflicts.size();
        return conflictCounter;
    }

//    public static Set<Map.Entry<Integer, Integer>> findLinearConflicts(ArrayList<Integer> board, int index, int baseVal) {
//        int baseRow = (index / Main.dimensions.height) + 1;
//        int baseColumn = (index % Main.dimensions.width) + 1;
//
//        //row
//        for (int i = 1; i <= Main.dimensions.width; i++) {
//            int curVal = board.get(i);
//            if (curVal == 0 || curVal == baseVal)
//                continue;
//
//            //row
//        }
//    }
}
