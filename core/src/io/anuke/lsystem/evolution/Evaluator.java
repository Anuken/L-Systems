package io.anuke.lsystem.evolution;

import com.badlogic.gdx.utils.Array;

import io.anuke.lsystem.evolution.LProcessor.Line;


public enum Evaluator{
	surface {
		@Override
		public float getScore(int branches, Array<Line> lines){
			float surface = 0f;
			//float scaleback = 0f;
			int volume = lines.size;
			
			for(Line line : lines){
				surface += Math.abs(line.y1 - line.y2);
				
				if(line.y1 < 0 || line.y2 < 0 || line.y1 > 300f || line.y2 > 300f){
					return -1;
				}
			}
			
			return surface*4f - volume - branches;
		}
	};
	
	public abstract float getScore(int branches, Array<Line> lines);
}
