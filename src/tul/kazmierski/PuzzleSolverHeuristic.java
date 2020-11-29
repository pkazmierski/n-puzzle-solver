package tul.kazmierski;

import java.util.ArrayList;

public interface PuzzleSolverHeuristic {
    /**
     * Solves the n-puzzle using a search strategy which requires a heuristic
     * @param initialBoard Initial board state.
     * @param heuristic ID of the heuristic to be used.
     * @return Order of moves which has to be executed on the initial board in order to solve the puzzle or null if not solvable.
     */
    public State solveWithHeuristic(ArrayList<Integer> initialBoard, Heuristic heuristic);
}
