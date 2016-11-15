package com.mygdx.game;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Kanade on 9/3/2016.
 */
public class AISystem extends EntitySystem {
    private ValidationSystem vs;
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);
    private ImmutableArray<Entity> aiPlayers;

    @Override
    public void addedToEngine(Engine engine) { // Pass
        vs = engine.getSystem(ValidationSystem.class);
        aiPlayers = engine.getEntitiesFor(Family.all(PlayerComponent.class, AIComponent.class).get());
    }

    @Override
    public void update(float deltaTime) { // Pass
        for (int i = 0; i < aiPlayers.size(); i++){
            PlayerComponent ai = pcm.get(aiPlayers.get(i));
            StateComponent.State[][] currentState = vs.getCurrentState();
            if (ai.turn && vs.gameEnd(currentState) == StateComponent.State.None) {
                ai.move = findBestMove(currentState, ai.state);
            }
        }
    }

    private ArrayList<int[]> getAvailableMoves(StateComponent.State[][] state){ // Pass
        ArrayList movesAvail = new ArrayList();
        StateComponent.State end = vs.gameEnd(state);
        if (end == StateComponent.State.Circle || end == StateComponent.State.Cross || end == StateComponent.State.Draw)
            return movesAvail;

        for (int i = 0; i < 3 ; i++){
            for (int j = 0; j < 3; j++){
                if (state[i][j] == StateComponent.State.None) {
                    int[] child = {i,j};
                    movesAvail.add(child);
                }
            }
        }
        return  movesAvail;
    }

    private int[] findBestMove(StateComponent.State[][] state, StateComponent.State player){
        ArrayList<int[]> movesAvail = getAvailableMoves(state); // First turn is size 8.
        int[][] scores = new int[movesAvail.size()][3];
        //System.out.println(Arrays.deepToString(state)); // Pass, prints out given state.

        for (int i = 0 ; i < movesAvail.size(); i++){
            StateComponent.State[][] child = state.clone();
            //System.out.println(Arrays.deepToString(child)); // Pass, duplicate children 8 times.

            child[movesAvail.get(i)[0]][movesAvail.get(i)[1]] = player;
            //System.out.println(Arrays.deepToString(child)); // Pass, all children is being assigned AI player once.

            int currentScore;
            if (player == StateComponent.State.Cross) {
                currentScore = min(child);
            }
            else {
                currentScore = max(child);
            }
            scores[i][0] = currentScore; // Pass, current score is assigned to index 0.
            scores[i][1] = movesAvail.get(i)[0]; // Pass, current moveX is assigned to index 1.
            scores[i][2] = movesAvail.get(i)[1]; // Pass, current moveY is assigned to index 2.
            child[movesAvail.get(i)[0]][movesAvail.get(i)[1]] = StateComponent.State.None; // Pass, Redundant, just in case.
        }
        System.out.println(Arrays.deepToString(scores)); // Pass, scores are printed correctly.
        if (player == StateComponent.State.Cross) {
            System.out.print("Best Move Max: ");
            System.out.print(bestMoveMax(scores)[0]);
            System.out.println(bestMoveMax(scores)[1]);
            return bestMoveMax(scores); // Pass, selects first best (max) scoring move.
        }
        else {
            System.out.print("Best Move Min: ");
            System.out.print(bestMoveMin(scores)[0]);
            System.out.println(bestMoveMin(scores)[1]);
            return bestMoveMin(scores); // BestMoveMin is not executed as AI is only Cross.
        }
    }

    private int min(StateComponent.State[][] state){
        //System.out.println("MIN : " + Arrays.deepToString(state));
        int winScore = vs.evaluate(state); // Pass, evaluation is correct.

        if (winScore != -1)
            return winScore;

        ArrayList<int[]> moves = getAvailableMoves(state); // Pass, GetAvailableMoves is good.
        int bestScore = Integer.MAX_VALUE;
        int currentScore;

        for(int i = 0; i < moves.size(); i++){
            StateComponent.State[][] child = state.clone();
            int[] move = moves.get(i);
            child[move[0]][move[1]] = StateComponent.State.Circle;
            currentScore = max(child);
            if (currentScore < bestScore)
                bestScore = currentScore; // Pass, bestScore is assigned lowest current score.
            child[move[0]][move[1]] = StateComponent.State.None; // Redundant, child is cloned.
        }
        return bestScore;
    }

    private int max(StateComponent.State[][] state){
        //System.out.println("MAX : " + Arrays.deepToString(state));
        int winScore = vs.evaluate(state);

        if (winScore != -1)
            return winScore;

        ArrayList<int[]> moves = getAvailableMoves(state);
        int bestScore = Integer.MIN_VALUE;
        int currentScore;
        for (int i = 0 ; i < moves.size(); i++){
            StateComponent.State[][] child = state.clone();
//            System.out.println(Arrays.deepToString(state));
            int[] move = moves.get(i);
            child[move[0]][move[1]] = StateComponent.State.Cross;
            currentScore = min(child);
            if (currentScore > bestScore)
                bestScore = currentScore;
            child[move[0]][move[1]] = StateComponent.State.None;
        }
        return bestScore;
    }

    private int[] bestMoveMax(int[][] scores){ // Pass
        int bestIndex = 0;
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0 ; i < scores.length; i++){
            if (scores[i][0] > bestScore){
                bestScore = scores[i][0];
                //System.out.println("BestMaxScore: " + bestScore);
                bestIndex = i;
            }
        }
        int[] bestMove = {scores[bestIndex][1], scores[bestIndex][2]};
        return bestMove;
    }

    private int[] bestMoveMin(int[][] scores){ // Pass
        int bestIndex = 0;
        int bestScore = Integer.MAX_VALUE;
        for (int i = 0 ; i < scores.length; i++){
            if (scores[i][0] < bestScore){
                bestScore = scores[i][0];
                System.out.println("BestMinScore: " + bestScore);
                bestIndex = i;
            }
        }
        int[] bestMove = {scores[bestIndex][1], scores[bestIndex][2]};
        return bestMove;
    }
}
