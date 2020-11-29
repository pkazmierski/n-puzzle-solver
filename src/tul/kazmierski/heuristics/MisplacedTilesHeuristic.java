package tul.kazmierski.heuristics;

import tul.kazmierski.Heuristic;
import tul.kazmierski.Main;

import java.util.ArrayList;

public class MisplacedTilesHeuristic implements Heuristic {
    @Override
    public int getRank(ArrayList<Integer> board) {
        int misplacedCounter = 0;
        for(int i = 0; i < board.size(); i++) {
            if(!board.get(i).equals(Main.solvedBoard.get(i)))
                misplacedCounter++;
        }
        return misplacedCounter;
    }
}
