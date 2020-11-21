package tul.kazmierski;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Need 3 arguments. Got " + args.length);
            exit(1);
        }

        switch(args[0]) {
            case "-b":
            case "--bfs":
                break;
            case "-d":
            case "--dfs":
                break;
            case "-i":
            case "--idfs":
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
