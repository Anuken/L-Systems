package io.anuke.lsystem;

import java.util.HashMap;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.scene.utils.Cursors;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Control extends RendererModule{
	//private GifRecorder recorder = new GifRecorder(batch);
	
	final private Color start = Color.valueOf("37682c");
	final private Color end = Color.valueOf("94ac62");
	final private Stack<Vector3> stack = new Stack<>();
	private float len = 4f;
	private float space = 25;
	
	private boolean moving = false;
	private float lastx, lasty;
	
	private float swayscl = 2f;
	private float swayphase = 15f;
	private float swayspace = 1f;
	private int iterations = 6;
	private String axiom = "X";
	private HashMap<Character, String> rules = map(
		'X', "F-[[X]+X]+F[+FX]-X",
		'F', "FF"
	);
	
	private int maxstack = 0;
	private LSystemTask task = new LSystemTask();
	private float angle = 90;
	private float x, y;
	
	public Control(){
		cameraScale = 1f;
		
		//setPixelation();
	}
	
	public void init(){
		generate();
		
		clear();
	}
	
	public Color startColor(){
		return start;
	}
	
	public Color endColor(){
		return end;
	}
	
	public void setSwaySpace(float sspace){
		swayspace = sspace;
	}
	
	public void setSwayScale(float sscl){
		swayscl = sscl;
	}
	
	public void setSwayPhase(float sph){
		swayphase = sph;
	}
	
	public void setAxiom(String ax){
		axiom = ax;
		generate();
	}
	
	public void setIterations(int i){
		iterations = i;
		generate();
	}
	
	public void setAngle(float angle){
		space = angle;
		generate();
	}
	
	public void setLength(float length){
		len = length;
		generate();
	}
	
	public HashMap<Character, String> rules(){
		return rules;
	}
	
	public int getCharacters(){
		return task.getCurrent().length();
	}
	
	public boolean isLoading(){
		return !task.isDone();
	}
	
	public void generate(){
		clear();
		
		task.reset(axiom, iterations, rules);
	}
	
	private void clear(){
		angle = 90;
		x = y = 0;
		
		stack.clear();
	}
	
	private void draw(char c){
		if(c == 'F'){
			drawForward();
		}else if(c == '-'){
			angle -= space;
		}else if(c == '+'){
			angle += space;
		}else if(c == '['){
			push();
		}else if(c == ']'){
			pop();
		}
	}
	
	private void drawForward(){
		float sway = swayscl*MathUtils.sin(Timers.time()/swayphase+stack.size()*swayspace);
		
		vector.set(len, 0).rotate(-angle+180 + sway);
		
		Draw.color(start, end, (float)stack.size()/(maxstack-3));
		Draw.line(x, y, x+vector.x, y+vector.y);
		
		x += vector.x;
		y += vector.y;
	}
	
	private void push(){
		stack.push(new Vector3(x, y, angle));
		maxstack = Math.max(stack.size(), maxstack);
	}
	
	private void pop(){
		if(stack.isEmpty()) return;
		Vector3 vec = stack.pop();
		x = vec.x;
		y = vec.y;
		angle = vec.z;
	}
	
	private void input(){
		if(Inputs.keyDown(Keys.ESCAPE))
			Gdx.app.exit();
		
		if(Inputs.scrolled()){
			camera.zoom = Mathf.clamp(camera.zoom-Inputs.scroll()/10f*delta(), 0.1f, 10f);
			camera.update();
		}
		
		if(!(Vars.ui.hasMouse() && !Inputs.keyDown(Keys.CONTROL_LEFT))){
			if(Inputs.buttonUp(Buttons.LEFT)){
				lastx = Graphics.mouse().x;
				lasty = Graphics.mouse().y;
				moving = true;
				Cursors.setHand();
			}
			
			if(Inputs.buttonRelease(Buttons.LEFT)){
				moving = false;
				Cursors.restoreCursor();
			}
			
			if(Inputs.buttonDown(Buttons.LEFT) && moving){
				float dx = Graphics.mouse().x-lastx;
				float dy = Graphics.mouse().y-lasty;
				camera.position.sub(dx*camera.zoom, dy*camera.zoom, 0);
				camera.update();
				
				lastx = Graphics.mouse().x;
				lasty = Graphics.mouse().y;
			}
		}
	}
	
	public void update(){
		input();
		
		if(!task.isDone()){
			maxstack = 0;
			task.iterate();
		}
		
		Timers.update(Gdx.graphics.getDeltaTime()*60f);
		
		drawDefault();
		
		clear();
		
		//recorder.update();
		Inputs.update();
	}
	
	public void draw(){
		for(int i = 0; i < task.getCurrent().length(); i ++){
			draw(task.getCurrent().charAt(i));
		}
		Draw.color();
	}
	
	private HashMap<Character, String> map(Object... objects){
		 HashMap<Character, String> map = new HashMap<>();
		 for(int i = 0; i < objects.length; i += 2){
			 map.put((char)objects[i], (String)objects[i+1]);
		 }
		 return map;
	}
	
	public void resize(){
		setCamera(200, 200);
		camera.update();
	}
}
