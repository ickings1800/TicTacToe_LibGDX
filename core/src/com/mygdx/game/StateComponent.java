package com.mygdx.game;

import com.badlogic.ashley.core.Component;

/**
 * Created by Kanade on 9/1/2016.
 */
public class StateComponent implements Component {
    public enum State  { None, Cross, Circle, Draw}
    public State state = State.None;

}
