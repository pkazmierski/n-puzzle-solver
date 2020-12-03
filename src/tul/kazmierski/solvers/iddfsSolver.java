package tul.kazmierski.solvers;

import tul.kazmierski.Main;
import tul.kazmierski.Move;
import tul.kazmierski.PuzzleSolverOrder;
import tul.kazmierski.State;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import static tul.kazmierski.Util.*;

public class iddfsSolver implements PuzzleSolverOrder {
    private boolean fail = false;

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
            if(fail)
                return null;
            if (result != null) {
                setUsedMemory();
                return result;
            }
        }
    }

    //Depth Limited Search (DFS)
    private State DLS(State start, int maxDepth) {
        Deque<State> candidates = new LinkedList<>();
        candidates.add(start);

        while (true) {
            Main.endTime = System.currentTimeMillis();
            if(Main.endTime - Main.startTime > 60000) {
                Main.failReason = "OUT_OF_TIME";
                fail = true;
                setUsedMemory();
                return null;
            }

            if(Runtime.getRuntime().freeMemory() < 10L * 1048576L) { //lest than 20 MB
                Main.failReason = "OUT_OF_MEMORY";
                fail = true;
                setUsedMemory();
                return null;
            }

            State current = candidates.pollLast();
            Main.visitedCounter++;

            assert current != null;

            if (current.depth == maxDepth) {
                if (checkIfSolved(current.board))
                    return current;
                if (candidates.size() == 0) {
                    return null;
                }
                continue;
            }

            Move[] validMoves = getValidMoves(current.board, current.moveToExecute);

            ArrayList<Integer> nextBoard;
            for (Move move : validMoves) {
                nextBoard = applyMove(current.board, move);
                State nextState = new State(nextBoard, current, move);
                candidates.add(nextState);
            }
        }
    }
}
