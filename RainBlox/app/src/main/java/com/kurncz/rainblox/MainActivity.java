package com.kurncz.rainblox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.AnimatorRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kurncz.rainblox.Helpers.PreferenceHelper;
import com.kurncz.rainblox.Models.Game;
import com.kurncz.rainblox.databinding.ActivityStartBinding;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.kurncz.rainblox.Helpers.Containts.PLAYER_ONE;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.toString();
    private static final String KEY_IS_ACTIVE = "IsActive";

    private ActivityStartBinding mBindings;
    private boolean mIsGameActive;
    private List<Integer> countOfCoolWins;
    private List<Integer> countOfWarmWins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
        {
            mIsGameActive = savedInstanceState.getBoolean(KEY_IS_ACTIVE,false);
        }

        //Load Main Menu
        mBindings = DataBindingUtil.setContentView(this, R.layout.activity_start);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();
        countOfCoolWins = new ArrayList<>();
        countOfWarmWins = new ArrayList<>();
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                countOfCoolWins.clear();
                countOfWarmWins.clear();
                for (DataSnapshot game : dataSnapshot.getChildren()){
                    Game gameData = game.getValue(Game.class);
                    int value = gameData.getTurn();
                    if (value == PLAYER_ONE){
                        countOfWarmWins.add(value);
                    } else {
                        countOfCoolWins.add(value);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

        if(mIsGameActive)
        {
            mBindings.btnStartGame.setVisibility(View.GONE);
            mBindings.btnLeaderBoards.setVisibility(View.GONE);
            mBindings.imgLogo.setVisibility(View.GONE);
            mBindings.lblLogoText.setVisibility(View.GONE);
            AnimateFragmentIn(true);
        }

        //Create a click listener to start the game
        mBindings.btnStartGame.setOnClickListener((v)->ShowSessionDialog());

        //Show Leaderboard Dialog
        mBindings.btnLeaderBoards.setOnClickListener(v -> {
            String xwins = "Warm Colors Won: " + countOfWarmWins.size();
            String owins = "Cool Colors Won: " + countOfCoolWins.size();

            View view = getLayoutInflater().inflate(R.layout.dialog_leaderboards, null);
            ((TextView)view.findViewById(R.id.lbl_x_wins)).setText(xwins);
            ((TextView)view.findViewById(R.id.lbl_o_wins)).setText(owins);


            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setView(view);
            dialog.setTitle(R.string.login_dialog_title_leaderboards);
            dialog.setNegativeButton("OK", (dialog1, which) -> {});
            dialog.show();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_IS_ACTIVE, mIsGameActive);
        super.onSaveInstanceState(outState);
    }

    private Fragment GetGameFragment() {
        Fragment gameFragment = getFragmentManager().findFragmentByTag(GameBoardFragment.TAG);
        if (gameFragment == null) {
            //If Not Create A New One
            gameFragment = new GameBoardFragment();
        }
        return gameFragment;
    }

    public void AnimateFragmentIn(boolean isopening) {
        if (isopening) {
            mBindings.imgLogo.animate().rotation(-20f).alpha(0).translationX(-100).translationY(-100).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBindings.imgLogo.setVisibility(View.GONE);
                }
            });
            mBindings.lblLogoText.animate().rotation(-20f).alpha(0).translationX(-100).translationY(-100).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBindings.lblLogoText.setVisibility(View.GONE);
                }
            });
            mBindings.btnStartGame.bringToFront();
            mBindings.btnStartGame.animate().translationY(400).setDuration(300).setStartDelay(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBindings.btnStartGame.setVisibility(View.GONE);
                }
            });
            mBindings.btnLeaderBoards.animate().translationY(400).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBindings.btnLeaderBoards.setVisibility(View.GONE);
                }
            });;
        } else {
            mBindings.imgLogo.animate().rotation(0f).alpha(1).translationX(0).translationY(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mBindings.imgLogo.setVisibility(View.VISIBLE);
                }
            });
            mBindings.lblLogoText.animate().rotation(0f).alpha(1).translationX(0).translationY(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mBindings.lblLogoText.setVisibility(View.VISIBLE);
                }
            });
            mBindings.btnStartGame.bringToFront();
            mBindings.btnLeaderBoards.animate().translationY(0).setDuration(300).setStartDelay(250).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBindings.btnLeaderBoards.setVisibility(View.VISIBLE);
                }
            });;
            mBindings.btnStartGame.animate().translationY(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationEnd(animation);
                    mBindings.btnStartGame.setVisibility(View.VISIBLE);
                }
            });;
        }
    }

    private void ShowSessionDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_session_dialog);
        dialog.setTitle(R.string.dialog_title_connect_to_session);

        // Get dialog elements
        TextView sessionId = (TextView) dialog.findViewById(R.id.txt_session_id);
        //Setup Join Button
        Button join = (Button) dialog.findViewById(R.id.btn_join);

        join.setOnClickListener((v)-> {
            if (!TextUtils.isEmpty(sessionId.getText().toString())){
                SetSessionID(sessionId.getText().toString());
                dialog.dismiss();
            } else {
                sessionId.setError(getString(R.string.dialog_error_not_null));
            }
        });

        //Setup Cancel
        Button cancel = (Button)dialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener((v) -> dialog.dismiss());

        dialog.show();
    }

    private void SetSessionID (String sessionID){
        //Set Preference For Ref If Needed
        PreferenceHelper.setSharedPreferenceString(
                this,
                PreferenceHelper.SESSION_ID,
                "Game" + sessionID
        );

        //Proceed to setup game
        SetupGame();
    }

    private void SetupGame(){
        mIsGameActive = true;
        //Animate the buttons moving away and display game board
        AnimateFragmentIn(true);

        Handler handler = new Handler();
        Runnable r = () -> getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_content,
                        GetGameFragment(),
                        GameBoardFragment.TAG)
                .commit();
        handler.postDelayed(r, 350);
    }

    @Override
    public void onBackPressed() {
        if(GetGameFragment().isVisible())
        {
            getFragmentManager()
                    .beginTransaction()
                    .remove(GetGameFragment())
                    .commit();
            AnimateFragmentIn(false);
        } else {
            super.onBackPressed();
        }
    }
}