package com.bignerdranch.android.geoquiz;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    //Bundle save keys
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANS_CORRECT = "answered_correct";
    private static final String KEY_ANS_INCORRECT = "answered_incorrect";
    private static final String KEY_CHEATED_ON = "cheated_on";
    private static final String KEY_ALREADY_ANSWERED = "already_answered";

    private static final int REQUEST_CODE_CHEAT = 0;

    private static final int ALLOWED_CHEATS = 3;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mCheatsRemaining;

    private Question[] mQuestionBank = new Question[]{
        new Question(R.string.question_australia, true),
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private int mAnsweredCorrect = 0;
    private int mAnsweredIncorrect = 0;
    private boolean[] mAlreadyAnswered = new boolean[mQuestionBank.length];
    private boolean[] mCheatedOn = new boolean[mQuestionBank.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        //If saved instance exists get values from it
        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnsweredCorrect = savedInstanceState.getInt(KEY_ANS_CORRECT, 0);
            mAnsweredIncorrect = savedInstanceState.getInt(KEY_ANS_INCORRECT, 0);
            mAlreadyAnswered = savedInstanceState.getBooleanArray(KEY_ALREADY_ANSWERED);
            mCheatedOn = savedInstanceState.getBooleanArray(KEY_CHEATED_ON);
        }

        mCheatsRemaining = (TextView) findViewById(R.id.cheats_remaining);
        updateCheatsRemaining();

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();

        //Challenge 1 - Click on text for next question
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(false);
            }
        });

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mPrevButton = findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + mQuestionBank.length -1) % mQuestionBank.length;
                updateQuestion();
            }
        });


        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Start CheatActivity
                if(mAlreadyAnswered[mCurrentIndex]){
                    Toast.makeText(QuizActivity.this, R.string.already_answered, Toast.LENGTH_SHORT).show();
                }
                else if(cheatsRemaining() == 0){
                    Toast.makeText(QuizActivity.this, R.string.out_of_cheats, Toast.LENGTH_SHORT).show();
                }
                else {
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);   //call superclass method. Mandatory
        Log.i(TAG, "onSaveInstanceState");          //.i priority constant INFO
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putInt(KEY_ANS_CORRECT, mAnsweredCorrect);
        savedInstanceState.putInt(KEY_ANS_INCORRECT, mAnsweredIncorrect);
        savedInstanceState.putBooleanArray(KEY_ALREADY_ANSWERED, mAlreadyAnswered);
        savedInstanceState.putBooleanArray(KEY_CHEATED_ON, mCheatedOn);
    }

    @Override public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion(){
        // Log.d(TAG, "Updating question text", new Exception()); //Page 78
        mIsCheater = false; //reset cheating on question change (update)
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue){
        if(mCheatedOn[mCurrentIndex]){
            Toast.makeText(QuizActivity.this, R.string.judgment_toast, Toast.LENGTH_LONG).show();
        }
        else if(!mAlreadyAnswered[mCurrentIndex]) {
            if (userPressedTrue == mQuestionBank[mCurrentIndex].isAnswerTrue()) {
                Toast toast = Toast.makeText(QuizActivity.this, R.string.correct_toast, Toast.LENGTH_SHORT);
                // toast.setGravity(Gravity.TOP, 0, 0); //Ch 1 - Challenge 1
                toast.show();
                mAnsweredCorrect ++;
            } else {
                Toast.makeText(QuizActivity.this, R.string.incorrect_toast, Toast.LENGTH_SHORT).show();
                mAnsweredIncorrect ++;
            }
            mAlreadyAnswered[mCurrentIndex] = true;
        }
        else{
            Toast.makeText(QuizActivity.this, R.string.already_answered, Toast.LENGTH_SHORT).show();
        }
        if(mAnsweredCorrect + mAnsweredIncorrect == mQuestionBank.length){
            float mPercentCorrect = (float) mAnsweredCorrect / mQuestionBank.length * 100;
            String mScore = getResources().getString(R.string.quiz_result) + String.format(Locale.getDefault(), " %.2f%%", mPercentCorrect);
            Toast.makeText(QuizActivity.this, mScore, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){
            if (data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);

            //If cheated on question
            if(mIsCheater){
                mCheatedOn[mCurrentIndex] = true;           //Remember question that was cheated on
                mAlreadyAnswered[mCurrentIndex] = true;     //Set question as answered
                mAnsweredIncorrect ++;                      //Increment incorrect answers
                updateCheatsRemaining();                    //Update cheats remaining
            }
        }
    }

    private int cheatsRemaining(){
        int cheatsRemaining = ALLOWED_CHEATS;
        for (boolean b: mCheatedOn){
            if (b){
                cheatsRemaining--;
            }
            if(cheatsRemaining == 0){
                break;
            }
        }
        return cheatsRemaining;
    }

    private void updateCheatsRemaining(){
        int cheatsRemaining = cheatsRemaining();
        String cheatsRemainingAlert = getText(R.string.cheats_remaining) + " " + Integer.toString(cheatsRemaining);
        mCheatsRemaining.setText(cheatsRemainingAlert);
    }
}
