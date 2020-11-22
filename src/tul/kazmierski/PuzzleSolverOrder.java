package tul.kazmierski;


import java.util.ArrayList;

public interface PuzzleSolverOrder {
    /**
     * Solves the n-puzzle using a search strategy which requires a moves order.
     * @param initialBoard Initial board state.
     * @param movesOrder Order in which the moves will be checked and subsequent nodes visited.
     * @return Order of moves which has to be executed on the initial board in order to solve the puzzle or null if not solvable.
     */
    public State solveWithOrder(ArrayList<Integer> initialBoard, Move[] movesOrder);
}
