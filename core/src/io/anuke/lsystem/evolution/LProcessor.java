package io.anuke.lsystem.evolution;

import java.util.HashMap;
import java.util.Stack;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import io.anuke.ucore.lsystem.LGen;

public class LProcessor{
	private static Stack<Vector3> stack = new Stack<>();
	private static LTree tree;
	private static float lastx, lasty, angle, space;
	
	private static final float len = 1f;
	
	public static LTree getLines(String axiom, HashMap<Character, String> map, int iterations, float space){
		tree = new LTree();
		stack.clear();
		lastx = lasty = 0;
		angle = 90f;
		LProcessor.space = space;
		
		String string = LGen.gen(axiom, map, iterations);
		
		for(int i = 0; i < string.length(); i ++){
			drawc(string.charAt(i));
		}
		
		return tree;
	}
	
	private static void drawForward(){
		
		float radians = MathUtils.degRad*(-angle+180);
		
		float nx = len;
		float ny = 0;
		float cos = MathUtils.cos(radians);
		float sin = MathUtils.sin(radians);
		float newX = nx * cos - ny * sin;
		float newY = nx * sin + ny * cos;
		
		nx = newX;
		ny = newY;
		
		tree.lines.add(new Line(lastx, lasty, lastx+nx, lasty+ny));
		
		lastx += nx;
		lasty += ny;
	}
	
	private static void push(){
		stack.push(new Vector3(lastx, lasty, angle));
		tree.branches ++;
	}
	
	private static void pop(){
		tree.leaves.add(new Leaf(lastx, lasty, angle));
		
		if(stack.isEmpty()) return;
		
		Vector3 vec = stack.pop();
		lastx = vec.x;
		lasty = vec.y;
		angle = vec.z;
	}
	
	private static void drawc(char c){
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
	
	public static class Line{
		public float x1, y1, x2, y2;
		
		public Line(float x1, float y1, float x2, float y2){
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}
}
