package tul.kazmierski.solvers;

import tul.kazmierski.Move;
import tul.kazmierski.PuzzleSolverOrder;
import tul.kazmierski.State;

import java.util.*;

import static tul.kazmierski.Util.*;

public class bfsSolver implements PuzzleSolverOrder {

    public State solveWithOrder2(int[] initialBoard, Move[] movesOrder) {
        Queue<State> candidates = new LinkedList<>();
        Set<int[]> discoveredBoards = new HashSet<>(181440);

        candidates.add(new State(initialBoard, null, null));

        while (candidates.size() > 0) {
            State current = candidates.poll();

            if (checkIfSolved(current.board))
                return current;

            Move[] validMoves = getValidMoves(current.board);

            assert validMoves.length != 0;

            int[] nextBoard;
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

    @Override

    public State solveWithOrder(int[] initialBoard, Move[] movesOrder) {
        Set<int[]> stateSets = new HashSet<>();
        Queue<State> queue = new LinkedList<State>();
        State currentState = new State(initialBoard, null, null);

        while (!checkIfSolved(currentState.board)) {
            stateSets.add(currentState.board);
            Move[] nodeSuccessors = getValidMoves(currentState.board);
            for (Move n : nodeSuccessors) {
                int[] newBoard = applyMove(currentState.board, n);
                if (stateSets.contains(newBoard))
                    continue;
                stateSets.add(currentState.board);

                queue.add(new State(newBoard, currentState, n));
            }
            currentState = queue.poll();
        }

        return currentState;
    }
}