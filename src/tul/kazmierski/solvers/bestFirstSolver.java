package tul.kazmierski.solvers;

import tul.kazmierski.*;

import java.util.*;

import static tul.kazmierski.Util.*;

public class bestFirstSolver implements PuzzleSolverHeuristic {
    private long freeMem = 0;
    @Override
    public State solveWithHeuristic(ArrayList<Integer> initialBoard, Heuristic heuristic) {
        PriorityQueue<RankedState> candidates = new PriorityQueue<>(rankedStateComparator);

        Set<ArrayList<Integer>> discoveredBoards = new HashSet<>(181440);

        candidates.add(new RankedState(new State(initialBoard, null, null),
                                       heuristic.getRank(initialBoard)));

        while (candidates.size() > 0) {
            Main.endTime = System.currentTimeMillis();
            if(Main.endTime - Main.startTime > 60000) {
                Main.failReason = "OUT_OF_TIME";
                setUsedMemory();
                return null;
            }
            freeMem = Runtime.getRuntime().freeMemory();
            if(freeMem < 1048576L) { //lest than 1 MB
                Main.failReason = "OUT_OF_MEMORY";
                setUsedMemory();
                return null;
            }

            State current = candidates.poll().state;
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
                    candidates.add(new RankedState(new State(nextBoard, current, move),
                                                   heuristic.getRank(nextBoard)));
                    discoveredBoards.add(nextBoard);
                }
            }
        }

        return null;
    }
}
