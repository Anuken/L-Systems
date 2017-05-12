package io.anuke.lsystem;

import static io.anuke.lsystem.Vars.control;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.TextField;
import io.anuke.ucore.scene.ui.TextField.TextFieldStyle;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.util.Strings;
import io.anuke.ucore.util.Timers;

public class UI extends SceneModule{
	Table ruletable;
	
	@Override
	public void init(){

		build.begin();
		
		new table(){{
			atop();
			aleft();
			new label(()->{
				return Gdx.graphics.getFramesPerSecond() + " FPS";
			}).left();
			
			row();
			
			new label(()->{
				return control.getCharacters() + " chars";
			}).left();
			
		}}.end();
		
		new table(){{
			new label(()->{
				return control.isLoading() ? "Loading."+new String(new char[(int)(Timers.time()/12)%4]).replace('\u0000', '.') : "";
			});
		}}.end();;
		
		new table(){{
			atop();
			aright();
			new table(){{
				get().background("button");
				get().pad(10);
				
				new label("Axiom: ").right();
				get().addField("X", c->{
					if(!c.isEmpty())
					control.setAxiom(c.toUpperCase());
				});
				
				row();
				add().height(10);
				row();
				
				new label("Rules:").colspan(2);
				
				row();
				
				ruletable = new Table();
				add(ruletable).colspan(2);
				
				row();
				
				new button("+", ()->{
					HashMap<Character, String> rules = control.rules();
					rules.put('?', "");
					
					updateRules();
					
				}).colspan(2).fillX();
			}}.end();
		}}.end();
		
		new table(){{
			abottom();
			aright();
			new table(){{
				get().pad(20);
				get().background("button");
				
				new label("Iterations: ");
				get().addField("6", s->{
					int out = Strings.parseInt(s);
					if(out != Integer.MIN_VALUE){
						control.setIterations(out);
					}
				});
				
				row();
				
				new label("Angle: ");
				get().addField("25", s->{
					int out = Strings.parseInt(s);
					if(out != Integer.MIN_VALUE){
						control.setAngle(out);
					}
				});
				
				row();
				
				new label("Length: ");
				get().addField("4.0", s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NaN){
						control.setLength(out);
					}
				});
				
				row();
				
				new label("Thickness: ").padBottom(20);
				get().addField("1.0", s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NaN){
						Draw.thick(out);
					}
				}).padBottom(20);
				
				row();
				

				new label("Sway Scale: ");
				get().addField("2.0", s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NaN){
						control.setSwayScale(out);
					}
				});
				
				row();
				
				new label("Sway Phase: ");
				get().addField("15.0", s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NaN){
						control.setSwayPhase(out);
					}
				});
				
				row();
				
				new label("Sway Space: ");
				get().addField("1.0", s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NaN){
						control.setSwaySpace(out);
					}
				});
				
				row();
				
			}}.end();
		}}.end();
		
		updateRules();
		
		build.end();
	}
	
	private void updateRules(){
		ruletable.clearChildren();
		
		HashMap<Character, String> rules = control.rules();
		
		for(Character c : rules.keySet()){
			TextField field = ruletable.newField(c+"", s->{
				if(s.isEmpty()) return;
				rules.put(s.toUpperCase().toCharArray()[0], rules.get(c));
				control.generate();
			});
			
			field.setMaxLength(1);
			field.setStyle(new TextFieldStyle(field.getStyle()));
			field.getStyle().fontColor = Color.YELLOW;
			
			ruletable.add(field).width(35).padRight(14).grow();
			
			ruletable.addField(rules.get(c), s->{
				rules.put(c, s.toUpperCase());
				control.generate();
			}).grow().minWidth(240);
			
			ruletable.addButton("-", ()->{
				rules.remove(c);
				updateRules();
				control.generate();
			}).grow();
			
			ruletable.row();
		}
		ruletable.pack();
	}
	
}
