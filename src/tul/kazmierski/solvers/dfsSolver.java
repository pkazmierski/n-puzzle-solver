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
            Main.endTime = System.currentTimeMillis();
            if(Main.endTime - Main.startTime > 60000) {
                Main.failReason = "OUT_OF_TIME";
                setUsedMemory();
                return null;
            }
            if(Runtime.getRuntime().freeMemory() < 10L * 1048576L) { //lest than 20 MB
                Main.failReason = "OUT_OF_MEMORY";
                setUsedMemory();
                return null;
            }

            State current = candidates.pollLast();
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
                    candidates.add(new State(nextBoard, current, move));
                    discoveredBoards.add(nextBoard);
                }
            }
        }

        return null;
    }
}