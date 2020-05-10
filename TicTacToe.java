import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

class TicTacToe {
	// take user input for number of players and run solve method
	public static void main(String args[]) {
		// get user input for number of players
		Scanner input = new Scanner(System.in);
		int mode = 0;
		while (mode != 1 && mode != 2) {
			try {
				System.out.print("One-player or two-player? (enter 1 or 2): ");
				mode = Integer.parseInt(input.nextLine());
				if (mode != 1 && mode != 2) System.out.println("Invalid input.");
			}
			catch (Exception e) {
				System.out.println("Invalid input.");
			}
		}
		if (mode==2) {
			// get user input for player one name
			System.out.print("Enter player 1 name: ");
			String p1 = input.nextLine();
			// get user input for player one name
			System.out.print("Enter player 2 name: ");
			String p2 = input.nextLine();
			// play the game with two players
			solve_two(p1, p2);
		}
		else {
			// play the game with one player
			solve_one();
		}
	}

	// METHODS FOR ONE PLAYER
	// play game: player vs computer
	private static void solve_one() {
		// initialize blank board
		String[][] board = {{"_", "_", "_"}, {"_", "_", "_"}, {"_", "_", "_"}};

		// get user input for computer skill level
		Scanner input = new Scanner(System.in);
		int skill = 0;
		while (skill != 1 && skill != 2 && skill != 3) {
			try {
				System.out.print("Choose computer skill level (1-3): ");
				skill = Integer.parseInt(input.nextLine());
				if (skill != 1 && skill != 2 && skill != 3) System.out.println("Invalid input.");
			}
			catch (Exception e) {
				System.out.println("Invalid input.");
			}
		}

		// choose who goes first
		first_player(board, skill);
	}


	// determine if player or computer goes first by guessing a number from 1-100
	// whoever is closer to a random number from 1-100 goes first
	private static void first_player(String[][] board, int skill) {
		Scanner input = new Scanner(System.in);

		// choose numbers
		Random rand = new Random();
		int ans = rand.nextInt(100)+1;
		System.out.print("Choose a number from 1-100 (inclusive): ");
		int player = first_check(input.nextLine());
		int computer = rand.nextInt(100)+1;
		// print numbers
		System.out.println("Answer: " + Integer.toString(ans)+ ", Player Guess: " + Integer.toString(player) + ", Computer Guess: " + Integer.toString(computer));

		// choose who wins- they go first
		if (Math.abs(ans-player) == Math.abs(ans-computer)) {
			System.out.println("Tie! Go again.");
			first_player(board, skill);
		}
		else if (Math.abs(ans-player) < Math.abs(ans-computer)) {
			System.out.println("You go first!");
			solve_player(board, skill);
		}
		else {
			System.out.println("Computer goes first.");
			solve_computer(board, skill);
		}
	}

	// play game when player goes first
	private static void solve_player(String[][] board, int skill){
		// display the board before the player goes
		disp(board);

		// player moves
		player_move(board);
		disp(board);

		// play until someone wins or the board is full
		int count = 0;
		while (count<4) {
			// computer moves
			if (skill == 1) computer_move_1(board);
			if (skill == 2) computer_move_2(board);
			if (skill == 3) computer_move_3(board);
			disp(board);

			// player moves
			player_move(board);
			disp(board);

			count++;
		}
		System.out.println("Tie!");
		end();
	}

	// play game when computer goes first
	private static void solve_computer(String[][] board, int skill){
		// computer moves
		if (skill == 1) computer_move_1(board);
		if (skill == 2) computer_move_2(board);
		if (skill == 3) computer_move_3(board);
		disp(board);

		// play until someone wins or the board is full
		int count = 0;
		while (count<4) {
			// player moves
			player_move(board);
			disp(board);

			// computer moves
			if (skill == 1) computer_move_1(board);
			if (skill == 2) computer_move_2(board);
			if (skill == 3) computer_move_3(board);
			disp(board);

			count++;
		}
		System.out.println("Tie!");
		end();
	}

	// player moves and checks for win
	private static String[][] player_move(String[][] board) {
		System.out.println("--Your move--");

		// player moves (repeat until they have a valid move)
		int turn = 0;
		int row; int col;
		while (turn==0) {
			// get user input for row and column
			Scanner input = new Scanner(System.in);
			System.out.print("Row: ");
			row = input_check_row(input.nextLine());
			System.out.print("Column: ");
			col = input_check_col(input.nextLine());

			// if chosen spot isn't taken
			if (spot_check(board, row, col)) {
				// change that spot to an X
				board[row-1][col-1]="X";
				turn = 1;
			}
			else System.out.println("Spot taken.");
		}
		// check for player win
		if (win(board, "X")) {
			disp(board);
			System.out.println("You win!");
			end();
		}
		return board;
	}

	// computer moves (level 1: random) and checks for win
	private static String[][] computer_move_1(String[][] board) {
		System.out.println("--Computer move--");

		// computer moves (repeat until it has a valid move)
		int turn = 0;
		int row; int col;
		Random rand = new Random();
		while (turn==0) {
			row = rand.nextInt(3) + 1; // random integer from 1-3
			col = rand.nextInt(3) + 1;

			// if chosen spot isn't taken
			if (spot_check(board, row, col)) {
				// change that spot to an O
				board[row-1][col-1]="O";
				turn = 1;
			}
		}
		// check for computer win
		if (win(board, "O")) {
			disp(board);
			System.out.println("Computer wins.");
			end();
		}
		return board;
	}

	// computer moves (level 2) and checks for win
	// if computer can win on next turn, go there
	// if player can win on next turn, block them
	// otherwise, go somewhere random
	private static String[][] computer_move_2(String[][] board) {
		ArrayList<ArrayList<Integer>> free = free_spaces(board);

		// see if computer can win on this turn
		for (int ii=0; ii<free.size(); ii++) {
			ArrayList<Integer> f = free.get(ii);
			String [][] board2 = new String[board.length][];
			for(int i = 0; i < board.length; i++)
			    board2[i] = board[i].clone();
			board2[f.get(0)][f.get(1)] = "O";
			if (win(board2, "O")) {
				System.out.println("--Computer move--");
				disp(board2);
				System.out.println("Computer wins.");
				end();
			}
		}

		// see if player can win on next turn
		for (int ii=0; ii<free.size(); ii++) {
			ArrayList<Integer> f = free.get(ii);
			String [][] board2 = new String[board.length][];
			for(int i = 0; i < board.length; i++)
			    board2[i] = board[i].clone();
			board2[f.get(0)][f.get(1)] = "X";
			if (win(board2, "X")) {
				System.out.println("--Computer move--");
				board[f.get(0)][f.get(1)] = "O";
				return board;
			}
		}

		// otherwise: random
		board = computer_move_1(board);
		return board;
	}

	// computer moves (level 3) and checks for win
	// uses minimax algorithm to find best possible move
	// calculates all remaining posibilities and assumes player uses their best move
	private static String[][] computer_move_3(String[][] board) {
		System.out.println("--Computer move--");

		// find best move
		String [][] board2 = new String[board.length][];
		for(int i = 0; i < board.length; i++)
	    	board2[i] = board[i].clone();
	    ArrayList<Integer> move = findBestMove(board2);

	    // execute move
	    int row = move.get(0);
	    int col = move.get(1);
	    board[row][col] = "O";

	    // check for win
		if (win(board, "O")) {
			disp(board);
			System.out.println("Computer wins.");
			end();
		}
		return board;
	}

	// finds the best computer move using the minimax function
	private static ArrayList<Integer> findBestMove(String[][] board) {
		int best = -1000;
		ArrayList<ArrayList<Integer>> free = free_spaces(board);
		ArrayList<Integer> move = new ArrayList<>(Arrays.asList(0, 0));

		for (int ii=0; ii<free.size(); ii++) {
			ArrayList<Integer> f = free.get(ii);
			String [][] board2 = new String[board.length][];
			for(int i = 0; i < board.length; i++)
		    	board2[i] = board[i].clone();
		    board2[f.get(0)][f.get(1)] = "O";
		    if (minimax(board2, false)>best) {
		    	best = minimax(board2, false);
		    	move = f;
		    }
		}
		return move;
	}

	// executes minimax algorithm to find the best move
	private static int minimax(String[][] board, boolean computer_turn) {
		ArrayList<ArrayList<Integer>> free = free_spaces(board);

		// if the game is over
		if (win(board, "O")) return 1;
		if (win(board, "X")) return -1;
		if (free.size()<1) return 0;

		if (computer_turn) {
			int best = -1000;
			for (int ii=0; ii<free.size(); ii++) {
				ArrayList<Integer> f = free.get(ii);
				String [][] board2 = new String[board.length][];
				for(int i = 0; i < board.length; i++)
			    	board2[i] = board[i].clone();
			    board2[f.get(0)][f.get(1)] = "O";
			    int val = minimax(board2, false);
			    best = Math.max(best, val);
			}
			return best;
		}

		else {
			int best = 1000;
			for (int ii=0; ii<free.size(); ii++) {
				ArrayList<Integer> f = free.get(ii);
				String [][] board2 = new String[board.length][];
				for(int i = 0; i < board.length; i++)
			    	board2[i] = board[i].clone();
			    board2[f.get(0)][f.get(1)] = "X";
			    int val = minimax(board2, true);
			    best = Math.min(best, val);
			}
			return best;
		}
	}

	// returns a list of lists [row, col] of all the free spots on the board
	private static ArrayList<ArrayList<Integer>> free_spaces(String[][] board) {
		ArrayList<Integer> a;
		ArrayList<ArrayList<Integer>> ans = new ArrayList<>();
		int[] intArray = {1, 2, 3};
		for (int r : intArray) {
			for (int c : intArray){
				if(spot_check(board, r, c)) {
					a = new ArrayList<>(Arrays.asList(r-1, c-1));
					ans.add(a);
				}
			}
		}
		return ans;
	}

	private static void end() {
		Scanner input = new Scanner(System.in);
		System.out.print("Play again? (Y/N): ");
		String again = input.nextLine();
		if (again.equals("Y") || again.equals("y") || again.equals("Yes") || again.equals("yes")) main(null);
		else System.exit(0);
	}

	// checks for win
	private static boolean win(String[][] board, String c) {
		//rows
		if (board[0][0]==c && board[0][1]==c && board[0][2]==c) return true;
		if (board[1][0]==c && board[1][1]==c && board[1][2]==c) return true;
		if (board[2][0]==c && board[2][1]==c && board[2][2]==c) return true;
		//columns
		if (board[0][0]==c && board[1][0]==c && board[2][0]==c) return true;
		if (board[0][1]==c && board[1][1]==c && board[2][1]==c) return true;
		if (board[0][2]==c && board[1][2]==c && board[2][2]==c) return true;
		//diagonals
		if (board[0][0]==c && board[1][1]==c && board[2][2]==c) return true;
		if (board[0][2]==c && board[1][1]==c && board[2][0]==c) return true;
		return false;
	}

	// check if a spot the player wants to move is taken
	private static boolean spot_check(String[][] board, int row, int col) {
		if (board[row-1][col-1]=="_") return true; // spot isn't taken
		else return false; // spot is taken
	}

	// check if user input for row is valid (int 1-3)
	private static int input_check_row(String i){
		int n;
		try {
			n = Integer.parseInt(i);
		}
		catch(Exception e) {
			n = 0;
		}
		if (n==1 || n==2 || n==3) return n;
		else {
			System.out.println("Invalid input.");
			Scanner input = new Scanner(System.in);
			System.out.print("Row: ");
			return input_check_row(input.nextLine());
		}
	}

	// check if user input for column is valid (int 1-3)
	private static int input_check_col(String i){
		int n;
		try {
			n = Integer.parseInt(i);
		}
		catch(Exception e) {
			n = 0;
		}
		if (n==1 || n==2 || n==3) return n;
		else {
			System.out.println("Invalid input.");
			Scanner input = new Scanner(System.in);
			System.out.print("Column: ");
			return input_check_row(input.nextLine());
		}
	}

	// check if player's input is valid for first move guess (1-100)
	private static int first_check(String i) {
		int n;
		try {
			n = Integer.parseInt(i);
		}
		catch(Exception e) {
			n = -1; // represents invalid input
		}
		if (n>0 && n<101) {
			return n;
		}
		else {
			System.out.println("Invalid input.");
			// take new input
			Scanner input = new Scanner(System.in);
			System.out.print("Choose a number from 1-100 (inclusive): ");
			return first_check(input.nextLine());
		}
	}

	// print out the board in the correct format
	private static void disp(String[][] board) {
		for (String[] row : board) {
			for (String elem : row) {
				System.out.print(elem+' ');
			}
			System.out.println();
		}
		System.out.println();
	}



	// METHODS FOR TWO PLAYERS
	// play game: player 1 vs player 2
	private static void solve_two(String p1, String p2) {
		// initialize blank board
		String[][] board = {{"_", "_", "_"}, {"_", "_", "_"}, {"_", "_", "_"}};

		// determine which player goes first by both guessing a number from 1-100
		Scanner input = new Scanner(System.in);

		// choose numbers
		Random rand = new Random();
		int ans = rand.nextInt(100)+1;
		System.out.print(p1 + " choose a number from 1-100 (inclusive): ");
		int guess1 = first_check(input.nextLine());
		System.out.print(p2 + " choose a number from 1-100 (inclusive): ");
		int guess2 = first_check(input.nextLine());
		// print numbers
		System.out.println("Answer: " + Integer.toString(ans)+ ", " + p1 + "'s guess: " + Integer.toString(guess1) + ", " + p2 + "'s guess: " + Integer.toString(guess2));

		// choose who wins- they go first
		if (Math.abs(ans-guess1) == Math.abs(ans-guess2)) {
			System.out.println("Tie! Go again.");
			solve_two(p1, p2);
		}
		else if (Math.abs(ans-guess1) < Math.abs(ans-guess2)) {
			System.out.println(p1 + " goes first!");
			solve_player_1(board, p1, p2);
		}
		else {
			System.out.println(p2 + " goes first!");
			solve_player_2(board, p1, p2);
		}		
	}

	// used if player 1 goes first
	private static void solve_player_1(String[][] board, String p1, String p2){
		disp(board);

		// player 1 moves
		board = player_move_1(board, p1, p2);
		disp(board);

		// play until someone wins or the board is full
		int count = 0;
		while (count<4) {
			// player 2 move
			board = player_move_2(board, p1, p2);
			disp(board);

			// player 1 move
			board = player_move_1(board, p1, p2);
			disp(board);

			count++;
		}
		System.out.println("Tie!");
		end_2(p1, p2);
	}

	// used if player 2 goes first
	private static void solve_player_2(String[][] board, String p1, String p2){
		disp(board);

		// player 2 moves
		board = player_move_2(board, p1, p2);
		disp(board);

		// play until someone wins or the board is full
		int count = 0;
		while (count<4) {
			// player 1 move
			board = player_move_1(board, p1, p2);
			disp(board);

			// player 2 move
			board = player_move_2(board, p1, p2);
			disp(board);

			count++;
		}
		System.out.println("Tie!");
		end_2(p1, p2);
	}

	// player 1 turn
	private static String[][] player_move_1(String[][] board, String p1, String p2){
		System.out.println("--" + p1 + "'s move [X]--");
		int turn = 0;
		while (turn==0) {
			Scanner input = new Scanner(System.in);
			System.out.print("Row: ");
			int row = input_check_row(input.nextLine());
			System.out.print("Column: ");
			int col = input_check_col(input.nextLine());
			if (spot_check(board, row, col)) { // if the spot isn't taken
				board[row-1][col-1] = "X";
				turn = 1;
			}
			else System.out.println("Spot taken.");
		}

		// check for win
		if (win(board, "X")) {
			disp(board);
			System.out.println(p1 + " wins!");
			end_2(p1, p2);
		}
		return board;
	}

	// player 2 turn
	private static String[][] player_move_2(String[][] board, String p1, String p2){
		System.out.println("--" + p2 + "'s move [X]--");
		int turn = 0;
		while (turn==0) {
			Scanner input = new Scanner(System.in);
			System.out.print("Row: ");
			int row = input_check_row(input.nextLine());
			System.out.print("Column: ");
			int col = input_check_col(input.nextLine());
			if (spot_check(board, row, col)) { // if the spot isn't taken
				board[row-1][col-1] = "O";
				turn = 1;
			}
			else System.out.println("Spot taken.");
		}

		// check for win
		if (win(board, "O")) {
			disp(board);
			System.out.println(p2 + " wins!");
			end_2(p1, p2);
		}
		return board;
	}

	// option to play again with the same players or exit
	private static void end_2(String p1, String p2){
		Scanner input = new Scanner(System.in);
		System.out.print("Play again with same players? (Y/N): ");
		String again = input.nextLine();
		if (again.equals("Y") || again.equals("y") || again.equals("Yes") || again.equals("yes")) solve_two(p1, p2);
		else System.exit(0);
	}


}