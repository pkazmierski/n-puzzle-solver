package tul.kazmierski;

import tul.kazmierski.heuristics.InversionsHeuristic;
import tul.kazmierski.heuristics.LinearConflictWithManhattanHeuristic;
import tul.kazmierski.heuristics.ManhattanDistanceHeuristic;
import tul.kazmierski.heuristics.MisplacedTilesHeuristic;
import tul.kazmierski.solvers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.System.exit;
import static tul.kazmierski.Util.*;

public class Main {

    public static ArrayList<Integer> solvedBoard = null;
    public static Dimensions dimensions = null;
    public static Move[] movesOrder = null;
    public static long startTime = 0;
    public static long endTime = 0;
    public static int visitedCounter = 0;
    public static long usedMemoryKb = 0;
    public static String failReason = "";
    public static HashMap<Move, Move> oppositeMoves = new HashMap<>();


    /**
     * Required arguments in order:
     * 1. Search strategy (algorithm) - refer to one of the switches below
     * 2. Moves order, i.e. LUDR  checks first left, then up, then down and finally right; use just R for random
     * 3. Heuristic ID - 1-4 (refer to gerHeuristic); put anything if using BFS, DFS or IDDFS
     * 4. File name - for proper output for report, not actually used to read from it
     * Input is done via stdin, first two columns are "width height"
     * NOTE: only NxN boards are supported now
     * The next lines are space separated values for each row, e.g. "4 0 5"
     * 0 = blank tile
     * By default the output is to stdout, single line, tab separated value in this order:
     * Heuristic, Moves order, Time (ms), Visited nodes, Memory (KB), Exit status, Moves count, Moves list
     * Solving is abandoned if
     * a) program has less than 1 MB of memory in JVM
     * b) it takes longer than 60 seconds to solve
     * Before solving solvability is checked; if the board is unsolvable, the program is terminated
     * By default visualizations are enabled, comment out visualizeSolution(finalState); to disable them
     * @param args Arguments passed to the program, as described above.
     */
    public static void main(String[] args) {
        //init
        oppositeMoves.put(Move.DOWN, Move.UP);
        oppositeMoves.put(Move.UP, Move.DOWN);
        oppositeMoves.put(Move.LEFT, Move.RIGHT);
        oppositeMoves.put(Move.RIGHT, Move.LEFT);

        //report mode
        if (args.length == 1 && args[0].equals("--report")) {
            reportMode(args);
        } else {
            normalMode(args);
        }
    }

    private static State callAlgorithm(ArrayList<Integer> initialBoard, String algorithm, String heuristicCode) {
        PuzzleSolverOrder puzzleSolverOrder;
        PuzzleSolverHeuristic puzzleSolverHeuristic;
        State finalState = null;
        Heuristic heuristic = null;

        switch (algorithm) {
            case "-b":
            case "--bfs":
                puzzleSolverOrder = new bfsSolver();
                finalState = puzzleSolverOrder.solveWithOrder(initialBoard, movesOrder);
                break;
            case "-d":
            case "--dfs":
                puzzleSolverOrder = new dfsSolver();
                finalState = puzzleSolverOrder.solveWithOrder(initialBoard, movesOrder);
                break;
            case "-i":
            case "--iddfs":
                puzzleSolverOrder = new iddfsSolver();
                finalState = puzzleSolverOrder.solveWithOrder(initialBoard, movesOrder);
                break;
            case "-h":
            case "--bf":
                puzzleSolverHeuristic = new bestFirstSolver();
                heuristic = getHeuristic(heuristicCode);
                finalState = puzzleSolverHeuristic.solveWithHeuristic(initialBoard, heuristic);
                break;
            case "-a":
            case "--astar":
                puzzleSolverHeuristic = new AStarSolver();
                heuristic = getHeuristic(heuristicCode);
                finalState = puzzleSolverHeuristic.solveWithHeuristic(initialBoard, heuristic);
                break;
            case "-s":
            case "--sma":
                puzzleSolverHeuristic = new SMAStarSolver();
                heuristic = getHeuristic(heuristicCode);
                finalState = puzzleSolverHeuristic.solveWithHeuristic(initialBoard, heuristic);
                break;
            default:
                throw new IllegalArgumentException("Incorrect 1st argument");
        }

        return finalState;
    }

    private static Heuristic getHeuristic(String arg) {
        switch (arg) {
            case "1":
                return new MisplacedTilesHeuristic();
            case "2":
                return new InversionsHeuristic();
            case "3":
                return new ManhattanDistanceHeuristic();
            case "4":
                return new LinearConflictWithManhattanHeuristic();
            default:
                return null;
        }
    }

    private static String getPuzzleSolverName(String arg) {
        switch (arg) {
            case "-b":
            case "--bfs":
                return "BFS";
            case "-d":
            case "--dfs":
                return "DFS";
            case "-i":
            case "--iddfs":
                return "IDDFS";
            case "-h":
            case "--bf":
                return "Best-first";
            case "-a":
            case "--astar":
                return "A*";
            case "-s":
            case "--sma":
                return "SMA*";
            default:
                throw new IllegalArgumentException("Incorrect algorithm argument");
        }
    }

    private static void normalMode(String[] args) {
        //1: algorithm, 2: move order, 3: heuristic, 4: filename (sample_boardX.txt)
        if (args.length != 4)
            throw new IllegalArgumentException("Need 4 arguments. Got " + args.length);

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
        sc.close();

        //set-up session paramaters
        dimensions = parseDimensions(dimensionsArg);
        ArrayList<Integer> initialBoard = parseInitialBoard(rowsArg, dimensions);
        solvedBoard = generateSolvedBoard(initialBoard);

        if (!checkIfSolvable(initialBoard)) {
            System.err.println("The board is not solvable. Aborting...");
            exit(1);
        }

//        System.out.println("Input board: ");
//        printBoard(initialBoard);
//        System.out.println("Solved board: ");
//        printBoard(solvedBoard);

        movesOrder = parseMovesOrder(args[1]);
//        System.out.println("Moves order: " + Arrays.toString(movesOrder));

        startTime = System.currentTimeMillis();
        State finalState = callAlgorithm(initialBoard, args[0], args[2]);
        endTime = System.currentTimeMillis();

//        System.out.println("Time to complete: " + (endTime - startTime) + " ms");
//        System.out.println("Nodes visited: " + visitedCounter);
//        if (finalState == null)
//            throw new IllegalArgumentException("Unknown error. Cannot solve the board");

        String algorithmName = getPuzzleSolverName(args[0]);
        Heuristic heuristic = getHeuristic(args[2]);
        String heuristicName = heuristic == null ? "N/A" : heuristic.getClass().getSimpleName();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(args[3]).append('\t') //filename
                .append(algorithmName).append('\t') //algorithm
                .append(heuristicName).append('\t') //heuristic
                .append(args[1]).append('\t') //moves order
                .append(endTime - startTime).append('\t')
                .append(visitedCounter).append('\t')
                .append(usedMemoryKb).append('\t');

        if (finalState != null) {
            Move[] solutionMoves = reconstructSolution(finalState);
            stringBuilder
                    .append("PASS").append('\t')
                    .append(solutionMoves.length).append('\t')
                    .append(Arrays.toString(solutionMoves));
        } else {
            stringBuilder
                    .append(failReason.equals("") ? "UNKNOWN_FAIL" : failReason).append('\t')
                    .append(-1).append('\t') //solution moves length
                    .append("[]"); //solution moves
        }

        System.out.print(stringBuilder.toString());

//        System.out.println("Number of moves: " + solutionMoves.length);
//        System.out.println(Arrays.toString(solutionMoves));
        if (finalState != null) {
            visualizeSolution(finalState);
        }
    }

    private static void reportMode(String[] args) {
        ArrayList<String> fileNames = new ArrayList<>(3);
        fileNames.add("sample_board1.txt");
        fileNames.add("sample_board4.txt");
        fileNames.add("sample_board7.txt");

        File currentBoardFile;

        for (String fileName : fileNames) {
            currentBoardFile = new File("assets/" + fileName);
            Scanner fileReader = null;
            try {
                fileReader = new Scanner(currentBoardFile);

                //parse dimensions
                String dimensionsArg;
                if (fileReader.hasNextLine()) {
                    dimensionsArg = fileReader.nextLine();
                    dimensions = parseDimensions(dimensionsArg);
                } else
                    throw new IllegalArgumentException("Line empty. Expected board dimensions");

                //parse rows
                ArrayList<String> rowsArg = new ArrayList<>();
                while (fileReader.hasNextLine()) {
                    String nextLine = fileReader.nextLine();
                    if (nextLine.length() != 0)
                        rowsArg.add(nextLine);
                }

                ArrayList<Integer> initialBoard = parseInitialBoard(rowsArg, dimensions);
                solvedBoard = generateSolvedBoard(initialBoard);

                if (!checkIfSolvable(initialBoard)) {
                    System.err.println("The board is not solvable. Aborting...");
                    exit(1);
                }

                String[] moveOrders = new String[]{"LDUR", "RUDL", "DRUL", "UDRL"};
                for (String mOrder : moveOrders) {
                    movesOrder = parseMovesOrder(mOrder);
                    String[] algorithmArgs = new String[]{"--bfs", "--dfs", "--iddfs", "--bf", "--astar", "--sma"};
                    for (String algorithmArg : algorithmArgs) {

                        Heuristic heuristic;

                        String[] heuristicsCodes;
                        if (algorithmArg.equals("--bf") || algorithmArg.equals("--astar") || algorithmArg.equals("--sma"))
                            heuristicsCodes = new String[]{"1", "2", "3", "4"};
                        else
                            heuristicsCodes = new String[]{"NULL"};

                        for (String heuristicCode : heuristicsCodes) {
                            if (!heuristicCode.equals("NULL"))
                                heuristic = getHeuristic(heuristicCode);
                            else
                                heuristic = null;
                            String heuristicName = heuristic == null ? "N/A" : heuristic.getClass().getSimpleName();
                            StringBuilder stringBuilder = new StringBuilder();

                            String algorithmName = getPuzzleSolverName(args[0]);

                            stringBuilder
                                    .append(fileName).append('\t')
                                    .append(algorithmName).append('\t')
                                    .append(heuristicName).append('\t')
                                    .append(mOrder).append('\t');

                            visitedCounter = 0;
                            usedMemoryKb = 0;
                            failReason = "";

                            startTime = System.currentTimeMillis();
                            State finalState = callAlgorithm(initialBoard, algorithmArg, heuristicCode);
                            endTime = System.currentTimeMillis();

                            stringBuilder
                                    .append(endTime - startTime).append('\t')
                                    .append(visitedCounter).append('\t');

                            if (finalState == null) {
                                Runtime.getRuntime().gc();
                                if (failReason.equals("OUT_OF_MEMORY")) {
                                    Thread.sleep(10000);
                                }
                                stringBuilder.append(failReason.equals("") ? "UNKNOWN_FAIL" : failReason).append('\t');
                                stringBuilder
                                        .append(-1).append('\t') //solution moves length
                                        .append("[]"); //solution moves
                            } else {
                                stringBuilder.append("PASS").append('\t');

                                Move[] solutionMoves = reconstructSolution(finalState);
                                stringBuilder
                                        .append(solutionMoves.length).append('\t')
                                        .append(Arrays.toString(solutionMoves));
                            }

                            System.out.println(stringBuilder.toString());
                        }
                    }
                }
                fileReader.close();
            } catch (FileNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
