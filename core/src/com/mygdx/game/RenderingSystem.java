package com.mygdx.game;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Kanade on 9/1/2016.
 */
public class RenderingSystem extends EntitySystem {
    private ImmutableArray<Entity> grid;
    private ImmutableArray<Entity> board;

    private ComponentMapper<PositionComponent> pcm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TextureComponent> tcm = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<ClickableComponent> ccm = ComponentMapper.getFor(ClickableComponent.class);
    private ComponentMapper<StateComponent> scm = ComponentMapper.getFor(StateComponent.class);

    private SpriteBatch sb;
    private ShapeRenderer sr;
    private Camera cam;

    private Texture crossTex;
    private Texture circleTex;
    private Texture rectTex;
    private Texture boardTex;

    @Override
    public void addedToEngine(Engine engine) {
        crossTex = new Texture("Cross.png");
        circleTex = new Texture("Circle.png");
        rectTex = new Texture("Rect.png");
        boardTex = new Texture("board.png");

        sb = new SpriteBatch();
        sr = new ShapeRenderer();

        cam = new OrthographicCamera(255,255);
        grid = engine.getEntitiesFor(Family.all(PositionComponent.class ,
                                                TextureComponent.class,
                                                ClickableComponent.class,
                                                StateComponent.class).get());

        board = engine.getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class)
                                            .exclude(ClickableComponent.class).get());

        Entity boardEntity = board.get(0); // Only one board.
        TextureComponent tex = tcm.get(boardEntity); // Board texture set only during initialisation.
        tex.t = boardTex;

        for (Entity entity : grid) {
            ClickableComponent click = ccm.get(entity);
            click.r = new Rectangle();
            click.r.setSize(50,50);
        }

        cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        cam.update();
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sr.setProjectionMatrix(cam.combined);
        sb.setProjectionMatrix(cam.combined);
        cam.update();

        sr.begin(ShapeRenderer.ShapeType.Line);
        sb.begin();

        for (int i = 0; i < board.size(); ++i){
            Entity entity = board.get(i);
            TextureComponent tex = tcm.get(entity);
            PositionComponent pos = pcm.get(entity);
            sb.draw(tex.t, pos.x, pos.y, tex.t.getWidth(), tex.t.getHeight());
        }

        for (int i = 0; i < grid.size(); ++i){
            Entity entity = grid.get(i);
            ClickableComponent click = ccm.get(entity);
            TextureComponent tex = tcm.get(entity);
            PositionComponent pos = pcm.get(entity);
            StateComponent state = scm.get(entity);
            //sr.rect(click.r.getX(), click.r.getY(), click.r.getWidth(), click.r.getHeight());

            if (state.state == StateComponent.State.Circle && tex.t != circleTex)
                tex.t = circleTex;
            if (state.state == StateComponent.State.Cross && tex.t != crossTex)
                tex.t = crossTex;
            if (state.state == StateComponent.State.None && tex.t != rectTex)
                tex.t = rectTex;

            sb.draw(tex.t, pos.x, pos.y, click.r.getWidth(), click.r.getHeight());
        }

        sr.end();
        sb.end();
    }
}
