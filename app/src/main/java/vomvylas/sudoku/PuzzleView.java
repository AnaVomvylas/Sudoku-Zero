package vomvylas.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

// to view tou Game.java
public class PuzzleView extends View{
    private final Game game;

    // setting variables
    private float width; //of each tile
    private float height; //of each tile
    private float room; //the distance between the title bar and the board
    private int selX; // 0 to 8 of the selected tile
    private int selY; // 0 to 8 of the selected tile
    private final Rect selRect = new Rect(); // the selected rectangle
    private float x; //offset for the numbers paint
    private float y;
    private static final int ID = View.generateViewId();
    // Paints for the draw class
    Paint background = new Paint();
    Paint minor = new Paint();
    Paint major = new Paint();
    Paint hintlines = new Paint();
    Paint numbers = new Paint();
    Paint current_numbers = new Paint();
    Paint selected = new Paint();
    Paint hint1 = new Paint();
    boolean hint_paint = false; // if hint 1 is selected
    Paint hints_small = new Paint();
    Paint hints = new Paint();
    Paint notes = new Paint();
    Paint submit = new Paint();

    // constructor
    public PuzzleView(Context context) {
        super(context); // initialize View first
        this.game = (Game) context; // pernaei to context sto game, pou einai apo to Game.java class
        setFocusable(true);
        setFocusableInTouchMode(true);
        setId(ID);
        InitPaints();
    }

    // Puts the selRect on the first empty cell of the dug grid
    private void InitSelRect(){
        outerloop:
        for (int i=0; i<9; i++){
            for (int j =0; j<9; j++){
                if (game.getDugGrid()[j][i] == 0) {
                    selX = j;
                    selY = i;
                    break outerloop;
                }
            }
        }
        setRect(selX,selY,selRect);
    }

    //Initializes the Paint parameters
    private void InitPaints(){
        //Background
        background.setColor(getResources().getColor(R.color.background));

        //Lines
        minor.setColor(getResources().getColor(R.color.minor_lines));
        minor.setStrokeWidth(3);
        major.setColor(getResources().getColor(R.color.major_lines));
        major.setStrokeWidth(9);
        hintlines.setColor(getResources().getColor(R.color.hints_small));
        hintlines.setStrokeWidth(3);


        /*hilite.setColor(getResources().getColor(R.color.hilite));
        hilite.setAlpha(40);*/

        //Numbers
        current_numbers.setAntiAlias(true);// makes them smooth
        current_numbers.setColor(getResources().getColor(R.color.current_numbers));
        current_numbers.setStyle(Paint.Style.FILL);
        current_numbers.setAlpha(100);

        numbers.setAntiAlias(true);
        numbers.setColor(getResources().getColor(R.color.numbers));
        numbers.setStyle(Paint.Style.FILL);

        //Notes
        notes.setAntiAlias(true);// makes them smooth
        notes.setColor(getResources().getColor(R.color.current_numbers));
        notes.setStyle(Paint.Style.FILL);
        notes.setAlpha(70);

        //Hints
        hints_small.setAntiAlias(true);
        hints_small.setColor(getResources().getColor(R.color.hints_small));
        hints_small.setStyle(Paint.Style.FILL);

        hints.setAntiAlias(true);
        hints.setColor(getResources().getColor(R.color.hints));
        hints.setStyle(Paint.Style.FILL);
        hints.setAlpha(30);

        //Submit
        submit.setAntiAlias(true);
        submit.setColor(getResources().getColor(R.color.submit));
        submit.setStyle(Paint.Style.FILL);

        //Selected Rectangle
        selected.setColor(getResources().getColor(R.color.selected));
        selected.setAlpha(30);

        //selected rectangle hunt
        hint1.setColor(getResources().getColor(R.color.hint1));
        hint1.setAlpha(30);
    }

    @Override
    //onSizeChanged is called after android knows the size of the view #tou drawable screen isws
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        width = w / 9f;
        height = width;
        room =(float) (width*1.5);
        InitSelRect();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //Updates Rect based on selX, selY
    private void setRect(int x, int y, Rect rect) {
        rect.set((int) (x * width), (int) (y * height+room), (int) (x * width + width), (int) (y * height + height+room));
        invalidate(rect);
    }

    //1st hint implementation
    private void showNextNumber(){

        int cellNumb = game.getRandomEmptyCell(game.getCurrentGrid());
        if (cellNumb >= 0) { // If there is an empty cell
            selX = cellNumb / 9;
            selY = cellNumb - (selX * 9);
            hint_paint = true;
            setRect(selX, selY, selRect);
            setSelectedTile(game.getSolvedGrid()[selX][selY]);
        }
    }

    //2nd hint implementation
    private void checkForMistakes(){
        int mistakes = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if ((game.getCurrentGrid()[i][j] != 0) && (game.getCurrentGrid()[i][j] != game.getSolvedGrid()[i][j] ))
                    mistakes += 1;
            }
        }
        Context context = getContext();
        CharSequence text = "You have made " + Integer.toString(mistakes) + " mistakes";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context,text, duration);
        //toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    //Submit button
    private void submitPuzzle(){
        boolean isComplete = true;
        outerloop:
        for (int i=0; i<9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.getCurrentGrid()[i][j] == 0) { //check if the puzzle is unfinished / contains zeros
                    isComplete = false;
                    break outerloop;
                }
            }
        }
        if (!isComplete) // if the puzzle is unfinished
            Toast.makeText(getContext(),R.string.submit_unfinished_toast,Toast.LENGTH_SHORT).show();
        else if(game.compareGrids(game.getCurrentGrid(), game.getSolvedGrid())) { // if puzzle is finished and solved
            Log.d(Sudoku.TAG,"completed and solved, boolean iscomplete = " + isComplete);
            game.submitDialog();
        }
        else //if puzzle is finished and NOT solved
            Toast.makeText(getContext(),R.string.submit_fail_toast,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { //to performclick einai gia tous blind otan xrhsimopoioun talkback. Den tha paizoun sudoku...
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return super.onTouchEvent(event);

        // tapped X and Y, quantized based on cell width
        int tapX =(int) (event.getX() / width);
        int tapY = (int) ((event.getY() -room) / height);



        if((event.getY() > room) && (event.getY() < getWidth()+room)){ // an einai mesa sta oria tou grid mou
            if (game.getDugGrid()[tapX][tapY] != 0 )  // an pathse ena apo ta SET numbers, do nothing
                return super.onTouchEvent(event);
            selX = tapX;
            selY = tapY;
            setRect(selX, selY,selRect);
           // game.showKeypadOrError();
        }
        else if (event.getY() < room) // an einai panw apo to grid
            if ((event.getX() < 2*width) && (event.getX() > width) ) //1st hint
                showNextNumber();
            else if ((event.getX() < 5*width) && (event.getX() > 4*width)) //2nd hint
                checkForMistakes();
            else if ((event.getX() > 6.8f*width) && (event.getX() < 8.5f*width)) // Submit
                submitPuzzle();
            else
                return super.onTouchEvent(event);
        else if ((event.getY() > getHeight()-2*y) && (event.getY() < getHeight()- 0.6*y))//an einai sta noumera katw
            setSelectedTile(tapX+1);
        else if ((event.getY() > getWidth()+ room )&& (event.getY() < getHeight()- 2*y) && (event.getX() > 3.5f*width) && (event.getX() < 5.5f*width) ) //an einai sto erase
            setSelectedTile(0);
        else
            return super.onTouchEvent(event);

        return true;
    }

    // draws the selected number on the selected tile
    public void setSelectedTile(int number){
        game.setTile(selX,selY,number);
        invalidate(selRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the background
        //canvas.drawRect(0,0, getWidth(),getHeight(), background); //left,top,right,bottom

        //drawing minor lines
        for (int i = 0; i < 9; i++) {
            //draw everything in minor lines, then draw above them with major
            canvas.drawLine(i * width, room, i * width, getWidth()+room, minor); //vertical lines
            canvas.drawLine(0, i * height +room, getWidth(), i * height +room, minor); //horizontal lines
            //highliting
            /*canvas.drawLine(i * width - hilite_offset, 0, i * width - hilite_offset, getHeight(), hilite);
            canvas.drawLine(i * width + hilite_offset, 0, i * width + hilite_offset, getHeight(), hilite);
            canvas.drawLine(0, i * height - hilite_offset, getWidth(), i * height - hilite_offset, hilite);
            canvas.drawLine(0, i * height + hilite_offset, getWidth(), i * height + hilite_offset, hilite);*/
        }

        //drawing major lines
        for (int i = -1; i < 10; i++) {
            if (i % 3 != 0)
                continue;
            canvas.drawLine(i * width, room, i * width, height*9f+ room, major); //vertical lines
            canvas.drawLine(0, i*height +room, getWidth(), i * height +room, major); //horizontal lines
        }

        //Font Metrics of numbers and hints
        current_numbers.setTextSize(height * 0.75f);
        current_numbers.setTextScaleX(width / height);
        current_numbers.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = current_numbers.getFontMetrics();

        numbers.setTextSize(height * 0.75f);
        numbers.setTextScaleX(width / height);
        numbers.setTextAlign(Paint.Align.CENTER);
        //numbers.getFontMetrics(fm);

        hints_small.setTextSize(height/3f);
        hints_small.setTextAlign(Paint.Align.CENTER);

        hints.setTextSize(height);
        hints.setTextAlign(Paint.Align.CENTER);

        submit.setTextSize(height/2f);
        submit.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fmHints = hints_small.getFontMetrics();


        x = width / 2f;
        y = height / 2f - (fm.ascent + fm.descent) / 2f; // wste na ksekinaei apo to midpoint tou arithmou, kai oxi apo to baseline tou

        //draw the SET numbers
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (game.getDugGrid()[i][j] != 0)
                    canvas.drawText(this.game.getTileString(i,j,game.getDugGrid()), i * width + x, j *height + room +y, numbers);
            }
        }
        //Draw the Current_grid numbers
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if ((game.getCurrentGrid()[i][j] != 0) && (game.getCurrentGrid()[i][j] != game.getDugGrid()[i][j]))
                    canvas.drawText(this.game.getTileString(i,j,game.getCurrentGrid()), i * width + x, j * height+ room+y, current_numbers);
            }
        }

        //Draw the Top bars (Hints, Check for completed)
        canvas.drawText(getResources().getString(R.string.hint1),width + x,room/2f - (fmHints.ascent + fmHints.descent) / 2f + fmHints.top  ,hints_small);
        canvas.drawText(getResources().getString(R.string.hint2),width + x,room/2f - (fmHints.ascent + fmHints.descent) / 2f,hints_small); //centered
        canvas.drawText(getResources().getString(R.string.hint3),width + x,room/2f - (fmHints.ascent + fmHints.descent) / 2f - fmHints.top ,hints_small);

        canvas.drawText(getResources().getString(R.string.hint4),4*width + x,room/2f - (fmHints.ascent + fmHints.descent) / 2f + fmHints.top  ,hints_small);
        canvas.drawText(getResources().getString(R.string.hint5),4*width + x,room/2f - (fmHints.ascent + fmHints.descent) / 2f,hints_small); //centered
        canvas.drawText(getResources().getString(R.string.hint6),4*width + x,room/2f - (fmHints.ascent + fmHints.descent) / 2f - fmHints.top ,hints_small);

        //canvas.drawText(getResources().getString(R.string.hints),3*width,height/2f,hints);

        //White square containing Hints
        canvas.drawLine(x,height/5.5f,5*width+x,height/5f,hintlines);
        canvas.drawLine(x,room -height/5.5f,5*width+x,room - height/5f,hintlines);
        canvas.drawLine(x,height/5.5f,x,room - height/5f,hintlines);
        canvas.drawLine(5*width + x,height/5.5f,5*width+x,room - height/5f,hintlines);

        //Draw Submit
        canvas.drawText(getResources().getString(R.string.submit),7*width + x,room/2f - (fmHints.ascent + fmHints.descent) / 2f  , submit);

        //canvas.drawRect(x,height*0.32f, 5*width+x, room -height*-0.32f,hints );



        //Draw the SELECTION NUMBERS and Clear below
        for (int i = 0; i < 9; i++) {
            canvas.drawText(String.valueOf(i+1),i*width+x,getHeight()-y, current_numbers);
        }
        //draw Erase
        canvas.drawText("Erase",4*width+x,getWidth() +room +y*1.2f, current_numbers);

        //canvas.drawText("test",4*width+x, getHeight(), numbers);
        /*canvas.drawLine(0,(getHeight()- height-y),getWidth(),(getHeight()- height-y),major);
        canvas.drawLine(0,(getHeight()- height),getWidth(),(getHeight()- height),major);
        canvas.drawLine(0,(getHeight()- y),getWidth(),(getHeight()- y),minor);
        canvas.drawLine(0,(getHeight()-2*y),getWidth(),(getHeight()-2*y),minor);*/

        //Draw the Selection Rectangle
        if (!hint_paint)
            canvas.drawRect(selRect, selected);
        else {
            canvas.drawRect(selRect, hint1);
            hint_paint = false;
        }

    }


}

