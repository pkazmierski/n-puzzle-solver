package tul.kazmierski.heuristics;

import tul.kazmierski.Heuristic;

import java.util.ArrayList;

import static tul.kazmierski.Util.countInversions;

public class InversionsHeuristic implements Heuristic {
    @Override
    public int getRank(ArrayList<Integer> board) {
        return countInversions(board);
    }
}
