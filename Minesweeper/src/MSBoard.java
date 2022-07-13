import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
public class MSBoard extends JPanel implements ActionListener{

    // could add so it clears all empty tiles that touch cleared area, add flags to put down, add timer, but i think im done with this for now.
    Image Scenic;
    Image Snowy;
    Image Tile;
    Image Clicked_Tile;
    Image Title;
    Image Explosion;
    private ArrayList<Integer> savedClicks = new ArrayList(); 
    private int BOARD_HEIGHT;
    private int BOARD_WIDTH;
    private int radius = 10;
    private final int DELAY = 100;
    private int CELL_SIZE = 50;
    private int[][] field;
    private int bombCount;
    private boolean ingame;
    private Timer timer;
    private Dimension d;

    public MSBoard(){
        initBoard();
        
    }

    private void initBoard() {
        
        addMouseListener(new MouseInputAdapter());
        addKeyListener(new TAdapter());

        loadImages();  
        setFocusable(true);
        
        d = new Dimension(0,0);
	    initGame();
    }

    private void initGame(){
        ingame=true;
        timer = new Timer(DELAY, this);
	    timer.start();
        BOARD_HEIGHT=(radius*CELL_SIZE) + (CELL_SIZE*3);
        BOARD_WIDTH =(radius*CELL_SIZE) + (CELL_SIZE*2); 
        d.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        setPreferredSize(d);
        field = makeField();

    }
    private void newGame(){
        ingame=true;
        savedClicks.clear();
        bombCount=0;
        initGame();
    }
    private void gameOver(){
        ingame=false;
    }
    @Override
	public void paintComponent(Graphics g) {
        
	    super.paintComponent(g);
        
        g.drawImage(Scenic,0,0, getWidth(), getHeight(), this);
        
	    doDrawing(g);
	}
	    
	private void doDrawing(Graphics g) {
        //UI stuff and text
        Font font = new Font("SansSerif Bold", Font.PLAIN, 20);
        g.setFont(font);
        g.setColor(Color.green);
        g.drawString("Restart: Press enter", 5 , 20);

        font = new Font("SansSerif Bold", Font.PLAIN, 15);
        g.setFont(font);
        g.setColor(Color.magenta);
        g.drawString("Easy: 1", 5 , 40);
        g.drawString("Medium: 2", 5 , 60);
        g.drawString("Hard: 3", 5 , 80);
        font = new Font("SansSerif Bold", Font.PLAIN, 30);
        g.setFont(font);
        g.drawString("Mines: "+bombCount, 450, CELL_SIZE);

        g.drawImage(Title, 175, 50, CELL_SIZE*5,CELL_SIZE, this);


        //make a visual grid of plain tiles
        g.setColor(Color.white);
        for(int x=CELL_SIZE;x<BOARD_WIDTH-CELL_SIZE;x+= CELL_SIZE){
            for(int y=CELL_SIZE*2; y<BOARD_HEIGHT-CELL_SIZE;y+=CELL_SIZE){
                g.drawImage(Tile,x,y, CELL_SIZE, CELL_SIZE, this);
            }
        }
        
        //draw all clicked tiles
        for(int i=0;i<savedClicks.size();i+=2){
            g.drawImage(Clicked_Tile,savedClicks.get(i),savedClicks.get(i+1),CELL_SIZE,CELL_SIZE,this);
        }

        

        //draw numbers on 0 tiles, else draw an x on mines      
        for(int i=0;i<savedClicks.size();i+=2){
            int x=(savedClicks.get(i)/CELL_SIZE)-1;
            int y=(savedClicks.get(i+1)/CELL_SIZE)-2;
  
            //Below commented code is to see coordinates of click and if 1 or 0
            //System.out.println("DEBUG::::"); 
            //System.out.println(x + " " +y);
            //System.out.println(field[x][y]);
            if(field[x][y] == 0){                
                
                //count touching bombs
                int count = 0;
                if(field[x][y]!=1){
                    if(x<radius-1){
                        if(field[x+1][y]==1){
                            count+=1;
                        }
                    }
                    if(x>0){
                        if(field[x-1][y]==1){
                            count+=1;
                        }
                    }
                    if(y<radius-1){
                        if(field[x][y+1]==1){
                            count+=1;
                        }
                    }
                    if(y<radius-1 && x>0){
                        if(field[x-1][y+1]==1){
                            count+=1;
                        }
                    }
                    if(x<radius-1 && y<radius-1){
                        if(field[x+1][y+1]==1){
                            count+=1;
                        }
                    }
                    if(y>0){
                        if(field[x][y-1]==1){
                            count+=1;
                        }
                    }
                    if(x>0 && y>0){
                        if(field[x-1][y-1]==1){
                            count+=1;
                        }
                    }
                    if(x<radius-1 && y>0){
                        if(field[x+1][y-1]==1){
                            count+=1;
                        }
                    }
                }
                g.setColor(Color.BLACK);       
                font = new Font("Arial Narrow Bold", Font.PLAIN, 50);
                g.setFont(font);
                if(count!=0){
                    String str = String.valueOf(count);
                    g.drawString(str, (x*CELL_SIZE+CELL_SIZE)+12, (y*CELL_SIZE + (CELL_SIZE*3))-5);
                }
                count=0;
            }
            else{
                      
                font = new Font("Arial Narrow Bold", Font.BOLD, 50);
                g.setColor(Color.RED);
                g.setFont(font);
                g.drawImage(Explosion, (x*CELL_SIZE)+CELL_SIZE, (y*CELL_SIZE)+CELL_SIZE*2,CELL_SIZE,CELL_SIZE,this);
            }
            if(ingame==false){//game over text and show mines 'X'

                //reveal mines
                for(int tx=0;tx<radius;tx+=1){
                    for(int ty=0;ty<radius;ty+=1){
                        if(field[tx][ty] == 1){  
                            g.drawImage(Explosion, (tx*CELL_SIZE)+CELL_SIZE, (ty*CELL_SIZE)+CELL_SIZE*2,CELL_SIZE,CELL_SIZE,this);
                        }  
                    }             
                } 
                //gameover message.             
                g.setColor(Color.BLACK);
                g.fillRect(175, 40, 250, 60);
                g.setColor(Color.red); 
                g.drawString("Game Over", 190, 85);
            }
        }
    }
    
    private int[][] makeField(){

        int[][] field = new int[radius][radius];
        Random r = new Random();
        for(int x=0;x< radius;x++){
            for(int y=0;y<radius;y++){
                //spawn safe tiles at a ratio of 1 for every 4
                int num = r.nextInt(4);
                if(num!=1)
                    num=0;           
                field[x][y]=num;
            }
        }
        


        //System.out.println(Arrays.deepToString(field).replace("], ", "]\n")); //print out matrix of field (1s and 0s)
        return field;
    }  

	private void loadImages(){
        ImageIcon iisc = new ImageIcon("src/images/Scenic.jpg");
	    Scenic = iisc.getImage();

        ImageIcon iisn = new ImageIcon("src/images/Snowy.jpg");
	    Snowy = iisn.getImage();

        ImageIcon iit = new ImageIcon("src/images/tile.jpg");
        Tile = iit.getImage();

        ImageIcon iict = new ImageIcon("src/images/Clicked-Tile.jpg");
        Clicked_Tile = iict.getImage();

        ImageIcon iitt = new ImageIcon("src/images/title.jpg");
        Title = iitt.getImage();

        ImageIcon iie = new ImageIcon("src/images/explosion.jpg");
        Explosion=iie.getImage();
    }
    @Override
	public void actionPerformed(ActionEvent e) {
	    repaint();
	}
    private class TAdapter extends KeyAdapter {
        @Override
	    public void keyPressed(KeyEvent e) {
	        int key = e.getKeyCode();

	        if (key == KeyEvent.VK_ENTER) {

                newGame();
	        }
            if(key==KeyEvent.VK_1){
                radius=8;
                newGame();
            }
            if(key==KeyEvent.VK_2){
                radius=10;
                newGame();
            }
            if(key==KeyEvent.VK_3){
                radius=12;
                newGame();
            }
	    }
	}
    private class MouseInputAdapter extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            Integer x =e.getX();
            Integer y =e.getY();
            //System.out.println("clicked: " + x + " " + y); //show clicked coords
            if(ingame){
                if(x>CELL_SIZE && x<(BOARD_WIDTH-CELL_SIZE) && y>CELL_SIZE*2 && y<(BOARD_HEIGHT-CELL_SIZE)){
                    x = (x/CELL_SIZE) * CELL_SIZE;
                    y = (((y-CELL_SIZE)/CELL_SIZE) * CELL_SIZE)+CELL_SIZE;
                    int tilex=(x/CELL_SIZE)-1;
                    int tiley=(y/CELL_SIZE)-2;
                    
                    if(savedClicks.size()==0){ //check if it is the first click, to clear a starting chunk.
                        
                        field[tilex][tiley]=0;
                        
                        //begins to go through surrounding tiles to make sure it can clear space
                        if(tilex<radius-1 && tiley<radius-2){//r + down
                            savedClicks.add(x+CELL_SIZE);
                            savedClicks.add(y+CELL_SIZE);
                            field[tilex+1][tiley+1]=0;
                        }
                        if(tilex<radius-1){
                            savedClicks.add(x+CELL_SIZE);//r
                            savedClicks.add(y);
                            field[tilex+1][tiley]=0;
                        }
                        if(tiley>0){
                            savedClicks.add(x);//up
                            savedClicks.add(y-CELL_SIZE);
                            field[tilex][tiley-1]=0;
                        }
                        if(tiley<radius-2){
                            savedClicks.add(x);//down
                            savedClicks.add(y+CELL_SIZE);
                            field[tilex][tiley+1]=0;
                        }
                        if(tiley<radius-3){
                            savedClicks.add(x);//down 2
                            savedClicks.add(y+CELL_SIZE+CELL_SIZE);
                            field[tilex][tiley+2]=0;
                        }
                        if(tilex>0 && tiley<radius-2){
                            savedClicks.add(x-CELL_SIZE);//l + down
                            savedClicks.add(y+CELL_SIZE);
                            field[tilex-1][tiley+1]=0;
                        }
                        if(tilex>0){
                            savedClicks.add(x-CELL_SIZE);//l
                            savedClicks.add(y);
                            field[tilex-1][tiley]=0;
                        }

                    }
                    if(field[tilex][tiley]==1){
                        ingame=false;
                        gameOver();
                    }
                    
                    savedClicks.add(x);
                    savedClicks.add(y);

                    bombCount=0;
                    for(int i=0;i< radius;i++){//count bombs
                        for(int p=0;p<radius;p++){
                            if(field[i][p]==1)//1s are bombs
                                bombCount+=1;          
                        }
                    }
                    
                }
            }
            repaint();
        }
        
    }
}

