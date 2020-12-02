package tul.kazmierski.solvers;

import tul.kazmierski.Move;
import tul.kazmierski.PuzzleSolverOrder;
import tul.kazmierski.State;

import java.util.ArrayList;

import static tul.kazmierski.Util.*;

public class iddfsSolverAlternative implements PuzzleSolverOrder {
    private long exploredStates;

    @Override
    public State solveWithOrder(ArrayList<Integer> initialBoard, Move[] movesOrder) {
        State initialState = new State(initialBoard, null, null);
        State solution = null;
        exploredStates = 0;
        int nextMaxDepth = 0;


        while (solution == null) {
            int currentMaxDepth = nextMaxDepth;
            solution = depthFirstSearch(initialState, currentMaxDepth);
            nextMaxDepth++;
        }

        return solution;
    }

    private State depthFirstSearch(State current, int maxDepth) {

        if (checkIfSolved(current.board)) {
            return current;
        }

        exploredStates++;

        Move[] validMoves = getValidMoves(current.board, current.moveToExecute);

        assert validMoves.length != 0;


        for (Move move : validMoves) {
            ArrayList<Integer> nextBoard = applyMove(current.board, move);
            State nextState = new State(nextBoard, current, move);
            if (nextState.depth <= maxDepth) {
                State result = depthFirstSearch(nextState, maxDepth);
                if (result != null)
                    return result;
            }
        }

        return null;
    }
}
