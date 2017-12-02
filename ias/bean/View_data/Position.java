package com.ias.bean.View_data;

import com.ias.utils.CommonUtil;

/**
 * Created by vector on 16/6/7.
 */
public class Position {
    float x;
    float y;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }


    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Position() {
    }


    public static boolean insideScreen(int[] position, int width, int height){
        int left = position[0];
        int right = left + width;
        int up = position[1];
        int down = up + height;
        int sx = CommonUtil.screen_x;
        int sy = CommonUtil.screen_y;
        if (left > sx || right < 0 || up > sy || down < 0)
            return false;
        return true;
    }


    @Override
    public boolean equals(Object o) {
        Position target = (Position) o;
        return this.x == target.getX() && this.y == target.getY();
    }
}
