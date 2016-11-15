package com.mygdx.game;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;


/**
 * Created by Kanade on 9/1/2016.
 */
public class InputSystem extends EntitySystem {
    private ImmutableArray<Entity> cellEntities;
    private ImmutableArray<Entity> playerEntities;

    private PlayerComponent player1;
    private PlayerComponent player2;

    private EntitySystem vs;
    private Vector2 mousePos;

    @Override
    public void addedToEngine(Engine engine) {
        mousePos = new Vector2();
        cellEntities = engine.getEntitiesFor(Family.all(
                GridPositionComponent.class,
                PositionComponent.class,
                ClickableComponent.class,
                TextureComponent.class
        ).get());

        vs = engine.getSystem(ValidationSystem.class);

        // Set up collision rects

        for (int i = 0; i < cellEntities.size(); ++i) {
            Entity e = cellEntities.get(i);
            ClickableComponent click = e.getComponent(ClickableComponent.class);
            GridPositionComponent gPos = e.getComponent(GridPositionComponent.class);
            PositionComponent pos = e.getComponent(PositionComponent.class);
            // 8px is offset.
            // 45px * index position of grid gives the relative position.
            pos.x = 8 + (gPos.gridX * click.r.getWidth()) + (gPos.gridX * 45);
            pos.y = 8 + (gPos.gridY * click.r.getHeight()) + (gPos.gridY * 45);
            click.r.setPosition(pos.x, pos.y);
        }

        // Set up players
        playerEntities = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());

        player1 = playerEntities.get(0).getComponent(PlayerComponent.class);
        player2 = playerEntities.get(1).getComponent(PlayerComponent.class);

        player1.state = StateComponent.State.Circle; // Player is circle.
        player2.state = StateComponent.State.Cross;
        player1.turn = true;
        player2.turn = false;
    }

    @Override
    public void update(float deltaTime) {
        // Respond to mouses clicks.
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            mousePos.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            for (int i = 0; i < cellEntities.size(); ++i) {
                Entity cell = cellEntities.get(i);
                StateComponent cellState = cell.getComponent(StateComponent.class);
                ClickableComponent click = cell.getComponent(ClickableComponent.class);
                GridPositionComponent gPos = cell.getComponent(GridPositionComponent.class);

                if (click.r.contains(mousePos) && cellState.state == StateComponent.State.None) {
                    if (player1.turn)
                        player1.move = new int[]{gPos.gridX, gPos.gridY};
                    else
                        player2.move = new int[]{gPos.gridX, gPos.gridY};
//                    System.out.print(gPos.gridX + " ");
//                    System.out.println(gPos.gridY);
                }
            }
        }

        // Set cell states
        if (player1.move != null) {
            for (int i = 0 ; i < cellEntities.size(); ++i) {
                Entity cell = cellEntities.get(i);
                StateComponent cellState = cell.getComponent(StateComponent.class);
                GridPositionComponent gPos = cell.getComponent(GridPositionComponent.class);
                if (gPos.gridX == player1.move[0] && gPos.gridY == player1.move[1]) {
                    cellState.state = player1.state;
                    player1.move = null;
                    player1.turn = false;
                    player2.turn = true;
                    break;
                }
            }
        } else if (player2.move != null) {
            for (int i = 0 ; i < cellEntities.size(); ++i) {
                Entity cell = cellEntities.get(i);
                StateComponent cellState = cell.getComponent(StateComponent.class);
                GridPositionComponent gPos = cell.getComponent(GridPositionComponent.class);
                if (gPos.gridX == player2.move[0] && gPos.gridY == player2.move[1]) {
                    cellState.state = player2.state;
                    player2.move = null;
                    player2.turn = false;
                    player1.turn = true;
                    break;
                }
            }
        }
    }
}
