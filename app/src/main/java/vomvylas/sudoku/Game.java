package vomvylas.sudoku;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;



public class Game extends AppCompatActivity {
    public static final String KEY_DIFFICULTY = "difficulty";
    public static final String PREFERENCES ="prefs";
    public static int[][] solved_grid; // my SOLVED sudoku grid
    public static int[][] dug_grid; //my DUG sudoku grid, ready to be solved
    public static int[][] current_grid; // the current grid, being modified by user input
    public static int diff; //0 to 4
    private PuzzleView puzzleView;
    public static boolean savedStats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diff = getIntent().getIntExtra(KEY_DIFFICULTY, 2);
        SharedPreferences prefs = getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        if (diff == -1){ // if the continue button was pressed
            current_grid = stringToGrid(prefs.getString("current_grid","DEFAULT"));
            //Log.d(Sudoku.TAG, "current" + Arrays.deepToString(current_grid));
            solved_grid = stringToGrid(prefs.getString("solved_grid","DEFAULT"));
            dug_grid = stringToGrid(prefs.getString("dug_grid","DEFAULT"));
            diff = prefs.getInt("diff",2);
        }
        else
            createGrids(diff);

        puzzleView = new PuzzleView(this);
        this.setContentView(puzzleView);
        puzzleView.requestFocus();
        setTitle(diffToString(diff) + " Puzzle");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // never turn off screen


        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("savedGame", true).apply();

        savedStats = false; // save stats only once protection
    }

    @Override
    protected void onPause(){
        super.onPause();

        //Save the current_grid
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE); //prefs for reading saved values
        SharedPreferences.Editor editor = prefs.edit(); //editor for writing values

        editor.putString("current_grid",gridToString(current_grid)).apply();
        editor.putString("dug_grid",gridToString(dug_grid)).apply();
        editor.putString("solved_grid",gridToString(solved_grid)).apply();
        editor.putInt("diff",diff).apply();

        //Log.d(Sudoku.TAG,"Game, not saved on Pause" + getPreferences(MODE_PRIVATE).getBoolean("savedGame",true));

    }

    //difficulty integer to string
    protected String diffToString(int difficulty) {
        String str = new String();
        switch (difficulty) {
            case 0:
                str = "Extremely Easy";
                break;
            case 1:
                str = "Easy";
                break;
            case 2:
                str=  "Medium";
                break;
            case 3:
                str = "Hard";
                break;
            case 4:
                str = "Insane";
                break;
        }
        return str;
    }

    private String gridToString(int[][] grid){
        Log.d(Sudoku.TAG,"stin function einai" +Arrays.deepToString(grid));

        return Arrays.deepToString(grid);

    }
    private int[][] stringToGrid(String str) {
        Log.d(Sudoku.TAG, "stin function2 einai STRing" + str);
        int[][] grid = new int[9][9];

        int row;
        int col;
        int index = 0;
        int counter = 0;
        while (counter < 81) {
            if (str.charAt(index) == '[' || str.charAt(index) == ']')
                index++;
            else if (str.charAt(index) == ',')
                index++;
            else if (str.charAt(index) == ' ')
                index++;
            else {
                row = counter / 9;
                col = counter - row * 9;
                grid[row][col] = str.charAt(index) - '0';
                index++;
                counter++;
            }
        }

        Log.d(Sudoku.TAG, "stin function2 einai" + Arrays.deepToString(grid));
        return grid;
    }

    //returns true if the 2 grids are the same
    protected boolean compareGrids(int [][] grid1,int [][] grid2){
        for (int i=0; i<9;i++){
            for (int j=0;j<9;j++){
                if (grid1[i][j] != grid2[i][j] )
                    return false;
            }
        }
        return true;
    }

    // getters
    protected int[][] getSolvedGrid(){
        return solved_grid;
    }
    protected int[][] getCurrentGrid(){
        return current_grid;
    }
    protected int[][] getDugGrid(){
        return dug_grid;
    }

    // returns the number on the specified tile on String form
    protected String getTileString(int x, int y, int[][] grid){
        return Integer.toString(grid[x][y]);
    }

    //Dialog for the submit button
    protected void submitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.submit_dialog_message)
                        .setTitle(R.string.submit_dialog_title);
                builder.setPositiveButton(R.string.submit_dialog_back, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Back button
                        Intent intent = new Intent(Game.this, Sudoku.class);
                        startActivity(intent);
                        finish(); //adding this to kill the activity, so that it's not accessible by the back button
            }
        });
        Log.d(Sudoku.TAG, " submit dialog end");
        builder.create();
        builder.show();
        saveStats();

        // set saved game to false
        SharedPreferences prefs = getSharedPreferences("prefs",MODE_PRIVATE); //prefs for reading saved values
        SharedPreferences.Editor editor = prefs.edit(); //editor for writing values
        editor.putBoolean("savedGame",false).apply();
        Log.d(Sudoku.TAG, "saved game after dialog"+ prefs.getBoolean("savedGame",false));
    }

    // save stats
    protected void saveStats(){
        if (!savedStats){
            int score;
            SharedPreferences prefs = getSharedPreferences(PREFERENCES,MODE_PRIVATE); //prefs for reading saved values
            SharedPreferences.Editor editor = prefs.edit(); //editor for writing values
            switch (diff){
                case 0:
                    score = prefs.getInt("extremely_easy_stats",0) +1;
                    editor.putInt("extremely_easy_stats", score).apply();
                    break;
                case 1:
                    score = prefs.getInt("easy_stats",0) + 1;
                    editor.putInt("easy_stats", score).apply();
                    break;
                case 2:
                    score = prefs.getInt("medium_stats",0) +1;
                    editor.putInt("medium_stats", score).apply();
                    break;
                case 3:
                    score = prefs.getInt("hard_stats",0) +1;
                    editor.putInt("hard_stats", score).apply();
                    break;
                case 4:
                    score = prefs.getInt("insane_stats",0) +1;
                    editor.putInt("insane_stats", score).apply();
                    break;
            }
        }
        savedStats = true;
    }

    //returns a random cell that is empty (grid == 0). If no empty cells are available return -1
    protected int getRandomEmptyCell(int[][] grid){
        ArrayList<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 9; i++) {  //
            for (int j = 0; j < 9; j++)
                if (grid[i][j] == 0)
                    emptyCells.add(i*9+j);
        }
        if (emptyCells.isEmpty())
            return -1;
        Collections.shuffle(emptyCells);
        return emptyCells.get(0);
    }

    // sets the input number on the current grid
    protected void setTile(int x, int y, int number){
        current_grid[x][y] = number;
    }

    // creates the solved_grid and the dug_grid
    private void createGrids(int diff) {
        solved_grid = new int[9][9];
        dug_grid = new int[9][9];
        current_grid = new int[9][9];


        do { // do it until init Randoms gives something solvable
            solved_grid = initRandoms();
            try {
                solved_grid = solver(solved_grid);
            }
            catch (Exception e){
                solved_grid = new int [9][9];
            }
        } while (ArrayUtils.isEmpty(solved_grid));


        dug_grid = digHoles(solved_grid, diff); // to vazw prin to shuffling gia na glitwsw xrono apo ton solver, an to anakatepsw prwta auksanetai polu to runtime


        // Shuffling solved grid
        for (int i=0; i<5; i++)
            shuffleGrid(solved_grid);
        //solved grid finished

        // Matching dug grid to solved grid
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++){
                if ((dug_grid[i][j] != 0) && (dug_grid[i][j] != solved_grid[i][j]))
                    dug_grid[i][j] = solved_grid[i][j];
            }
        }//Dug grid ready


        //Log.d(Sudoku.TAG,"solved grid =" +Arrays.deepToString(solved_grid));
        //Log.d(Sudoku.TAG,"dug grid =" +Arrays.deepToString(dug_grid));


        // Initializing current grid
        for (int i=0; i<9;i++){
            for(int j=0;j<9;j++)
                current_grid[i][j] = dug_grid[i][j];
        }
        //Current grid ready
    }

    // Vazei ta prwta n random noumera sto [9][9], mporei na mhn lunetai to apotelesma
    private int[][] initRandoms() {
        final int n = 9; // number of cells to be filled
        int[][] grid = new int [9][9];
        ArrayList<Integer> emptyCells = getShuffledList(0,80); // 1d mapper gia to 2d grid
        int k = 0;
        while (k < n) { // fill n cells
            int cellNumb = emptyCells.get(k); // mia timh apo to shuffled mapper mou
            int row = cellNumb / 9; // row major, diladi to mapping tou 9*9 ginetai left->right, top->bottom
            int col = cellNumb - row * 9;

            ArrayList<Integer> randNumb = getShuffledList(1,9); //initialize with all values 1-9 randomly

            boolean backtrack = true;
            for (int j = 0; j < 9; j++) {
                grid[row][col] = randNumb.get(j); //add a number 1-9 form the list
                if (isValid(grid, row, col)) {
                    backtrack = false;
                    break;
                }
            }
            if (backtrack) {
                grid[row][col] = 0;
                k--;
            } else
                k++;
        }
        return grid;
    }

    //checks the sudoku rules for a specific value M[r][c]
    private static boolean isValid(int[][] M, int r, int c) {
        int v = M[r][c]; // my int value that i am checking

        //check column
        for (int i = 0; i < 9; i++) {
            if (i == r)
                continue;
            if (M[i][c] == v)
                return false;
        }
        //check row
        for (int j = 0; j < 9; j++) {
            if (j == c)
                continue;
            if (M[r][j] == v)
                return false;
        }
        //check block
        for (int i = (r / 3) * 3; i < (r / 3) * 3 + 3; i++) {
            for (int j = (c / 3) * 3; j < (c / 3) * 3 + 3; j++) {
                if (i == r && j == c)
                    continue;
                if (M[i][j] == v)
                    return false;
            }
        }
        return true;
    }

    //returns empty grid if its not solvable
    private static int[][] solver(int[][] M) {
        int [][] grid = new int [9][9];
        for (int i = 0; i < 9; i++) {  // copying M, because java is pass-by-value
            for (int j = 0; j < 9; j++)
                grid[i][j] = M[i][j];
        }

        LinkedList<Integer> emptyCells = new LinkedList<>();
        for (int i = 0; i < 9; i++) {  //fill up a list with the empty cells, the cells count from 0 to 81
            for (int j = 0; j < 9; j++) {
                if (grid[i][j] == 0)
                    emptyCells.add((i * 9)+j);
            }
        }

        //Collections.shuffle(emptyCells);   //for variety //TODO na grapsw giati exei thema me to runtime etsi

        int pointer = 0; // pointer for emptyCells list
        do { // Fill up a cell until all cells are filled
            boolean backtrack = true;
            int cellNumb = emptyCells.get(pointer);
            int row = cellNumb / 9;
            int col = cellNumb - (row * 9);

            //try to fill up the cell
            for (int i = grid[row][col] + 1; i < 10; i++) {
                grid[row][col] = i;
                //Log.d(Sudoku.TAG,"grid[row[col]:" +grid[row][col]);
                if (isValid(grid, row, col)) { //if the digit on the current cell is valid
                    backtrack = false;
                    break;
                }
            }

            if (backtrack) { //if it didn't manage to find a valid digit for the cell
                grid[row][col] = 0; //reset cell
                pointer --; //go to the previous cell
                if (pointer < 0)   // no more cells to backtrack to
                    return (new int[9][9]); //return null grid
            } else
                pointer++;

        } while (pointer < emptyCells.size());
        //Log.d(Sudoku.TAG,"teleiwse to solver");
        return grid;
    }

    //returns list with the cell numbers in sequence according to difficulty
    private static LinkedList<Integer> getDiggable_cells(int diff) {
        String[] sequence = {"randglob", "randglob", "jumpone", "Sshape", "leftbottom"};
        LinkedList<Integer> diggable_cells = new LinkedList<>();
        switch (sequence[diff]) {
            case "randglob":
                for (int i = 0; i < 81; i++)
                    diggable_cells.add(i);
                Collections.shuffle(diggable_cells);
                break;
            case "jumpone":
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 81; j += 2)
                        if (i == 0 && j % 2 == 0) //add evens
                            diggable_cells.add(j);
                        else if (i == 1 && j % 2 == 1) // add odds
                            diggable_cells.add(j);
                }
                break;
            case "Sshape":
                int rownumber = 0;
                while (rownumber < 9) {
                    if (rownumber % 2 == 0) { // if the row is even starting from 0
                        for (int i = 0; i < 9; i++)
                            diggable_cells.add(i + (rownumber * 9));
                    } else {
                        for (int i = 8; i > -1; i--)
                            diggable_cells.add(i + (rownumber * 9));
                    }
                    rownumber++;
                }
                break;
            case "leftbottom":
                for (int i = 0; i < 81; i++)
                    diggable_cells.add(i);
                break;
        }
       // Log.d(Sudoku.TAG,"to list diggable cells einai:" + diggable_cells.toString());
        return diggable_cells;
    } // not used but kept for possible future updates

    //returns a shuffled list  containing Integers from start to finish
    private static ArrayList<Integer> getShuffledList(int start, int finish) {
        ArrayList<Integer> randNumb = new ArrayList<>();
        for (int i = start; i < finish + 1; i++)
            randNumb.add(i);
        Collections.shuffle(randNumb);
        return randNumb;
    }

    //shuffles the given grid for variety (still obeys rules)
    private static int[][] shuffleGrid(int[][] grid) {

        Random rand = new Random();
        int type = rand.nextInt(3); // 4 types of shuffling
        String[] shuffle_types = {"digits", "columns", "blocks", "rotate"};


        switch (shuffle_types[type]) {
            case "digits":
                ArrayList<Integer> randNumb = getShuffledList(1, 9);
                int digit1 = randNumb.get(0);
                int digit2 = randNumb.get(1);
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++)
                        if (grid[i][j] == digit1)
                            grid[i][j] = digit2;
                        else if (grid[i][j] == digit2)
                            grid[i][j] = digit1;
                }
                break;
            case "columns":
                ArrayList<Integer> randColumn = getShuffledList(0, 2);
                int col1 = randColumn.get(0);
                int col2 = randColumn.get(1);
                ArrayList<Integer> randBlock = getShuffledList(0, 2);
                int block = randBlock.get(0);
                for (int i = 0; i < 9; i++) { //swap 2 values
                    int temp = grid[i][col1 +block*3];
                    grid[i][col1+block*3] = grid[i][col2+block*3];
                    grid[i][col2+block*3] = temp;
                }
                break;
            case "blocks":
                ArrayList<Integer> randBlocks = getShuffledList(0, 2);
                int block1 = randBlocks.get(0);
                int block2 = randBlocks.get(1);
                for (int col = 0; col < 3; col++) {
                    for (int i = 0; i < 9; i++) { //swap 2 values
                        int temp = grid[i][col + block1 * 3];
                        grid[i][col + block1 * 3] = grid[i][col + block2 * 3];
                        grid[i][col + block2 * 3] = temp;
                    }
                }
                break;
            case "rotate":
                int [][] rotated_grid = new int[9][9];
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        rotated_grid[j][8-i] = grid[i][j]; //mallon swsto einai
                    }
                }
                grid = rotated_grid;
                break;
        }
    return grid;
    }

    //returns grid ready to be given to the user for solving
    private static int[][] digHoles(int[][] M, int diff) {
        int [][] grid = new int [9][9];
        for (int i = 0; i < 9; i++) { //creating local instance of grid
            for (int j = 0; j < 9; j++)
                grid[i][j] = M[i][j];
        }

        Random rand = new Random();
        int given_cells = 81; //how many cells contain a number. Since we get a solved grid, its 81 for now
        int[] lower_bound = {5, 4, 3, 2, 0}; //lower bound of givens in each row and column for each difficulty
        int[][] givens = {{50, 70}, {36, 49}, {32, 35}, {28, 31}, {22, 27}}; //range of givens for each difficulty level
        int givens_value = rand.nextInt(givens[diff][1] - givens[diff][0]) + givens[diff][0]; // random value between the givens

        ArrayList<Integer> diggable_cells = getShuffledList(0,80);

        //Dig 1st cell so that solver() doesnt throw error when empty_cells array is empty
        grid[diggable_cells.get(0)/9][diggable_cells.get(0) - diggable_cells.get(0)/9* 9] = 0;
        diggable_cells.remove(0);

        while (!diggable_cells.isEmpty()) {
            int cellNumb = diggable_cells.get(0);
            int row = cellNumb / 9;
            int col = cellNumb - (row * 9);

            // find how many givens are on the row and col
            int non_zeros_row = 0;
            int non_zeros_col = 0;
            for (int i = 0; i < 9; i++) {
                if (grid[row][i] != 0)
                    non_zeros_col++;
                if (grid[i][col] != 0)
                    non_zeros_row++;
            }

            //Log.d(Sudoku.TAG, "booleans gia ta restrictions:" + (given_cells >= givens_value) + (non_zeros_row > lower_bound[diff]) +(non_zeros_col > lower_bound[diff]));
            // Log.d(Sudoku.TAG, "diggable cells size:" +diggable_cells.size());

            boolean unique_solution = true;
            int number_before_testing = grid[row][col]; //the number contained in the cell before trying to test for multiple solutions
            if ((given_cells >= givens_value) && (non_zeros_row > lower_bound[diff]) && (non_zeros_col > lower_bound[diff])) { // difficulty restrictions

                //trying all numbers 1-9 to check for unique solution
                for (int i = 1; i < 10; i++) {
                    if (number_before_testing == i)
                        continue;
                    grid[row][col] = i;
                    if (isValid(grid, row, col)){// if the solver reports a solution for a different set number other than the original , then its not unique
                        try {
                            if (solver(grid)[0][0] != 0) {
                                unique_solution = false;
                                break;
                            }
                        }
                        catch (Exception e){
                            unique_solution = true;
                            }
                    }

                }


                if (unique_solution) {
                    grid[row][col] = 0;
                    given_cells--;
                }
                else
                    grid[row][col] = number_before_testing;
            }
            diggable_cells.remove(0);
            }
        return grid;
    }


}


