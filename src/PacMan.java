import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Random;

import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prvDirction = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x +=this.velocityX;
            this.y +=this.velocityY;
            for(Block wall:walls){
                if(collision(this, wall)){
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prvDirction;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -titleSize / 4;
            } else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = titleSize / 4;
            } else if (this.direction == 'L') {
                this.velocityX = -titleSize / 4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                this.velocityX = titleSize / 4;
                this.velocityY = 0;
            }
        }

        void reset(){
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int titleSize = 32;
    private int boardWidth = columnCount * titleSize;
    private int boardHeight = rowCount * titleSize;

    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls, foods, ghosts;
    Block pacman;
    Timer gameLoop;
    char[] direction = {'U','D','L','R'};
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for(Block ghost:ghosts){
            char newDirection = direction[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tile = tileMap[r].charAt(c);
                int x = c * titleSize;
                int y = r * titleSize;

                switch (tile) {
                    case 'X' -> walls.add(new Block(wallImage, x, y, titleSize, titleSize));
                    case 'b' -> ghosts.add(new Block(blueGhostImage, x, y, titleSize, titleSize));
                    case 'o' -> ghosts.add(new Block(orangeGhostImage, x, y, titleSize, titleSize));
                    case 'p' -> ghosts.add(new Block(pinkGhostImage, x, y, titleSize, titleSize));
                    case 'r' -> ghosts.add(new Block(redGhostImage, x, y, titleSize, titleSize));
                    case 'P' -> pacman = new Block(pacmanRightImage, x, y, titleSize, titleSize);
                    case ' ' -> foods.add(new Block(null, x + 14, y + 14, 4, 4));
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }
        g.setFont(new Font("Arial",Font.PLAIN,18));
        if(gameOver){
            g.drawString("Game Over: "+  String.valueOf(score), titleSize/2,titleSize/2);
        }
        else{
            g.drawString("x"+ String.valueOf(lives)+" Score: "+String.valueOf(score), titleSize/2,titleSize/2);
        }
    }

    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for(Block wall:walls){
            if(collision(pacman, wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            } 
        }

        for(Block ghost:ghosts){
            if(collision(ghost, pacman)){
                lives -= 1;
                if(lives == 0){
                    gameOver = true;
                    return;
                }
                resetPosition();
            }
            if(ghost.y == titleSize*9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for(Block wall:walls){
                if(collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = direction[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }  
        }

        Block foodEaten = null;
        for(Block food:foods){
            if(collision(pacman, food)){
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        if(foods.isEmpty()){
            loadMap();
            resetPosition();
        }
    }

    public boolean collision(Block a, Block b){
        return  a.x < b.x + b.width &&
                a.x+a.width >b.x &&
                a.y < b.y + b.height &&
                a.y+a.height > b.y;
    }

    public void resetPosition(){
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;

        for(Block ghost:ghosts){
            ghost.reset();
            char newDirection = direction[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            gameLoop.stop();
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {

        if(gameOver){
            loadMap();
            resetPosition();
            lives =3;
            score = 0;
            gameOver =false;
            gameLoop.start();
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> pacman.updateDirection('U');
            case KeyEvent.VK_DOWN -> pacman.updateDirection('D');
            case KeyEvent.VK_LEFT -> pacman.updateDirection('L');
            case KeyEvent.VK_RIGHT -> pacman.updateDirection('R');
        }

        if(pacman.direction =='U' ){
            pacman.image = pacmanUpImage;
        }
        else if(pacman.direction =='D' ){
            pacman.image = pacmanDownImage;
        }
        else if(pacman.direction =='L' ){
            pacman.image = pacmanLeftImage;
        }
        else if(pacman.direction =='R' ){
            pacman.image = pacmanRightImage;
        }
    }
}
