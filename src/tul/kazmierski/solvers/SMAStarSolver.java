package tul.kazmierski.solvers;

import tul.kazmierski.*;

import java.util.*;

import static tul.kazmierski.Util.*;

public class SMAStarSolver implements PuzzleSolverHeuristic {
    @Override
    public State solveWithHeuristic(ArrayList<Integer> initialBoard, Heuristic heuristic) {
        int maxCapacity = 30;

        PriorityQueue<RankedState> candidates = new PriorityQueue<>(rankedStateComparator);

        Set<ArrayList<Integer>> discoveredBoards = new HashSet<>(maxCapacity);

        candidates.add(new RankedState(new State(initialBoard, null, null, 0),
                heuristic.getRank(initialBoard)));

        int minDepth = 0;

        while (candidates.size() > 0) {
            RankedState currentRankedState = candidates.poll();
            State current = currentRankedState.state;
            Main.visitedCounter++;

            Move[] validMoves = getValidMoves(current.board);
            int lowestForgottenRank = Integer.MAX_VALUE;

            ArrayList<Integer> nextBoard;
            ArrayList<RankedState> successors = new ArrayList<>();
            for (Move move : validMoves) {
                nextBoard = applyMove(current.board, move);
                State nextState = new State(nextBoard, current, move);
                RankedState nextRankedState;
                if (checkIfSolved(nextBoard))
                    return current;
                else {
                    nextRankedState = new RankedState(nextState,
                            Math.max(currentRankedState.rank, heuristic.getRank(nextState.board) + nextState.depth));
                }
                successors.add(nextRankedState);

                if (!discoveredBoards.contains(nextBoard)) {
                    candidates.add(nextRankedState);
                    discoveredBoards.add(nextBoard);
                }
            }

            //last successor added,  update the ancestors' f-cost to be
            //the minimum of their successors's f-cost
            RankedState minRankState = Collections.min(successors, Comparator.comparingInt(o -> o.rank));
            currentRankedState.rank = minRankState.rank;

            if(candidates.size() >= maxCapacity) {
                minDepth = Integer.MAX_VALUE;
                //set minimum depth of the current candidates queue
                for (RankedState cur : candidates) {
                    if (cur.state.depth < minDepth)
                        minDepth = cur.state.depth;
                }

                Iterator<RankedState> iter = candidates.iterator();
                ArrayList<RankedState> rankedStates = new ArrayList<>();
                //find the candidate with the depth equal to minDepth
                while (iter.hasNext()) {
                    RankedState cur = iter.next();
                    if(cur.state.depth == minDepth)
                        rankedStates.add(cur);
                }

                RankedState minDepthMaxRank = Collections.max(rankedStates, Comparator.comparingInt(o -> o.rank));

                if(minDepthMaxRank.rank < lowestForgottenRank)
                    lowestForgottenRank = minDepthMaxRank.rank;
                candidates.remove(minDepthMaxRank);
            }
        }

        return null;
    }
}
