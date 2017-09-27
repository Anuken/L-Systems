package io.anuke.lsystem;

import static io.anuke.ucore.core.Core.camera;

import java.util.HashMap;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

import io.anuke.ucore.UCore;
import io.anuke.ucore.core.*;
import io.anuke.ucore.lsystem.LSystemData;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.scene.utils.Cursors;
import io.anuke.ucore.util.Mathf;

public class Control extends RendererModule{
	//private GifRecorder recorder = new GifRecorder(batch);
	
	final private Color start = Color.valueOf("37682c");
	final private Color end = Color.valueOf("94ac62");
	final private Stack<Vector3> stack = new Stack<>();
	final private Json json = new Json();
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
	
	private String expath = "";
	private String exfilename = "out";
	
	private String impath = "";
	private String imfilename = "out";
	
	public Control(){
		Core.cameraScale = 1;
		
		if(System.getProperty("user.name").equals("anuke")){
			expath = "LSystemExport/";
			impath = "LSystemExport/";
		}
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
	
	public void setExportFilename(String name){
		exfilename = name;
	}
	
	public String getExportFilePath(){
		return expath;
	}
	
	public void setExportFilePath(String name){
		expath = name;
	}
	
	public void setImportFilename(String name){
		imfilename = name;
	}
	
	public String getImportFilePath(){
		return impath;
	}
	
	public void setImportFilePath(String name){
		impath = name;
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
	
	public String getAxiom(){
		return axiom;
	}
	
	public int getIterations(){
		return iterations;
	}
	
	public float getAngle(){
		return space;
	}
	
	public float getThickness(){
		return Draw.getThickness();
	}
	
	public float getLength(){
		return len;
	}
	
	public float getSwayScale(){
		return swayscl;
	}
	
	public float getSwaySpace(){
		return swayspace;
	}
	
	public float getSwayPhase(){
		return swayphase;
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
	
	private void drawForward(){
		float sway = swayscl*MathUtils.sin(Timers.time()/swayphase+stack.size()*swayspace);
		
		float radians = MathUtils.degRad*(-angle+180 + sway);
		
		float nx = len;
		float ny = 0;
		float cos = MathUtils.cos(radians);
		float sin = MathUtils.sin(radians);
		float newX = nx * cos - ny * sin;
		float newY = nx * sin + ny * cos;
		
		nx = newX;
		ny = newY;
		
		float scl = (float)stack.size()/(maxstack-2);
		
		Draw.color(start, end, scl);
		Draw.line(x, y, x+nx, y+ny);
		
		x += nx;
		y += ny;
	}
	
	public void exportFile(){
		try{
			
			LSystemData data = new LSystemData(axiom, rules, iterations, 
					swayspace, swayphase, swayscl, len, space, Draw.getThickness(), start, end);
			FileHandle file = Gdx.files.absolute(Gdx.files.absolute(System.getProperty("user.home")) 
					+ "/" + expath + "/" + exfilename+".json");

			file.writeString(json.toJson(data), false);
			UCore.log("File written to " + file.toString());
			
			Vars.ui.showMessage("File written to " + file.toString());
		
		}catch (Exception e){
			Vars.ui.showMessage("File write failed: \n" + e.getClass() + ": " + e.getMessage(), Color.SCARLET);
			e.printStackTrace();
		}
	}
	
	public void importFile(){
		try{
			
			FileHandle file = Gdx.files.absolute(Gdx.files.absolute(System.getProperty("user.home")) 
					+ "/" + impath + "/" + imfilename+".json");
			
			LSystemData data = json.fromJson(LSystemData.class, file);
			data.normalize();
			rules = data.rules;
			axiom = data.axiom;
			end.set(data.end);
			start.set(data.start);
			iterations = data.iterations;
			swayspace = data.swayspace;
			swayscl = data.swayscl;
			swayphase = data.swayphase;
			len = data.len;
			space = data.space;
			Draw.thickness(data.thickness);
			
			generate();
			
			Core.scene.clear();
			Vars.ui.init();
			
			Vars.ui.showMessage("File opened from " + file.toString());
			
		}catch (Exception e){
			Vars.ui.showMessage("File open failed: \n" + e.getMessage(), Color.SCARLET);
			e.printStackTrace();
		}
	}
	
	private void input(){
		if(Inputs.keyDown(Keys.ESCAPE))
			Gdx.app.exit();
		
		if(Inputs.scrolled()){
			camera.zoom = Mathf.clamp(camera.zoom-Inputs.scroll()/5f*delta(), 0.1f, 10f);
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
		
		Timers.update();
		
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
