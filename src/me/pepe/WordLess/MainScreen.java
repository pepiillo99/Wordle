package me.pepe.WordLess;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import me.pepe.GameAPI.Game.Game;
import me.pepe.GameAPI.Screen.Screen;
import me.pepe.GameAPI.Utils.RenderUtils;
import me.pepe.GameAPI.Utils.ResizeType;
import me.pepe.GameAPI.Utils.Utils;
import me.pepe.GameAPI.Windows.KeyInput;
import me.pepe.GameAPI.Windows.Windows;

public class MainScreen extends Screen {
	private boolean isLetter = true;
	private String word = "";
	private int intent = 0;
	private int letterSize = 0;
	private int[][] colors = new int[6][5];
	private String[] words = new String[] { "", "", "", "", "", ""};
	private long startTime = 0;
	private long finishTime = 0;
	private long messageTime = 0;
	private String message = "";
	public MainScreen(Windows windows, Game game) {
		super(windows, game);
		setKeyInput(new KeyInput() {
			@Override
			public void tick() {}
			@Override
			public void onKeyPressed(int key) {}
			@Override
			public void onKeyReleased(int key) {
				if (key == 10) {
					if (isLetter) {
						if (words[intent].length() != 0) {
							if (hasWord(words[intent])) {
								complete();
							} else {
								messageTime = System.currentTimeMillis() + 2500;
								message = "¡" + words[intent] + " no esta en el diccionario!";
							}
						} else {
							messageTime = System.currentTimeMillis() + 2500;
							message = "¡Nos has introducido nada!";
						}
					} else {
						try {
							int number = Integer.valueOf(words[intent]);
							if (!isPrime(number)) {
								complete();
							} else {
								messageTime = System.currentTimeMillis() + 2500;
								message = "¡" + number + " no es primo!";
							}
						} catch(NumberFormatException e) {
							messageTime = System.currentTimeMillis() + 2500;
							message = "¡Nos has introducido nada!";
						}
					}
				} else if (key == 8 && words[intent].length() >= 1) {
					words[intent] = words[intent].substring(0, words[intent].length()-1);
                	letterSize--;
				}
                String text = KeyEvent.getKeyText(key);
                if (text.length() == 1 && words[intent].length() < 5 && (!isLetter ? (key >= 48 && key <= 57) : !(key >= 48 && key <= 57))) {
                	words[intent] = words[intent] + text;
                	letterSize++;
                }
			}			
		});
	}
	@Override
	public void internalTick() {
		if (messageTime != 0 && messageTime - System.currentTimeMillis() <= 0) {
			messageTime = 0;
			message = "";
		}
	}
	@Override
	protected void paintLevel(Graphics g) {
		for (int x = 0; x < word.length(); x++) {
			for (int y = 0; y < 6; y++) {
				if (intent == y) {
					if (letterSize == x) {
						g.setColor(Color.MAGENTA);
					} else {
						g.setColor(Color.GRAY);
					}
				} else {
					g.setColor(Color.WHITE);
				}
				int color = colors[y][x];
				if (color != 0) {
					if (color == 1) {
						g.setColor(Color.GREEN);
					} else if (color == 2) {
						g.setColor(Color.DARK_GRAY);
					} else if (color == 3) {
						g.setColor(Color.ORANGE);
					}
					g.fillRect(100 + (x * 55), 50 + (y * 55), 50, 50);
				}
				g.drawRect(100 + (x * 55), 50 + (y * 55), 50, 50);
			}
		}
		g.setColor(Color.WHITE);
		for (int y = 0; y < words.length; y++) {
			for (int i = 0; i <words[y].length(); i++) {
				char c = words[y].charAt(i);
				g.setFont(new Font("Aria", Font.PLAIN, 45));
				g.drawString(c + "", 110 + (i * 55), 93 + (y * 55));
			}
		}
		if (messageTime != 0) {
			showMessage(message, g);
		}
	}
	private void showMessage(String message, Graphics g) {
		Font font = new Font("Aria", Font.PLAIN, 30);
	    FontMetrics metrics = g.getFontMetrics(font);
		RenderUtils.fillCenteredRect(getWindows(), 50, 90, metrics.stringWidth(message) + 20, metrics.getAscent() + 10, g, Color.RED, ResizeType.NONE);
		RenderUtils.drawCenteredString(getWindows(), message, font, 50, 89, g, Color.WHITE);
	}
	private void complete() {
		colors[intent] = new int[] { 0, 0, 0, 0, 0};
		HashMap<Character, Integer> letterCounter = new HashMap<Character, Integer>();
		for (int i = 0; i<words[intent].toLowerCase().length(); i++) {
			char c = word.toLowerCase().charAt(i);
			if (letterCounter.containsKey(c)) {
				letterCounter.put(c, letterCounter.get(c) + 1);
			} else {
				letterCounter.put(c, 1);
			}
			if (words[intent].toLowerCase().charAt(i) == c) {
				colors[intent][i] = 1;
				letterCounter.put(c, letterCounter.get(c) - 1);
			}
		}
		for (int i = 0; i<words[intent].toLowerCase().length(); i++) {
			char c  = words[intent].toLowerCase().charAt(i);
			if (colors[intent][i] != 1) {
				if (letterCounter.containsKey(c) && letterCounter.get(c) >= 1) {
					colors[intent][i] = 3;
					letterCounter.put(c, letterCounter.get(c) - 1);
				} else {
					colors[intent][i] = 2;
				}	
			}
		}
		if (words[intent].equalsIgnoreCase(word)) {
			WinLoseScreen winLose = (WinLoseScreen) getGame().getScreenManager().getScreen("win/lose");
			winLose.setWin(true);
			winLose.setWord(word);
			finishTime = System.currentTimeMillis();
			getGame().setScreen("win/lose");
		} else {
			intent++;
			letterSize = 0;
			if (intent >= 6) {
				WinLoseScreen winLose = (WinLoseScreen) getGame().getScreenManager().getScreen("win/lose");
				winLose.setWin(false);
				winLose.setWord(word);
				finishTime = System.currentTimeMillis();
				getGame().setScreen("win/lose");
			}			
		}
	}
	public void restart(boolean isLetter) {
		startTime = System.currentTimeMillis();
		this.isLetter = isLetter;
		intent = 0;
		letterSize = 0;
		colors = new int[6][5];
		words = new String[] { "", "", "", "", "", ""};
		try {
			BufferedReader bufr = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("files/" + (isLetter ? "words.txt" : "primes.txt"))));
			int count = (int) bufr.lines().count();
			int random = Utils.random.nextInt(count);
			bufr.close();
			bufr = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("files/" + (isLetter ? "words.txt" : "primes.txt"))));
			for (int i = 0; i < random; i++) {
				word = bufr.readLine();
			}
			bufr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	public boolean isLetter() {
		return isLetter;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getFinishTime() {
		return finishTime;
	}
	private boolean isPrime(int num) {
		boolean prime = false;
		for (int i = 2; i <= num / 2; ++i) {
			if (num % i == 0) {
				prime = true;
				break;
			}
		}
		return prime;
	}
	private boolean hasWord(String word) {
		boolean contains = false;
		try {
			BufferedReader bufr = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("files/" + (isLetter ? "words.txt" : "primes.txt"))));
			String s = "";
			while (!contains && (s = bufr.readLine()) != null) {
				if (s.equalsIgnoreCase(word)) {
					contains = true;
				}
			}
			bufr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return contains;
	}
}
