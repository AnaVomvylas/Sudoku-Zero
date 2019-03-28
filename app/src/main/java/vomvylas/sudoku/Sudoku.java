package vomvylas.sudoku;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ViewDebug;

public class Sudoku extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "Debugging message: ";
    public static final String PREFERENCES = "prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Setup up Listeners for main.xml
        View continueButton = findViewById(R.id.continue_game_button);
        continueButton.setOnClickListener(this);
        View statsButton = findViewById(R.id.stats_button);
        statsButton.setOnClickListener(this);
        View newButton = findViewById(R.id.new_game_button);
        newButton.setOnClickListener(this);
    }

    // Listeners gia ta koumpia tou main.xml
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_game_button:
                Log.d(TAG, "clicked on new game button");
                newGameDialog();
                break;
            // add it later
            case R.id.continue_game_button:
                SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                Log.d(Sudoku.TAG, "saved game on continue" + prefs.getBoolean("savedGame", false));
                if (prefs.getBoolean("savedGame", false)) // If there is a saved game
                    createPuzzle(-1);
                break;
            case R.id.stats_button:
                statsDialog();
                break;
        }
    }

    // Dialog box when pressing new game
    private void newGameDialog() {
        // set title = title, set items = array + listener
        new AlertDialog.Builder(this).setTitle(R.string.new_game_dialog).setItems(R.array.difficulty, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createPuzzle(i);
            }
        }).show();
    }

    // Dialog box when pressing stats
    private void statsDialog() {
        String message;
        // get the stats values from the shared preferences
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        message = getResources().getString(R.string.extremelyeasy_label) + "   " + Integer.toString(prefs.getInt("extremely_easy_stats", 0)) + " \n" +
                getResources().getString(R.string.easy_label) + "   " + String.valueOf((prefs.getInt("easy_stats", 0))) + " \n" +
                getResources().getString(R.string.medium_label) + "   " + String.valueOf((prefs.getInt("medium_stats", 0))) + " \n" +
                getResources().getString(R.string.hard_label) + "   " + String.valueOf((prefs.getInt("hard_stats", 0))) + " \n" +
                getResources().getString(R.string.insane_label) + "   " + String.valueOf((prefs.getInt("insane_stats", 0)));

        Log.d(TAG, "stats dialog message = " + Integer.toString(prefs.getInt("extremely_easy_stats", 0)));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(R.string.stats_label);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }


    //Initiate Game activity
    private void createPuzzle(int i) {
        // puzzle creation here with difficulty i = 0,1,2,3,4 extreasy, easy, medium, hard, insane
        Intent intent = new Intent(Sudoku.this, Game.class);
        intent.putExtra(Game.KEY_DIFFICULTY, i);
        startActivity(intent);
    }
}
