package tul.kazmierski.solvers;

import tul.kazmierski.Main;
import tul.kazmierski.Move;
import tul.kazmierski.PuzzleSolverOrder;
import tul.kazmierski.State;

import java.util.*;

import static tul.kazmierski.Util.*;

public class iddfsSolverAlternative2 implements PuzzleSolverOrder {
    private class SpecialState {
        State state;
        boolean remaining; //does this state have children?

        public SpecialState(State state, boolean remaining) {
            this.state = state;
            this.remaining = remaining;
        }
    }

    @Override
    public State solveWithOrder(ArrayList<Integer> initialBoard, Move[] movesOrder) {
        State initialState = new State(initialBoard, null, null);
        for (int maxDepth = 0; ; maxDepth++) {
            State result = DLS(initialState, maxDepth);
            if (result != null)
                return result;
        }
    }

    //Depth Limited Search (DFS)
    private State DLS(State start, int maxDepth) {
        Deque<State> candidates = new LinkedList<>();
        Set<ArrayList<Integer>> discoveredBoards = new HashSet<>(181440);
        candidates.add(start);

        while (candidates.size() != 0) {
            State current = candidates.pollLast();
            Main.visitedCounter++;

            assert current != null;

            if (current.depth == maxDepth) {
                if (checkIfSolved(current.board))
                    return current;
                continue;
            }

            Move[] validMoves = getValidMoves(current.board, current.moveToExecute);

            ArrayList<Integer> nextBoard;
            for (Move move : validMoves) {
                nextBoard = applyMove(current.board, move);
                if (!discoveredBoards.contains(nextBoard)) {
                    State nextState = new State(nextBoard, current, move);
                    candidates.add(nextState);
                    discoveredBoards.add(nextBoard);
                }
            }
        }
        return null;
    }
}
