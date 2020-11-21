package tul.kazmierski;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.exit;
import static tul.kazmierski.Util.*;

public class Main {

    public static int[] solvedBoard = null;
    public static Dimensions dimensions = null;
    //TODO consider refactoring and moving this elsewhere
    public static Move[] movesOrder = null;


    /**
     * Required arguments in order:
     * 1. Search strategy
     * 2. Search strategy argument, i.e. order or heuristic id
     * @param args Arguments passed to the program, as described above.
     */
    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Need 2 arguments. Got " + args.length);

        Scanner sc = new Scanner(System.in);
        String dimensionsArg;
        ArrayList<String> rowsArg = new ArrayList<>();

        //parse arguments as Strings
        if (sc.hasNextLine())
            dimensionsArg = sc.nextLine();
        else
            throw new IllegalArgumentException("Line empty. Expected board dimensions");

        while (sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            if (nextLine.length() != 0)
                rowsArg.add(nextLine);
        }

        //set-up session paramaters
        dimensions = parseDimensions(dimensionsArg);
        int[] initialBoard = parseInitialBoard(rowsArg, dimensions);
        solvedBoard = getSolvedBoard(initialBoard);

        if (!checkIfSolvable(initialBoard)) {
            System.err.println("The board is not solvable. Aborting...");
            exit(1);
        }

        switch (args[0]) {
            case "-b":
            case "--bfs":
                movesOrder = parseMovesOrder(args[1]);
                break;
            case "-d":
            case "--dfs":
                movesOrder = parseMovesOrder(args[1]);
                break;
            case "-i":
            case "--idfs":
                movesOrder = parseMovesOrder(args[1]);
                break;
            case "-h":
            case "--bf":
                break;
            case "-a":
            case "--astar":
                break;
            case "-s":
            case "--sma":
                break;
        }

    }
}
