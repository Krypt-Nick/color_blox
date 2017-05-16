package com.kurncz.rainblox.Helpers;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 4/30/17.
 */

public class Containts {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PLAYER_ONE,
            PLAYER_TWO
    })
    public @interface Players{}
    public static final int PLAYER_ONE = 0;
    public static final int PLAYER_TWO = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            TOP_LEFT,
            TOP_MIDDLE,
            TOP_RIGHT,
            CENTER_LEFT,
            CENTER_MIDDLE,
            CENTER_RIGHT,
            BOTTOM_LEFT,
            BOTTOM_MIDDLE,
            BOTTOM_RIGHT,
            NONE
    })
    public @interface BoardLocations{}
    public static final int TOP_LEFT = 0;
    public static final int TOP_MIDDLE = 1;
    public static final int TOP_RIGHT = 2;
    public static final int CENTER_LEFT = 3;
    public static final int CENTER_MIDDLE = 4;
    public static final int CENTER_RIGHT = 5;
    public static final int BOTTOM_LEFT = 6;
    public static final int BOTTOM_MIDDLE = 7;
    public static final int BOTTOM_RIGHT = 8;
    public static final int NONE = 9;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            WHITE,
            WARM_1,
            WARM_2,
            WARM_3,
            WARM_4,
            COOL_1,
            COOL_2,
            COOL_3,
            COOL_4
    })

    public @interface Colors{}
    public static final int WHITE = 0;
    public static final int WARM_1 = 1;
    public static final int WARM_2 = 2;
    public static final int WARM_3 = 3;
    public static final int WARM_4 = 4;
    public static final int COOL_1 = 5;
    public static final int COOL_2 = 6;
    public static final int COOL_3 = 7;
    public static final int COOL_4 = 8;
}
