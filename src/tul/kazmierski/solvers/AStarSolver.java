package tul.kazmierski.solvers;

import tul.kazmierski.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import static tul.kazmierski.Util.*;

public class AStarSolver implements PuzzleSolverHeuristic {
    @Override
    public State solveWithHeuristic(ArrayList<Integer> initialBoard, Heuristic heuristic) {
        PriorityQueue<RankedState> candidates = new PriorityQueue<>(rankedStateComparator);

        Set<ArrayList<Integer>> discoveredBoards = new HashSet<>(181440);

        candidates.add(new RankedState(new State(initialBoard, null, null, 0),
                heuristic.getRank(initialBoard)));

        while (candidates.size() > 0) {
            Main.endTime = System.currentTimeMillis();
            if(Main.endTime - Main.startTime > 60000) {
                Main.failReason = "OUT_OF_TIME";
                setUsedMemory();
                return null;
            }
            if(Runtime.getRuntime().freeMemory() < 1048576L) { //lest than 1 MB
                Main.failReason = "OUT_OF_MEMORY";
                setUsedMemory();
                return null;
            }

            RankedState currentRankedState = candidates.poll();
            State current = currentRankedState.state;
            Main.visitedCounter++;

            if (checkIfSolved(current.board)) {
                setUsedMemory();
                return current;
            }

            Move[] validMoves = getValidMoves(current.board, current.moveToExecute);

            assert validMoves.length != 0;

            ArrayList<Integer> nextBoard;
            for (Move move : validMoves) {
                nextBoard = applyMove(current.board, move);
                if (!discoveredBoards.contains(nextBoard)) {
                    State nextState = new State(nextBoard, current, move);
                    candidates.add(new RankedState(nextState,heuristic.getRank(nextBoard) + nextState.depth));
                    discoveredBoards.add(nextBoard);
                }
            }
        }

        return null;
    }
}
