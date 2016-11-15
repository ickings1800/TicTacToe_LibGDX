package com.mygdx.game;

import com.badlogic.ashley.core.Component;

/**
 * Created by Kanade on 9/1/2016.
 */
public class GridPositionComponent implements Component {
    public int gridX;
    public int gridY;

    public GridPositionComponent(int gridX, int gridY){
        this.gridX = gridX;
        this.gridY = gridY;
    }
}
