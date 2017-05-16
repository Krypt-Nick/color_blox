package com.kurncz.rainblox;

import android.app.AlertDialog;
import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kurncz.rainblox.Helpers.Containts;
import com.kurncz.rainblox.Helpers.PreferenceHelper;
import com.kurncz.rainblox.Models.Game;
import com.kurncz.rainblox.Models.Session;
import com.kurncz.rainblox.databinding.FragmentGameBoardBinding;

import java.util.Arrays;
import java.util.List;

import static com.kurncz.rainblox.Helpers.Containts.BOTTOM_LEFT;
import static com.kurncz.rainblox.Helpers.Containts.BOTTOM_MIDDLE;
import static com.kurncz.rainblox.Helpers.Containts.BOTTOM_RIGHT;
import static com.kurncz.rainblox.Helpers.Containts.CENTER_LEFT;
import static com.kurncz.rainblox.Helpers.Containts.CENTER_MIDDLE;
import static com.kurncz.rainblox.Helpers.Containts.CENTER_RIGHT;
import static com.kurncz.rainblox.Helpers.Containts.COOL_1;
import static com.kurncz.rainblox.Helpers.Containts.COOL_2;
import static com.kurncz.rainblox.Helpers.Containts.COOL_3;
import static com.kurncz.rainblox.Helpers.Containts.COOL_4;
import static com.kurncz.rainblox.Helpers.Containts.NONE;
import static com.kurncz.rainblox.Helpers.Containts.PLAYER_ONE;
import static com.kurncz.rainblox.Helpers.Containts.PLAYER_TWO;
import static com.kurncz.rainblox.Helpers.Containts.TOP_LEFT;
import static com.kurncz.rainblox.Helpers.Containts.TOP_MIDDLE;
import static com.kurncz.rainblox.Helpers.Containts.TOP_RIGHT;
import static com.kurncz.rainblox.Helpers.Containts.WARM_1;
import static com.kurncz.rainblox.Helpers.Containts.WARM_2;
import static com.kurncz.rainblox.Helpers.Containts.WARM_3;
import static com.kurncz.rainblox.Helpers.Containts.WARM_4;
import static com.kurncz.rainblox.Helpers.Containts.WHITE;

/**
 * Created by Nick Kurncz on 12/14/16.
 */

public class GameBoardFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = GameBoardFragment.class.toString();

    //Constants For Firebase Reference Lookup
    private static final String NUM_PLAYERS = "NumPlayers";
    private static final String WHOS_TURN = "Turn";
    private static final String PLAYER_ONE_LOCKED = "PlayerOneBlockLocked";
    private static final String PLAYER_TWO_LOCKED = "PlayerTwoBlockLocked";
    private static final String BLOCK_COLORS = "BlockColors";
    private static final String IS_FINISHED = "Finished";

    //Database Connection & Listeners
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mGameRef;
    private ValueEventListener mValueEventListener;

    //Active Database Props
    private int mPlayer;
    private int mTurn;
    private List<Integer> mBlockColors;
    private boolean mIsFinished;

    //Local Items
    protected FragmentGameBoardBinding binding;
    private ImageButton[] mGameBlocks;
    protected ImageButton[] mWinningCombos[];
    protected View[] mWinLines;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fire up that database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Get Reference to root json tree
        mRootRef = mFirebaseDatabase.getReference();
        //Get Reference to our game
        mGameRef = mRootRef.child(
                PreferenceHelper.getSharedPreferenceString(
                        getActivity(),
                        PreferenceHelper.SESSION_ID,
                        null
                )
        );

        //Either Join Game Or Create New Game Reference In Database
        checkForActiveGame();

        //Set Database Listener
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Grab Game Object
                Game gameSession = dataSnapshot.getValue(Game.class);

                //Update some local vars
                mBlockColors = gameSession.getBlockColors();
                mTurn = gameSession.getTurn(); //Set local variable for future use

                //Update Game UI
                rotateArrow(gameSession.getTurn());
                unlockAll(); //Unlock blox
                lockBlock(gameSession.getPlayerOneBlockLocked()); //Lock Player One Blox
                lockBlock(gameSession.getPlayerTwoBlockLocked()); //Lock Player Two Blox
                refreshBoardColors(gameSession.getBlockColors()); //Update Board Colors
                CheckForWinner(); //Do we have a winner after those changes?
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
    }

    private void checkForActiveGame() {
        mGameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game gameSession = dataSnapshot.getValue(Game.class);
                if (gameSession == null) {
                    //Create a new game session
                    mRootRef.child(
                            PreferenceHelper.getSharedPreferenceString(
                                    getActivity(),
                                    PreferenceHelper.SESSION_ID,
                                    null
                            )
                    );

                    //Update game session data in database
                    mGameRef.child(NUM_PLAYERS).setValue(0); //Zero for update method
                    mGameRef.child(WHOS_TURN).setValue(PLAYER_ONE); //Set game to start with player one
                    mGameRef.child(PLAYER_ONE_LOCKED).setValue(NONE); //Set no P1 block locked
                    mGameRef.child(PLAYER_TWO_LOCKED).setValue(NONE); //Set no P2 block locked
                    mGameRef.child(BLOCK_COLORS).setValue(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)); //Set default values for board colors
                    mGameRef.child(IS_FINISHED).setValue(false);
                }
                updateGamePlayer(); //Update database with active players
                startGame();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private void updateGamePlayer() {
        mGameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numberOfPlayers = dataSnapshot.child(NUM_PLAYERS).getValue(Integer.class);
                switch (numberOfPlayers) {
                    case 0:
                        mGameRef.child(NUM_PLAYERS).setValue(1);
                        mPlayer = PLAYER_ONE;
                        break;
                    case 1:
                        mGameRef.child(NUM_PLAYERS).setValue(2);
                        mPlayer = PLAYER_TWO;
                        break;
                    case 2:
                        ShowAlertGoToMenu(R.string.dialog_message_game_full);
                        break;
                    default:
                        ShowAlertGoToMenu(R.string.dialog_message_game_not_found);
                }

                binding.lblWhoAreYou.setText(
                        mPlayer == PLAYER_ONE
                                ? getString(R.string.fragment_game_playing_as_warm)
                                : getString(R.string.fragment_game_playing_as_cool)
                );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game_board, container, false);
        View view = binding.getRoot();

        mGameBlocks = new ImageButton[]{
                binding.imgBtn1TopLeft,
                binding.imgBtn2TopCenter,
                binding.imgBtn3TopRight,
                binding.imgBtn4CenterLeft,
                binding.imgBtn5CenterCenter,
                binding.imgBtn6CenterRight,
                binding.imgBtn7BottomLeft,
                binding.imgBtn8BottomCenter,
                binding.imgBtn9BottomRight};

        mWinningCombos = new ImageButton[][]{
                {binding.imgBtn1TopLeft, binding.imgBtn2TopCenter, binding.imgBtn3TopRight},
                {binding.imgBtn4CenterLeft, binding.imgBtn5CenterCenter, binding.imgBtn6CenterRight},
                {binding.imgBtn7BottomLeft, binding.imgBtn8BottomCenter, binding.imgBtn9BottomRight},
                {binding.imgBtn1TopLeft, binding.imgBtn4CenterLeft, binding.imgBtn7BottomLeft},
                {binding.imgBtn2TopCenter, binding.imgBtn5CenterCenter, binding.imgBtn8BottomCenter},
                {binding.imgBtn3TopRight, binding.imgBtn6CenterRight, binding.imgBtn9BottomRight},
                {binding.imgBtn1TopLeft, binding.imgBtn5CenterCenter, binding.imgBtn9BottomRight},
                {binding.imgBtn3TopRight, binding.imgBtn5CenterCenter, binding.imgBtn7BottomLeft}
        };

        mWinLines = new View[]{
                binding.viewWinLineTopHorizontal,
                binding.viewWinLineCenterHorizontal,
                binding.viewWinLineBottomHorizontal,
                binding.viewWinLineLeftVertical,
                binding.viewWinLineCenterVertical,
                binding.viewWinLineRightVertical,
                binding.viewWinLineLeftDiagonal,
                binding.viewWinLineRightDiagonal
        };

        binding.imgBtn1TopLeft.setOnClickListener(this);
        binding.imgBtn2TopCenter.setOnClickListener(this);
        binding.imgBtn3TopRight.setOnClickListener(this);
        binding.imgBtn4CenterLeft.setOnClickListener(this);
        binding.imgBtn5CenterCenter.setOnClickListener(this);
        binding.imgBtn6CenterRight.setOnClickListener(this);
        binding.imgBtn7BottomLeft.setOnClickListener(this);
        binding.imgBtn8BottomCenter.setOnClickListener(this);
        binding.imgBtn9BottomRight.setOnClickListener(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private int GetNextColor(int colorID) {
        if (colorID == Containts.WHITE
                || colorID == WARM_4
                || colorID == Containts.COOL_4) {
            return mPlayer == PLAYER_ONE ? WARM_1 : COOL_1;
        } else if (colorID == WARM_1 || colorID == COOL_1) {
            return mPlayer == PLAYER_ONE ? WARM_2 : COOL_2;
        } else if (colorID == WARM_2 || colorID == COOL_2) {
            return mPlayer == PLAYER_ONE ? WARM_3 : COOL_3;
        } else if (colorID == WARM_3 || colorID == COOL_3) {
            return mPlayer == PLAYER_ONE ? WARM_4 : COOL_4;
        } else {
            //default return
            return WHITE;
        }
    }

    private int getColorForId(int colorId) {
        switch (colorId) {

            case WHITE:
                return R.color.md_blue_grey_100;
            case WARM_1:
                return R.color.color_warm_1;
            case WARM_2:
                return R.color.color_warm_2;
            case WARM_3:
                return R.color.color_warm_3;
            case WARM_4:
                return R.color.color_warm_4;
            case COOL_1:
                return R.color.color_cool_1;
            case COOL_2:
                return R.color.color_cool_2;
            case COOL_3:
                return R.color.color_cool_3;
            case COOL_4:
                return R.color.color_cool_4;
            default:
                return R.color.md_blue_grey_100;
        }
    }

    private void CheckForWinner() {
        for (int i = 0; i < mWinningCombos.length; i++) {
            int color1 = ((ColorDrawable) mWinningCombos[i][0].getBackground()).getColor();
            int color2 = ((ColorDrawable) mWinningCombos[i][1].getBackground()).getColor();
            int color3 = ((ColorDrawable) mWinningCombos[i][2].getBackground()).getColor();

            if (color1 != ContextCompat.getColor(this.getActivity(), R.color.md_blue_grey_100) ||
                    color2 != ContextCompat.getColor(this.getActivity(), R.color.md_blue_grey_100) ||
                    color3 != ContextCompat.getColor(this.getActivity(), R.color.md_blue_grey_100)) {

                if (color1 == color2 && color2 == color3) {
                    mIsFinished = true; // For local usage
                    mGameRef.child(IS_FINISHED).setValue(true); //Tell database that the game has ended
                    switch (i) {
                        case 0:
                            mWinLines[0].setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            mWinLines[1].setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            mWinLines[2].setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            mWinLines[3].setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            mWinLines[4].setVisibility(View.VISIBLE);
                            break;
                        case 5:
                            mWinLines[5].setVisibility(View.VISIBLE);
                            break;
                        case 6:
                            mWinLines[6].setVisibility(View.VISIBLE);
                            break;
                        case 7:
                            mWinLines[7].setVisibility(View.VISIBLE);
                            break;
                        case 8:
                            mWinLines[8].setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        }
    }

    private void ShowAlertGoToMenu(int message) {
        //Tell user what happened
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage(message)
                .setTitle(R.string.dialog_title_info)
                .setPositiveButton(R.string.dialog_button_ok, (d, w) -> goBackToMenu());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goBackToMenu() {
        mGameRef.removeEventListener(mValueEventListener);
        //Sends user back to main menu by finding the previously created fragment
        Fragment gameFragment = getFragmentManager().findFragmentByTag(GameBoardFragment.TAG);
        getFragmentManager()
                .beginTransaction()
                .remove(gameFragment)
                .commit();
        ((MainActivity) getActivity()).AnimateFragmentIn(false);
    }

    @Override
    public void onClick(View block) {
        if (mTurn == mPlayer) {
            final int boardPosition = getBoardPosition(block); //Get BOARD_LOCATION

            if (boardPosition != NONE) { //If an actual block is being changed
                //Advance Block Color
                if (mBlockColors != null) {
                    mBlockColors.set(boardPosition, GetNextColor(mBlockColors.get(boardPosition))); //Set board color
                    mGameRef.child(BLOCK_COLORS).setValue(mBlockColors); //Update Database

                    mGameRef.child(mPlayer == PLAYER_ONE ? PLAYER_ONE_LOCKED : PLAYER_TWO_LOCKED).setValue(boardPosition); //Lock Board Location

                    if (!mIsFinished) {
                        mGameRef.child(WHOS_TURN).setValue(mTurn == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE);
                    }
                }
            }
        }
    }

    private ImageButton getBoardButtonByPosition(@Containts.BoardLocations int position) {
        switch (position) {
            case TOP_LEFT:
                return mGameBlocks[0];
            case TOP_MIDDLE:
                return mGameBlocks[1];
            case TOP_RIGHT:
                return mGameBlocks[2];
            case CENTER_LEFT:
                return mGameBlocks[3];
            case CENTER_MIDDLE:
                return mGameBlocks[4];
            case CENTER_RIGHT:
                return mGameBlocks[5];
            case BOTTOM_LEFT:
                return mGameBlocks[6];
            case BOTTOM_MIDDLE:
                return mGameBlocks[7];
            case BOTTOM_RIGHT:
                return mGameBlocks[8];
            default:
                return null;
        }
    }

    private int getBoardPosition(View block) {
        switch (block.getId()) {
            case R.id.img_btn_1_top_left:
                return TOP_LEFT;
            case R.id.img_btn_2_top_center:
                return TOP_MIDDLE;
            case R.id.img_btn_3_top_right:
                return TOP_RIGHT;
            case R.id.img_btn_4_center_left:
                return CENTER_LEFT;
            case R.id.img_btn_5_center_center:
                return CENTER_MIDDLE;
            case R.id.img_btn_6_center_right:
                return CENTER_RIGHT;
            case R.id.img_btn_7_bottom_left:
                return BOTTOM_LEFT;
            case R.id.img_btn_8_bottom_center:
                return BOTTOM_MIDDLE;
            case R.id.img_btn_9_bottom_right:
                return BOTTOM_RIGHT;
            default:
                return NONE;
        }
    }

    private void unlockAll() {
        mGameBlocks[0].setEnabled(true);
        mGameBlocks[1].setEnabled(true);
        mGameBlocks[2].setEnabled(true);
        mGameBlocks[3].setEnabled(true);
        mGameBlocks[4].setEnabled(true);
        mGameBlocks[5].setEnabled(true);
        mGameBlocks[6].setEnabled(true);
        mGameBlocks[7].setEnabled(true);
        mGameBlocks[8].setEnabled(true);
        mGameBlocks[0].setImageDrawable(null);
        mGameBlocks[1].setImageDrawable(null);
        mGameBlocks[2].setImageDrawable(null);
        mGameBlocks[3].setImageDrawable(null);
        mGameBlocks[4].setImageDrawable(null);
        mGameBlocks[5].setImageDrawable(null);
        mGameBlocks[6].setImageDrawable(null);
        mGameBlocks[7].setImageDrawable(null);
        mGameBlocks[8].setImageDrawable(null);
    }

    private void startGame() {
        mGameRef.addValueEventListener(mValueEventListener);
        mGameRef.child(IS_FINISHED).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final boolean isfinished = dataSnapshot.getValue(Boolean.class);
                lockAll(isfinished);
                displayWinnerInformation(isfinished);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private void rotateArrow(int player) {
        if (player != PLAYER_ONE) {
            binding.imgTurnArrow.animate().rotation(0).setDuration(250);
        } else {
            binding.imgTurnArrow.animate().rotation(180).setDuration(250);
        }
    }

    private void lockBlock(int position) {
        final ImageButton block = getBoardButtonByPosition(position);
        if (block != null) {
            block.setEnabled(false);
            block.setImageResource(R.drawable.vector_lock_white);
        }
    }

    private void unlockBlock(int position) {
        final ImageButton block = getBoardButtonByPosition(position);
        if (block != null) {
            block.setEnabled(true);
            block.setImageDrawable(null);
        }
    }

    private void refreshBoardColors(List<Integer> blockColors) {
        mGameBlocks[0].setBackgroundResource(getColorForId(blockColors.get(0)));
        mGameBlocks[1].setBackgroundResource(getColorForId(blockColors.get(1)));
        mGameBlocks[2].setBackgroundResource(getColorForId(blockColors.get(2)));
        mGameBlocks[3].setBackgroundResource(getColorForId(blockColors.get(3)));
        mGameBlocks[4].setBackgroundResource(getColorForId(blockColors.get(4)));
        mGameBlocks[5].setBackgroundResource(getColorForId(blockColors.get(5)));
        mGameBlocks[6].setBackgroundResource(getColorForId(blockColors.get(6)));
        mGameBlocks[7].setBackgroundResource(getColorForId(blockColors.get(7)));
        mGameBlocks[8].setBackgroundResource(getColorForId(blockColors.get(8)));
    }

    private void lockAll(boolean isFinished){
        if(isFinished){
            binding.imgBtn1TopLeft.setOnClickListener(null);
            binding.imgBtn2TopCenter.setOnClickListener(null);
            binding.imgBtn3TopRight.setOnClickListener(null);
            binding.imgBtn4CenterLeft.setOnClickListener(null);
            binding.imgBtn5CenterCenter.setOnClickListener(null);
            binding.imgBtn6CenterRight.setOnClickListener(null);
            binding.imgBtn7BottomLeft.setOnClickListener(null);
            binding.imgBtn8BottomCenter.setOnClickListener(null);
            binding.imgBtn9BottomRight.setOnClickListener(null);
        }
    }

    private void displayWinnerInformation(boolean isfinished) {
        if (isfinished) {
            binding.lblWinnerFound.setVisibility(View.VISIBLE); //Display the winners message
        }
    }
}
