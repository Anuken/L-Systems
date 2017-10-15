package io.anuke.lsystem.evolution;

import java.util.HashMap;

import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.lsystem.LSystemData;
import io.anuke.ucore.util.Mathf;

public class Evolver {
	public int variants = 10;
	public int generations = 40;
	public int maxMutations = 5;
	public int iterations = 3;
	public String axiom = "X";
	public IEvaluator eval = Evaluator.leafcount;
	public boolean debug = false;
	
	char[] insertChars = { '+', '-', 'F', 'X' };

	HashMap<Character, String> current = new HashMap<Character, String>();
	float currentSpace = 35f;
	float currentScore = 0f;

	boolean limitrulesize = false;
	int maxrulesize = 15;
	
	public LSystemData evolve(IEvaluator eval) {
		this.eval = eval;
		return evolve();
	}

	public LSystemData evolve() {
		current.put('X', "XF");
		current.put('F', "F");

		for (int g = 0; g < generations; g++) {
			if(debug)
				UCore.log("Generation " + g + ": best score currently " + currentScore);

			HashMap<Character, String> bestTree = current;
			float bestSpace = currentSpace;
			float bestScore = currentScore;

			for (int i = 0; i < variants; i++) {
				HashMap<Character, String> mutation = mutateCurrent();
				float mutSpace = currentSpace + Mathf.range(5f);

				LTree tree = LProcessor.getTree(axiom, mutation, iterations, mutSpace);

				float score = eval.getScore(tree);

				if (score < 0) {
					continue;
				}
				
				if(debug)
					UCore.log("Score of " + mutation.toString() + ": " + score);

				if (score > bestScore) {
					if(debug)
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

		return new LSystemData(axiom, current, iterations, 0, 0, 1f, 4f, currentSpace, 1f,
				Hue.random(), Hue.random());
	}

	HashMap<Character, String> mutateCurrent() {
		HashMap<Character, String> map = (HashMap<Character, String>) current.clone();

		for (Character c : map.keySet()) {
			map.put(c, mutateString(map.get(c)));
		}

		return map;
	}

	String mutateString(String in) {
		int mutations = Mathf.random(1, maxMutations);
		StringBuilder current = new StringBuilder(in);

		for (int i = 0; i < mutations; i++) {

			int rand = Mathf.random(0, insertChars.length + 5);

			// delete a random character
			if (Mathf.chance(0.2) && current.length() > 5) {
				int idx = Mathf.random(current.length() - 1);
				current.deleteCharAt(idx);
				continue;
			}

			if (limitrulesize && current.length() > maxrulesize) {
				// can only change a character, so just delete here, and let other code add
				if(rand < insertChars.length) {
					int idx = Mathf.random(current.length() - 1);
					current.deleteCharAt(idx);
				} else if ((rand <= insertChars.length + 1 && current.length() > 1) || 
						(current.length() > 4)){
					int idx = Mathf.random(current.length() - 1);
					current.deleteCharAt(idx);
					
					idx = Mathf.random(current.length() - 1);
					current.deleteCharAt(idx);
				}
			}

			if (rand < insertChars.length) { // insert a random character
				current.insert(Mathf.random(0, current.length() - 1), insertChars[rand]);
			} else if (rand <= insertChars.length + 1 && current.length() > 1) {

				current.insert(Mathf.random(0, current.length() - 1), '-');
				current.insert(Mathf.random(0, current.length() - 1), '+');

			} else if (current.length() > 4) {
				int idx = Mathf.random(0, current.length() - 3);
				current.insert(idx, '[');
				current.insert(Mathf.random(idx + 1, current.length() - 1), ']');
			}

		}

		return current.toString();
	}
}
