package tul.kazmierski.solvers;

import tul.kazmierski.Main;
import tul.kazmierski.Move;
import tul.kazmierski.PuzzleSolverOrder;
import tul.kazmierski.State;

import java.util.*;

import static tul.kazmierski.Util.*;

public class bfsSolverAlternative implements PuzzleSolverOrder {

    @Override
    public State solveWithOrder(ArrayList<Integer> initialBoard, Move[] movesOrder) {
        Set<ArrayList<Integer>> stateSets = new HashSet<>();
        Queue<State> queue = new LinkedList<State>();
        State currentState = new State(initialBoard, null, null);

        while (currentState != null && !checkIfSolved(currentState.board)) {
            stateSets.add(currentState.board);
            Main.visitedCounter++;
            Move[] nodeSuccessors = getValidMoves(currentState.board);
            for (Move n : nodeSuccessors) {
                ArrayList<Integer> newBoard = applyMove(currentState.board, n);
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