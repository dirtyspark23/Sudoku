package com.appify.stormwest.Sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Game extends Activity {
	private static final String TAG = "Sudoku";
	
	public static final String KEY_DIFFICULTY = "com.appify.stormwest.Sudoku.difficulty";
	
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_MEDIUM = 1;
	public static final int DIFFICULTY_HARD = 2;
	
	private int puzzle[];
	
	private PuzzleView puzzleView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		int diff = getIntent(.getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
		puzzle = getPuzzle(diff);
		calculateUsedTiles();
		
		puzzleView = new PuzzleView(this);
		setContentView(puzzleVIew);
		puzzleView.requestFocus();
	}
}
