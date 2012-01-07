/* Copyright (C) 2011 Appify - Stormwest Corporation

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at
  
          http://www.apache.org/licenses/LICENSE-2.0
  
     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/

package com.appify.stormwest.Sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class PuzzleView extends View {
	private static final String TAG = "Sudoku";
	private final Game game;
	
	public PuzzleView(Context context) {
		super(context);
		this.game = (Game) context;
		setFocusable(true);
		setFocusableInTouchMode(true);
	}
	
	private float width;
	private float height;
	private int selX;
	private int selY;
	private final Rect selRect = new Rect();
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w / 9f;
		height = h / 9f;
		getRect(selX, selY, selRect);
		Log.d(TAG, "onSizeChanged: width " + width + ", height " + height);
	}
	private void getRect(int x, int y, Rect rect) {
		rect.set((int) (x * width), (int) (y * height), (int) (x * width + width), (int) (y * height + height));
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// Draw the background
		Paint background = new Paint();
		background.setColor(getResources().getColor(R.color.puzzle_background));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
		
		// Draw the board...
		// Define colors for the grid lines
		Paint dark = new Paint();
		dark.setColor(getResources().getColor(R.color.puzzle_dark));
		
		Paint hilite = new Paint();
		hilite.setColor(getResources().getColor(R.color.puzzle_hilite));
		
		Paint light = new Paint();
		light.setColor(getResources().getColor(R.color.puzzle_light));
		
		// Draw the minor grid lines
		for (int i = 0; i < 9; i++) {
			canvas.drawLine(0, i * height, getWidth(), i * height, light);
			canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, hilite);
			canvas.drawLine(i * width, 0, i * width, getHeight(), light);
			canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), hilite);
		}
		
		// Draw the major grid lines
				for (int i = 0; i < 9; i++) {
					if (i % 3 != 0)
						continue;
					
					canvas.drawLine(0, i * height, getWidth(), i * height, dark);
					canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1, hilite);
					canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
					canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(), hilite);
				}	
		// Draw the numbers...
		// Define color and style for numbers
		Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);
		foreground.setColor(getResources().getColor(R.color.puzzle_foreground));
		foreground.setStyle(Style.FILL);
		foreground.setTextSize(height * 0.75f);
		foreground.setTextScaleX(width / height);
		foreground.setTextAlign(Paint.Align.CENTER);
		
		// Draw the number in the center of  the tile
		FontMetrics fm = foreground.getFontMetrics();
		// Centering in X: use alignment (and X at midpoint)
		float x = width / 2;
		// Centering in Y: measure ascent/descent first
		float y = height / 2 - (fm.ascent + fm.descent) / 2;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				canvas.drawText(this.game.getTileString(i, j), i * width + x, j * height + y, foreground);		
			}	
		}
		// Draw the hints...
		// Pick a hint color based on #moves left
		
		Paint hint = new Paint();
		int c[] = { getResources().getColor(R.color.puzzle_hint_0), getResources().getColor(R.color.puzzle_hint_1), 
				getResources().getColor(R.color.puzzle_hint_2), };
		
		Rect r = new Rect();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
			int movesleft = 9 - game.getUsedTiles(i, j).length;
			if(movesleft < c.length) {
				getRect(i, j, r);
				hint.setColor(c[movesleft]);
				canvas.drawRect(r, hint);
			}
		}
		// Draw the selection...
		Log.d(TAG, "selRect=" + selRect);
		Paint selected = new Paint();
		selected.setColor(getResources().getColor(R.color.puzzle_selected));
		canvas.drawRect(selRect, selected);
		}
	}
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event=" + event);
			switch (keyCode) {
			case KeyEvent.KEYCODE_0:
			case KeyEvent.KEYCODE_1:
			case KeyEvent.KEYCODE_2:
			case KeyEvent.KEYCODE_3:
			case KeyEvent.KEYCODE_4:
			case KeyEvent.KEYCODE_5:
			case KeyEvent.KEYCODE_6:
			case KeyEvent.KEYCODE_7:
			case KeyEvent.KEYCODE_8:
			case KeyEvent.KEYCODE_9:
			case KeyEvent.KEYCODE_ENTER:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				game.showKeypadOrError(selX, selY);
				break;				
			case KeyEvent.KEYCODE_DPAD_UP:
				select(selX, selY - 1);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				select(selX, selY + 1);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				select(selX, selY + 1);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				select(selX, selY + 1);
				break;
				default:
					return super.onKeyDown(keyCode, event);
			}
			return true;
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() != MotionEvent.ACTION_DOWN)
			return super.onTouchEvent(event);
			
			select((int) (event.getX() / width),
					(int) (event.getY() / height));
					game.showKeypadOrError(selX, selY);
			Log.d(TAG, "onTouchEvent: x " + selX + ", y " + selY);
			return true;
		}
		
		public void setSelectedTile(int tile) {
			if (game.setTileIfValid(selX, selY, tile)) {
				invalidate(); // may change hints
			} else {
				// Number is not valid for this tile
				Log.d(TAG, "setSelectedTile: invalid: " + tile);
				startAnimation(AnimationUtils.loadAnimation(game, R.anim.shake));
			}
		}
		
		private void select(int x, int y) {
			invalidate(selRect);
			selX = Math.min(Math.max(x, 0), 8);
			selY = Math.min(Math.max(y, 0), 8);
			getRect(selX, selY, selRect);
			invalidate(selRect);
		}
}