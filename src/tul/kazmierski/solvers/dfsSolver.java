package tul.kazmierski.solvers;

import tul.kazmierski.Main;
import tul.kazmierski.Move;
import tul.kazmierski.PuzzleSolverOrder;
import tul.kazmierski.State;

import java.util.*;

import static tul.kazmierski.Util.*;

public class dfsSolver implements PuzzleSolverOrder {

    @Override
    public State solveWithOrder(ArrayList<Integer> initialBoard, Move[] movesOrder) {
        Deque<State> candidates = new LinkedList<>();
        Set<ArrayList<Integer>> discoveredBoards = new HashSet<>(181440);

        candidates.add(new State(initialBoard, null, null));

        while (candidates.size() > 0) {
            State current = candidates.pollLast();
            Main.visitedCounter++;

            if (checkIfSolved(current.board))
                return current;

            Move[] validMoves = getValidMoves(current.board);

            assert validMoves.length != 0;

            ArrayList<Integer> nextBoard;
            for (Move move : validMoves) {
                nextBoard = applyMove(current.board, move);
                if (!discoveredBoards.contains(nextBoard)) {
                    candidates.add(new State(nextBoard, current, move));
                    discoveredBoards.add(nextBoard);
                }
            }
        }

        return null;
    }
}