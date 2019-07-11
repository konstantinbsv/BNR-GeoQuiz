package com.bignerdranch.android.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.view.View;
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

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        if(savedInstanceState != null){
            mIsAnswerShown = savedInstanceState.getBoolean(KEY_IS_ANS_SHOWN, false);
            if(mIsAnswerShown){
                showAnswer();
            }
        }

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mIsAnswerShown = true;
                showAnswer();
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
