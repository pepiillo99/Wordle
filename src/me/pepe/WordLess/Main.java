package me.pepe.WordLess;

import me.pepe.GameAPI.GameAPI;
import me.pepe.GameAPI.Game.Game;

public class Main {
	private String abcedario = "";
	public static void main(String[] args) {
		new Main();
	}
	public Main() {
		GameAPI gameAPI = new GameAPI();
		Game game = new Game("Wordle", 500, 500, null);
		game.getScreenManager().registerNewScreen("game", new MainScreen(game.getWindows(), game));
		game.getScreenManager().registerNewScreen("win/lose", new WinLoseScreen(game.getWindows(), game));
		game.setScreen("win/lose");
		game.getWindows().setResizable(false);
		new Thread() {
			@Override
			public void run() {
				game.start();
			}
		}.start();
	}
}
