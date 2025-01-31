
/*Spring Fling 
 by Andrew Ebenbach, Manjiri Bhandarwar, Toni-Ann Royes, & Andy Xiong
 5/10/21 
 The character jumps up through a series of ledges. 
 The player can use buttons to control their movement in the left and right directions. 
 The character automatically jumps when they land on a ledge. 
 The game generates more ledges as the player goes higher and higher, but deletes ledges which have gone below the player�s screen. 
 The score of the player is how high they go. The game ends when the player falls below the screen.
 */

import processing.core.*;
import ddf.minim.*;


public class Game extends PApplet{

	public static void main(String[] args) {
		PApplet.main("Game");
		
	}
    //initialize variables
    Player player;
    boolean left_pressed;
    boolean right_pressed;
    boolean space_pressed;
    boolean q_pressed;
    boolean game_started;
    boolean game_over = false;
    boolean i_pressed;
    boolean held_down;
    boolean on_title = true;
    boolean display_title = true;
    boolean display_instruction = false;
    float cam_speed;
    float score;
    Ledge[] level1;
    Ledge[] level2;
    PImage ledge_img;
    PImage character_img;
    PImage character_r_img;
    PImage background_img;
    PImage instruction_img;
    PImage title_img;
    PImage end_img;
    Minim minim;
    AudioPlayer audio_player;

    
    public void settings() {  
   	 size(400,500);
    }
    public void setup() {
   	 //get all the art assets from the folder
   	 ledge_img = loadImage("Tile for Intro to CS Game.png");
   	 character_img = loadImage("Character for Intro to CS .png");
   	 character_r_img = loadImage("Character for Intro to CS reversed.png");
   	 background_img = loadImage("Background for Intro to CS game.png");
   	 instruction_img = loadImage("Instruction Page.png");
   	 title_img = loadImage("Title Screen for CS Game.png");
   	 end_img = loadImage("Game Over Screen for CS game.png");
   	 
   	 background_img.resize(400,500);
   	 instruction_img.resize(400,500);
   	 title_img.resize(400,500);
   	 end_img.resize(400,500);
   	 
   	 background(title_img);
   	 cam_speed = 0;
   	 score = 0;
   	 player = new Player();
	    
	 //Setting up the background music 
   	 minim = new Minim(this);
   	 audio_player = minim.loadFile("in motion.mp3");
   	 audio_player.loop();
   	 
   	 //infinite, random generation works by creating two chunks at a time called levels
   	 level1 = new Ledge[6];
   	 level2 = new Ledge[6];
   	 generateLevel(level1,0);
   	 //starting ledge
   	 level1[0] = new Ledge(190,490,0,0);
   	 generateLevel(level2, 500);

    }
    
    //detecting which buttons have been pressed.
    //the players speed cannot be adjusted directly through these methods because otherwise when both buttons are
    //pressed the player will move in the most recently pressed direction
    //I added some other keys so you can move from screen to screen
    //there's also an issue with holding down the key registering countless times so I added a boolean to check that
    public void keyPressed() {

   	 if (key == 'a') {
   		 left_pressed = true;
   	 }
   	 if (key == 'd') {
   		 right_pressed = true;
   	 }
   	 if (key == 32) {
   		 space_pressed = true;
   	 }
   	 if (key == 'i') {
   		 i_pressed = true;
   	 }
   	 if (key == 'q') {
   		 q_pressed = true;
   	 }


    }
    //detecting which buttons have been released
    public void keyReleased() {

   	 if (key == 'a') {
   		 left_pressed = false;
   	 }
   	 if (key == 'd'){
   		 right_pressed = false;
   	 }
   	 if (key == 32) {
   		 space_pressed = false;
   	 }
   	 if (key == 'i') {
   		 i_pressed = false;
   		 held_down = false;
   	 }
   	 if (key == 'q') {
   		 q_pressed = false;
   	 }
    }
    public void draw() {
   	 
   	 //set the background based on what keys are/are not pressed
   	 if (space_pressed && on_title) {
   		 game_over = false;
   		 game_started = true;
   		 on_title = false;
   	 }
   	 
   	 if (display_title) {
   		 background(title_img);
   		 on_title = true;
   	 }
   	 else if (display_instruction) {
   		 background(instruction_img);
   		 on_title = false;
   	 }
   	 
   	 if (i_pressed && !game_started && held_down == false) {
   		 held_down = true;
   		 if (display_title) {
   			 display_title = false;
   			 display_instruction = true;
   		 }
   		 else if (display_instruction) {
   			 display_title = true;
   			 display_instruction = false;
   		 }
   	 }

   	 if (game_started) {
   		 //function resolves the x speed of the player at this particular frame
   		 player.resolveX(left_pressed,right_pressed);
   		 
   		 //checks if player has landed, and if so jumps
   		 if (isLanded(player, level1,level2)) {
   			 player.y_speed = -9f;
   		 }
   		 else if (player.y_speed < 8f){
   			 player.y_speed += .20f;
   		 }
   		 
   		 //moves camera at player speed if player is halfway up screen
   		 if (player.y_pos < 250f && player.y_speed < 0f) {
   			 cam_speed = player.y_speed * -1;
   		 }
   		 else {
   			 cam_speed = 0f;
   		 }
   		 //cam speed also happens to be the players additional score for each frame
   		 score += cam_speed;
   		 
   		 //update and display everything
   		 background(background_img);
   		 updateLevel(level1,level2,cam_speed);
   		 displayLevel(level1,level2,this);
   		 player.update(cam_speed);
   		 player.display(this);
   		 
   		 //Displaying score
   		 PFont rainy_hearts;
   		 rainy_hearts = createFont("rainyhearts.ttf", 26);
	   	 textFont(rainy_hearts);
	   	 textAlign(RIGHT);
   		 text((int)score, 390, 20);
   		 fill(255,255,255);
   		 
   		 if (player.playerStatus()) {
   			 game_started = false;
   			 game_over = true;
   		 }
   	 }
   	 
   	 if (game_over) {
   		 background(end_img);
   		 textAlign(CENTER);
   		 textSize(40);
   		 text((int)score, 300, 260);
  		 fill(255,255,255);
  		 
   		 if (space_pressed) {
   			 game_over = false;
   			 setup();
   		 }
   		 if(q_pressed) {
			 audio_player.pause();
   			 exit();
   		 }   	 
   	 }
    }
    
    //iterates through all ledges to check if player has landed on any of them
    public static boolean isLanded(Player player, Ledge[] level1, Ledge[] level2) {
   	 for(Ledge ledge : level1) {
   		 if (player.x_pos >= ledge.x_pos-15f && player.x_pos <= ledge.x_pos+25f
   				 && player.y_pos >= ledge.y_pos-15f && player.y_pos <= ledge.y_pos - 5f && player.y_speed>0) {
   			 return true;
   		 }
   	 }
   	 for(Ledge ledge : level2) {
   		 if (player.x_pos >= ledge.x_pos-15f && player.x_pos <= ledge.x_pos+25f
   				 && player.y_pos >= ledge.y_pos-15f && player.y_pos <= ledge.y_pos - 5f && player.y_speed>0) {
   			 return true;
   		 }
   	 }
   	 return false;
   	 
    }
    
    //generates new chunk of ledges when one goes past screen
    public static void generateLevel(Ledge[] level, float height) {
   	 float interval = (500 / level.length);
   	 for(int x=0; x < level.length ; x++) {
   		 float x_pos = (float)(Math.random() * 376);
   		 

   		 if (Math.random() <= 0.17) {
   	   	     level[x] = new Ledge(x_pos, 490f - (interval *x) - height, (float)(Math.random() * 3), 0f);

   	   	 }
   	   	 else {
   	   	     level[x] = new Ledge(x_pos, 490f - (interval *x) - height, 0f, 0f);
   	   	 }
   	 }
    }
    //moves ledges at cam speed, and triggers new level generation if needed
    public static void updateLevel(Ledge[] level1, Ledge[] level2, float cam_speed) {
   	 if (level1[5].y_pos < 500) {
   		 for(Ledge ledge : level1) {
   			 ledge.update(cam_speed);
   		 }
   	 }
   	 else {
   		 generateLevel(level1, 500);
   	 }
   	 if(level2[5].y_pos<500) {
   		 for(Ledge ledge : level2) {
   			 ledge.update(cam_speed);
   		 }
   	 }
   	 else {
   		 generateLevel(level2, 500);
   	 }
    }
    //displays all ledges
    public static void displayLevel(Ledge[] level1,Ledge[] level2, Game game) {
   	 for(Ledge ledge : level1) {
   		 ledge.display(game);
   	 }
   	 for(Ledge ledge : level2) {
   		 ledge.display(game);
   	 }
    }
    
    //displays the images
    public void printImage(String img_name, float x_pos, float y_pos, float width, float height) {    
   	 if (img_name.equals("ledge_img")){
   		 this.image(ledge_img, x_pos, y_pos, width, height);
   	 }
   	 else if (img_name.equals("character_img")){
   		 this.image(character_img, x_pos, y_pos, width, height);
   	 }
   	 else if (img_name.equals("character_r_img")){
   		 this.image(character_r_img, x_pos, y_pos, width, height);
   	 }
    }
    		
}
