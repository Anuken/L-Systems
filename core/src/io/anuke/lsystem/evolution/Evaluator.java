package io.anuke.lsystem.evolution;

import com.badlogic.gdx.utils.Array;

import io.anuke.lsystem.evolution.LProcessor.Line;
import io.anuke.ucore.util.GridMap;


public enum Evaluator{
	leastIntersect {
		@Override
		public float getScore(int branches, Array<Line> lines){
			GridMap<Integer> map = new GridMap<>();
			float cellsize = 3f;
			
			float surface = 0f;
			float min = 99999, max = 0;
			int volume = lines.size;
			
			for(Line line : lines){
				surface += Math.abs(line.x1 - line.x2);
				
				int cx = (int)(line.x1 / cellsize), cy = (int)(line.y1 / cellsize);
				int before = map.get(cx, cy) == null ? 0 : map.get(cx, cy);
				
				min = Math.min(line.x1, min);
				max = Math.max(line.x1, max);
				
				if(Math.abs(line.x1) > 100 || line.y1 > 100) return -1;
				
				map.put(cx, cy, before + 1);
				
				if(before > 1){
					surface -= (before - 1)/10f;
				}
				
				if(line.y1 < 0 || line.y2 < 0 || line.y1 > maxY || line.y2 > maxY){
					return -1;
				}
			}
			
			return surface*4f - volume - Math.abs(max - min);
		}
	},
	xSurface{
		@Override
		public float getScore(int branches, Array<Line> lines){
			
			float surface = 0f;
			
			for(Line line : lines){
				surface += Math.abs(line.x1 - line.x2);
				
				if(line.y1 < 0 || line.y2 < 0 || line.y1 > maxY || line.y2 > maxY){
					return -1;
				}
			}
			
			return surface*4f - lines.size;
		}
	},
	ySurface{
		@Override
		public float getScore(int branches, Array<Line> lines){
			
			float surface = 0f;
			
			for(Line line : lines){
				surface += Math.abs(line.y1 - line.y2);
				
				if(line.y1 < 0 || line.y2 < 0 || line.y1 > maxY || line.y2 > maxY){
					return -1;
				}
			}
			
			return surface*4f - lines.size;
		}
	};
	static float maxY = 300f;
	
	public abstract float getScore(int branches, Array<Line> lines);
}
