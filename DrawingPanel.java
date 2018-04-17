package memoryCardGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import resources.SoundPlayer;

public class DrawingPanel extends JPanel implements MouseListener{

	public static final int NUM_OF_CARDS = 24;
	public static final int NUM_OF_COLUMNS = 5;
	public static final int MARGIN = 10;
	private JFrame frame;
	private Card[] cards;
	private int score = 100;
	private long lastTimeCardSelected;
	private static SoundPlayer buzz, chime, tick;
	
	static {
		buzz = new SoundPlayer("jrJava/memoryCardGame_3/buzz.wav");
		chime = new SoundPlayer("jrJava/memoryCardGame_3/chime.wav");
		tick = new SoundPlayer("jrJava/memoryCardGame_3/tick.wav");
	}
	
	private boolean noneSelected = true, oneSelected, twoSelected;
	private Card firstSelected, secondSelected;

	public DrawingPanel(){
		int width = 2*MARGIN + NUM_OF_COLUMNS*Card.SIZE;
		int height = 2*MARGIN + (NUM_OF_CARDS/NUM_OF_COLUMNS)*Card.SIZE;
		if (NUM_OF_CARDS%NUM_OF_COLUMNS != 0) height += Card.SIZE;
		frame = new JFrame("Score: " + score);
		frame.setBounds(200, 50, 0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setPreferredSize(new Dimension(width,height));
		frame.getContentPane().add(this);

		cards = new Card[NUM_OF_CARDS];
		for(int i=0; i<cards.length; i++){
			cards[i] = new Card(i/2, MARGIN + i%NUM_OF_COLUMNS*Card.SIZE, MARGIN + i/NUM_OF_COLUMNS*Card.SIZE);
		}
		for(int i=0; i<cards.length; i++){
			cards[i].swapImageWith(cards[(int)(Math.random()*cards.length)]);
		}
		
		frame.pack();
		frame.setVisible(true);
		this.addMouseListener(this);
	}

	public void paintComponent(Graphics g){
		//draw the card
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());

		for(int i=0; i<cards.length; i++){
			cards[i].draw(g);
		}
		
	}

	public void mousePressed(MouseEvent e){
		for(int i=0; i<cards.length; i++){
			if(!cards[i].hasBeenMatched() && cards[i].isSelected(e.getX(), e.getY())){
				if(noneSelected){
					firstSelected = cards[i];
					firstSelected.show();
					noneSelected = false;
					oneSelected = true;
					tick.play();
				}
				else if (oneSelected){
					if(cards[i]==firstSelected) return;
					secondSelected = cards[i];
					secondSelected.show();
					oneSelected = false;
					twoSelected = true;
					lastTimeCardSelected = System.currentTimeMillis();
					tick.play();
				}
				this.repaint();
				break;
			}
		}
	}

	public void mouseReleased(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}

	public void applyGameLogic(){
		//Game logic will be applied
		if(!twoSelected || System.currentTimeMillis()-lastTimeCardSelected < 2000) return;
		if (firstSelected.matchWith(secondSelected)){
			firstSelected.setMatched();
			secondSelected.setMatched();
			score += 10;
			frame.setTitle("Score: " + score);
			chime.play();
		}
		else{
			firstSelected.hide();
			secondSelected.hide();
			score -= 5;
			frame.setTitle("Score: " + score);
			buzz.play();

		}
		this.repaint();
		twoSelected = false;
		noneSelected = true;
		firstSelected = null;
		secondSelected = null;
	}

	public void showAll(){
		for(int i=0; i<cards.length; i++) cards[i].show();
	}
	
	public void hideAll(){
		for(int i=0; i<cards.length; i++) cards[i].hide();
	}
	
	public static void main(String[] args) {
		DrawingPanel panel = new DrawingPanel();
		panel.showAll();
		panel.repaint();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
		panel.hideAll();
		panel.repaint();
		while(true){
			panel.applyGameLogic();
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
		}

	}

}
