package io.anuke.lsystem.evolution;

import java.util.HashMap;

import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.lsystem.LSystemData;
import io.anuke.ucore.util.Mathf;

public class Evolver{
	int variants = 6;
	int generations = 50;
	int maxMutations = 3;
	char[] insertChars = { '+', '-', 'F', 'X' };
	Evaluator eval = Evaluator.leafcount;

	int iterations = 3;
	String axiom = "X";
	HashMap<Character, String> current = new HashMap<Character, String>();
	float currentSpace = 15f;
	float currentScore = 0f;

	public LSystemData evolve(){
		current.put('X', "XF");
		current.put('F', "F");
		
		for(int g = 0; g < generations; g++){
			UCore.log("Generation " + g + ": best score currently " + currentScore);
			
			HashMap<Character, String> bestTree = current;
			float bestSpace = currentSpace;
			float bestScore = currentScore;

			for(int i = 0; i < variants; i++){
				HashMap<Character, String> mutation = mutateCurrent();
				float mutSpace = currentSpace + Mathf.range(5f);

				LTree tree = LProcessor.getLines(axiom, mutation, iterations, mutSpace);

				float score = eval.getScore(tree);
				
				if(score < 0){
					continue;
				}
				
				UCore.log("Score of " + mutation.toString() + ": " + score);
				
				if(score > bestScore){
					UCore.log("Selected successor: " + bestTree.toString());
					bestTree = mutation;
					bestScore = score;
					bestSpace = mutSpace;
				}
			}

			current = bestTree;
			currentSpace = bestSpace;
			currentScore = bestScore;
		}
		
		return new LSystemData(axiom, current, iterations, 0, 0, 1f, 4f, currentSpace, Mathf.random(1f, 2f), Hue.random(), Hue.random());
	}

	HashMap<Character, String> mutateCurrent(){
		HashMap<Character, String> map = (HashMap<Character, String>) current.clone();

		for(Character c : map.keySet()){
			map.put(c, mutateString(map.get(c)));
		}

		return map;
	}

	String mutateString(String in){
		int mutations = Mathf.random(1, maxMutations);
		StringBuilder current = new StringBuilder(in);

		for(int i = 0; i < mutations; i++){
			int rand = Mathf.random(0, insertChars.length + 2);
			
			//delete a random character
			if(Mathf.chance(0.2) && current.length() > 5){
				int idx = Mathf.random(current.length()-1);
				current.deleteCharAt(idx);
				continue;
			}
			
			if(rand < insertChars.length){ //insert a random character
				current.insert(Mathf.random(0, current.length() - 1), insertChars[rand]);
			}else if(rand <= insertChars.length + 1 && current.length() > 1){

				current.insert(Mathf.random(0, current.length() - 1), '-');
				current.insert(Mathf.random(0, current.length() - 1), '+');
				
			}else if(current.length() > 4){
				int idx = Mathf.random(0, current.length() - 3);
				current.insert(idx, '[');
				current.insert(Mathf.random(idx + 1, current.length() - 1), ']');
			}
		}

		return current.toString();
	}
}
