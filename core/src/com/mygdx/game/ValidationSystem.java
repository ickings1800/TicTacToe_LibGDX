package com.mygdx.game;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.Arrays;

/**
 * Created by Kanade on 9/1/2016.
 */
public class ValidationSystem extends EntitySystem{

    private ImmutableArray<Entity> entities;

    private ComponentMapper<GridPositionComponent> gpcm = ComponentMapper.getFor(GridPositionComponent.class);
    private ComponentMapper<StateComponent> scm = ComponentMapper.getFor(StateComponent.class);
    private StateComponent.State[][] boardState;
    @Override
    public void update(float deltaTime) {
        if (!Arrays.deepEquals(boardState, getCurrentState())){
            boardState = getCurrentState();
            if (gameEnd(boardState) == StateComponent.State.Draw)
                System.out.println("Draw");
            if (gameEnd(boardState) == StateComponent.State.Circle)
                System.out.println("Circle");
            if (gameEnd(boardState) == StateComponent.State.Cross)
                System.out.println("Cross");
        }
    }

    @Override
    public void addedToEngine(Engine engine) {
        // Get all pieces.
        entities = engine.getEntitiesFor(Family.all(
                GridPositionComponent.class,
                StateComponent.class
        ).get());

        boardState = getCurrentState();
    }

    public StateComponent.State gameEnd(StateComponent.State[][] state) {
        int[][][] winComb = {
                {{0, 0}, {1, 0}, {2, 0}}, // Horizontal Win
                {{0, 1}, {1, 1}, {2, 1}},
                {{0, 2}, {1, 2}, {2, 2}},

                {{0, 0}, {0, 1}, {0, 2}}, // Vertical Win
                {{1, 0}, {1, 1}, {1, 2}},
                {{2, 0}, {2, 1}, {2, 2}},

                {{0, 0}, {1, 1}, {2, 2}}, // Diagonal Win
                {{0, 2}, {1, 1}, {2, 0}}
        };

        StateComponent.State[] win = new StateComponent.State[3];

        for (int i = 0 ; i < 8 ; i++){
            for (int j = 0; j < 3; j++){
                int winX = winComb[i][j][0];
                int winY = winComb[i][j][1];
                win[j] = state[winX][winY];
            }
            if (win[0] == win[1] && win[1] == win[2] && win[1] != StateComponent.State.None) {
                return win[1];
            }
        }

        for (int i = 0 ; i < 3 ; i++){
            for (int j = 0 ; j < 3 ; j++){
                if (state[i][j] == StateComponent.State.None)
                    return StateComponent.State.None;
            }
        }
        return StateComponent.State.Draw;
    }

    public StateComponent.State[][] getCurrentState(){
        StateComponent.State[][] currentState = new StateComponent.State[3][3];
        for (int i = 0; i < entities.size(); i++){
            Entity entity = entities.get(i);
            GridPositionComponent gridPos = gpcm.get(entity);
            StateComponent state = scm.get(entity);
            currentState[gridPos.gridX][gridPos.gridY] = state.state;
        }
        return currentState;
    }

    public int evaluate(StateComponent.State[][] state){
        if (gameEnd(state) == StateComponent.State.Circle)
            return -10;
        else if (gameEnd(state) == StateComponent.State.Cross)
            return 10;
        else if (gameEnd(state) == StateComponent.State.Draw)
            return 0;
        return -1;
    }
}
