package io.anuke.lsystem;

import java.util.HashMap;

public class LSystemTask{
	private static final int maxiters = 700;
	private StringBuilder current;
	private StringBuilder out;
	private int iteration;
	private int index;
	private boolean done = false;
	
	private String axiom;
	private int iterations;
	private HashMap<Character, String> rules;
	
	public void reset(String axiom, int iterations, HashMap<Character, String> rules){
		this.axiom = axiom;
		this.iterations = iterations;
		this.rules = rules;
		
		current = new StringBuilder(axiom);
		out = new StringBuilder();
		iteration = 0;
		index = 0;
		done = false;
	}
	
	public boolean isDone(){
		return done;
	}
	
	public StringBuilder getCurrent(){
		return out;
	}
	
	public void iterate(){
		int i = 0;
		
		outer:
		for(; iteration < iterations; iteration ++){
			if(index == 0)
				out.setLength(0);
			
			for(; index < current.length(); index ++){
				char c = current.charAt(index);
				
				if(rules.get(c) != null){
					out.append(rules.get(c));
				}else{
					out.append(c);
				}
				
				i ++;
				
				if(i > maxiters){
					index ++;
					break outer;
				}
			}
			
			index = 0;
			current = new StringBuilder(out);
			
			if(iteration == iterations-1){
				done = true;
			}
		}
	}
}
