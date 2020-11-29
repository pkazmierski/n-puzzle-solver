package tul.kazmierski.heuristics;

import tul.kazmierski.Heuristic;
import tul.kazmierski.Main;

import java.util.ArrayList;

public class MisplacedTilesHeuristic implements Heuristic {
    @Override
    public int getRank(ArrayList<Integer> board) {
        int misplacedCounter = 0;
        for(int i = 0; i < board.size(); i++) {
            int currentBoardValue = board.get(i);
            int solvedBoardValue = Main.solvedBoard.get(i);
            if(currentBoardValue != solvedBoardValue && currentBoardValue != 0)
                misplacedCounter++;
        }
        return misplacedCounter;
    }
}
