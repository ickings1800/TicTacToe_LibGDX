package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;


public class TicTacToe extends ApplicationAdapter {

	private Engine e;
	
	@Override
	public void create () {
        e = new Engine();

        Entity board = new Entity();
        board.add(new PositionComponent());
        board.add(new TextureComponent());

        Entity player = new Entity();
        player.add(new PlayerComponent());

        Entity playerAI = new Entity();
        playerAI.add(new PlayerComponent());
        playerAI.add(new AIComponent());

        e.addEntity(board);
        e.addEntity(player);
        e.addEntity(playerAI);

        for (int x = 0; x < 3; x++){ // 9 Pieces across the board
            for (int y = 0; y < 3; y++){
                Entity piece = new Entity();
                piece.add(new PositionComponent());
                piece.add(new TextureComponent());
                piece.add(new ClickableComponent());
                piece.add(new GridPositionComponent(x, y));
                piece.add(new StateComponent());
                e.addEntity(piece);
            }
        }

        InputSystem is = new InputSystem();
        RenderingSystem rs = new RenderingSystem();
        AISystem as = new AISystem();
        ValidationSystem vs = new ValidationSystem();

        e.addSystem(rs);
        e.addSystem(is);
        e.addSystem(vs);
        e.addSystem(as);

	}

	@Override
	public void render () {
        e.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
	}
}
