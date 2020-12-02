package tul.kazmierski.solvers;

import tul.kazmierski.Main;
import tul.kazmierski.Move;
import tul.kazmierski.PuzzleSolverOrder;
import tul.kazmierski.State;

import java.util.*;

import static java.lang.System.exit;
import static tul.kazmierski.Util.*;

public class iddfsSolverAlternative3 implements PuzzleSolverOrder {
    private class SpecialState {
        State state;
        boolean remaining; //does this state have children?

        public SpecialState(State state, boolean remaining) {
            this.state = state;
            this.remaining = remaining;
        }
    }

    private final Set<State> discoveredStates = new HashSet<>();

    @Override
    public State solveWithOrder(ArrayList<Integer> initialBoard, Move[] movesOrder) {
        State initialState = new State(initialBoard, null, null);
        for (int maxDepth = 0; ; maxDepth++) {
            SpecialState result = DLS(initialState, maxDepth);

            assert result != null;

            if (result.state != null)
                return result.state;
            else if(!result.remaining)
                return null;

            discoveredStates.clear();
        }
    }

    //Depth Limited Search (DFS)
    private SpecialState DLS(State start, int maxDepth) {
        Main.visitedCounter++;

        if(maxDepth == 0) {
            if(checkIfSolved(start.board))
                return new SpecialState(start, true);
            else
                return new SpecialState(null, true);
        } else if(maxDepth > 0) {
                boolean anyRemaining = false;

                Move[] validMoves = getValidMoves(start.board, start.moveToExecute);

                ArrayList<Integer> nextBoard;
                for (Move move : validMoves) {
                    nextBoard = applyMove(start.board, move);
                    State nextState = new State(nextBoard, start, move);
                    if (colContains(nextState))
                        continue;
                    discoveredStates.add(nextState);
                    SpecialState nextSpecialState = DLS(nextState, maxDepth-1);

                    assert nextSpecialState != null;

                    if (nextSpecialState.state != null)
                        return new SpecialState(nextSpecialState.state, true);
                    if (nextSpecialState.remaining)
                        anyRemaining = true;
                }
                return new SpecialState(null, anyRemaining);
        } else {
            System.err.println("Depth cannot be lower than 0");
            exit(-2);
            return null;
        }
    }

    private boolean colContains(State toCompare) {
        for(State state : discoveredStates) {
            if (state.depth == toCompare.depth && state.board.equals(toCompare.board))
                return true;
        }
        return false;
    }
}
