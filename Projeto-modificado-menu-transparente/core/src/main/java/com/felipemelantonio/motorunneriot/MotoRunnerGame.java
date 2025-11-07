package com.felipemelantonio.motorunneriot;

import com.badlogic.gdx.Game;
import com.felipemelantonio.motorunneriot.screens.GameScreen;
import com.felipemelantonio.motorunneriot.screens.MenuScreen;

public class MotoRunnerGame extends Game {
    @Override
    public void create() {
        // Inicia um GameScreen "por trás" e coloca o Menu transparente por cima; sem carros/coins/colisão
        GameScreen behind = new GameScreen(this, 1, true); // backgroundMode = true
        setScreen(new MenuScreen(this, behind));
    }
}
