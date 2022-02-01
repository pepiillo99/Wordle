package me.pepe.WordLess;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import me.pepe.GameAPI.Game.Game;
import me.pepe.GameAPI.Screen.Screen;
import me.pepe.GameAPI.Utils.RenderUtils;
import me.pepe.GameAPI.Windows.KeyInput;
import me.pepe.GameAPI.Windows.Windows;

public class WinLoseScreen extends Screen {
	private boolean win = false;
	private boolean gamed = false;
	private String word = "";
	private MainScreen mainScreen;
	public WinLoseScreen(Windows windows, Game game) {
		super(windows, game);
		setKeyInput(new KeyInput() {
			@Override
			public void tick() {}
			@Override
			public void onKeyPressed(int key) {}
			@Override
			public void onKeyReleased(int key) {
				if (key == 10 || key == 32) {
					mainScreen.restart(key!=10);
					getGame().setScreen("game");
					gamed = true;
				}
			}
		});
		mainScreen = (MainScreen) getGame().getScreenManager().getScreen("game");
	}
	@Override
	public void internalTick() {}
	@Override
	protected void paintLevel(Graphics g) {
		Font font = new Font("Aria", Font.PLAIN, 20);
		if (gamed) {
			RenderUtils.drawCenteredString(getWindows(), "¡Has " + (win ? "ganado" : "perdido") + "!", font.deriveFont(Font.PLAIN, 45), 50, 37, g, Color.WHITE);
			RenderUtils.drawCenteredString(getWindows(), ((mainScreen.isLetter() ? "La palabra" : "El número") + " era " + word.toLowerCase()), font, 50, 50, g, Color.WHITE);
			RenderUtils.drawCenteredString(getWindows(), "Tiempo de juego: " + ((mainScreen.getFinishTime() - mainScreen.getStartTime())/1000) + "s", font, 50, 56, g, Color.WHITE);
		} else {
			RenderUtils.drawCenteredString(getWindows(), "Elige tipo de juego", font.deriveFont(Font.PLAIN, 45), 50, 37, g, Color.WHITE);
		}
		RenderUtils.drawCenteredString(getWindows(), "Pulsa ESPACIO para jugar con palabras", font, 50, 90, g, Color.GRAY);
		RenderUtils.drawCenteredString(getWindows(), "Pulsa ENTER para jugar con numeros primos", font, 50, 95, g, Color.GRAY);

	}
	public void setWin(boolean b) {
		win = b;
	}
	public void setWord(String s) {
		word = s;
	}
}
