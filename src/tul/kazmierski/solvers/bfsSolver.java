package tul.kazmierski.solvers;

import tul.kazmierski.Move;
import tul.kazmierski.PuzzleSolverOrder;
import tul.kazmierski.State;

import java.util.ArrayList;
import java.util.LinkedList;

import static tul.kazmierski.Util.*;

public class bfsSolver implements PuzzleSolverOrder {
    @Override

    public Move[] solveWithOrder(int[] initialBoard, Move[] movesOrder) {
        LinkedList<State> candidates = new LinkedList<>();
        ArrayList<int[]> discoveredBoards = new ArrayList<>();

        candidates.add(new State(initialBoard, null, null));

        while(candidates.size() > 0) {
            State current = candidates.remove();

            if(checkIfSolved(current.board))
                return reconstructSolution(current);

            Move[] validMoves = getValidMoves(current.board, movesOrder);

            assert validMoves != null;
            
            for(Move move : validMoves) {
                int[] nextBoard = applyMove(current.board, move);
                if(discoveredBoards.contains(nextBoard))
                    continue;
                else
                    discoveredBoards.add(nextBoard);

                candidates.add(new State(nextBoard, current, move));
            }
        }

        return null;
    }
}
