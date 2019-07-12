package com.bignerdranch.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";

    private static final String KEY_IS_ANS_SHOWN = "is_ans_shown";

    private boolean mAnswerIsTrue;
    private boolean mIsAnswerShown;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mAPILevelTextView;

    //Public Static Methods
    //Creating new intent with extra about answer
    public static Intent newIntent(Context packageContext, boolean answerIsTrue){
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return  intent;
    }

    //Get data about cheating from intent
    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false); //false will be default value
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        //Is current answer true
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        //Inflate answer button and text view
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mAPILevelTextView = (TextView) findViewById(R.id.api_level);

        //Show API Level
        mAPILevelTextView.setText("API Level " + Build.VERSION.SDK_INT);

        //Check if answer already shown
        if(savedInstanceState != null){
            mIsAnswerShown = savedInstanceState.getBoolean(KEY_IS_ANS_SHOWN, false);
            if(mIsAnswerShown){
                showAnswer();
                mShowAnswerButton.setVisibility(View.INVISIBLE);    //Hide Show Answer button
            }
        }

        //Set onClick Listener on Show Answer button
        mShowAnswerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mIsAnswerShown = true;
                showAnswer();

                //Hide button on press - Animation
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                }//If API level to old for animation, just hide the button
                else{
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    private void setAnswerShownResult(boolean isAnswerShown){
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(KEY_IS_ANS_SHOWN, mIsAnswerShown);
    }

    private void showAnswer(){
        if(mAnswerIsTrue){
            mAnswerTextView.setText(R.string.true_button);
        }
        else{
            mAnswerTextView.setText(R.string.false_button);
        }
        setAnswerShownResult(true);
    }
}
