package com.mygdx.game;

import com.badlogic.ashley.core.Component;

/**
 * Created by Kanade on 9/2/2016.
 */
public class PlayerComponent implements Component{
    public int score = 0;
    public StateComponent.State state;
    public boolean turn = false;
    public int[] move;
}
