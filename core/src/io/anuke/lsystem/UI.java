package io.anuke.lsystem;

import static io.anuke.lsystem.Vars.control;

import java.util.HashMap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.*;
import io.anuke.ucore.scene.ui.TextField.TextFieldStyle;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.scene.utils.Elements;
import io.anuke.ucore.util.Strings;

public class UI extends SceneModule{
	Table ruletable;
	Dialog colordialog;
	Dialog importdialog, exportdialog;
	ColorPicker picker;
	boolean visible = true;
	
	@Override
	public void init(){
		Dialog.closePadR -= 1;
		skin.font().setUseIntegerPositions(true);
		
		picker = new ColorPicker();
		picker.colorChanged(c->{
			//what
		});
		
		colordialog = new Dialog("Select Color");
		colordialog.content().add(picker).pad(10);
		colordialog.addCloseButton();
		colordialog.setCentered(false);
		colordialog.setMovable(true);
		
		colordialog.getButtonTable().addButton("OK", ()->{
			colordialog.hide();
		}).size(90, 40).padBottom(2);
		
		importdialog = new Dialog("Import", "dialog");
		importdialog.addCloseButton();
		
		exportdialog = new Dialog("Export", "dialog");
		exportdialog.addCloseButton();
		
		importdialog.shown(()->{
			scene.setKeyboardFocus(null);
		});
		
		exportdialog.shown(()->{
			scene.setKeyboardFocus(null);
		});
		
		
		//import
		build.begin(importdialog.content());
		importdialog.content().pad(10);
		
		new label("Path: ").left();
		
		new field(control.getImportFilePath(), f->{
			control.setImportFilePath(f);
		}).width(300f);
		
		importdialog.content().row().padTop(10);
		
		new label("Filename: ");
		
		new field("out", f->{
			control.setImportFilename(f);
		}).width(300f);
		
		importdialog.content().row();
		
		new button("Import", ()->{
			control.importFile();
			importdialog.hide();
		}).colspan(2).padTop(10).fillX();
		build.end();
		
		//export
		build.begin(exportdialog.content());
		exportdialog.content().pad(10);
		
		new label("Exporting").colspan(2).padBottom(10);
		
		exportdialog.content().row();
		
		new label("Path: ").left();
		
		new field(control.getExportFilePath(), f->{
			control.setExportFilePath(f);
		}).width(300f);
		
		exportdialog.content().row().padTop(10);
		
		new label("Filename: ");
		
		new field("out", f->{
			control.setExportFilename(f);
		}).width(300f);
		
		exportdialog.content().row();
		
		new button("Export", ()->{
			control.exportFile();
			exportdialog.hide();
		}).colspan(2).padTop(10).fillX();
		build.end();
		
		build.begin();
		
		new table(){{
			atop();
			aleft();
			
			new label(()->Gdx.graphics.getFramesPerSecond() + " FPS").left();
			row();
			
			new label(()->{
				return control.getCharacters() + " chars";
			}).left();
			row();
			
			new label(()->{
				if(control.getCharacters() < 50) {
					return "Chars: " + control.getTask().getCurrent();
				}else {
					return "(too long)";
				}
				}).left();
			row();
			
			new label(()->("<L> Sorting: " + (control.isSorting() ? "enabled" : "disabled"))).left();
			row();
			
			new label(()->"<M> Sort mode: " + (control.sortMode() ? "ascending" : "descending")).left();
			row();
			
			new label(()->"<C> Color boost: " + (control.colorBoost() ? "enabled" : "disabled")).left();
			row();
			
			new label("<Q> to toggle UI").left();
			row();
			
			new label("<P> to take a screenshot").left();
			
		}}.end();
		
		new table(){{
			new label(()->{
				return control.isLoading() ? "Loading."+new String(new char[(int)(Timers.time()/12)%4]).replace('\u0000', '.') : "";
			});
		}}.end();;
		
		new table(){{
			abottom();
			aright();
			
			if(Gdx.app.getType() != ApplicationType.WebGL){
				
				new button("Import", ()->{
					importdialog.show();
				}).fillX();
				
				row();
				new button("Export", ()->{
					exportdialog.show();
				}).fillX();
				row();
			}
			
			new table("button"){{
				get().pad(10);
				
				new label("Axiom: ").right();
				get().addField(control.getAxiom(), c->{
					if(!c.isEmpty()){
						control.setAxiom(c.toUpperCase());
					}
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
			aleft();
			
			new table("button"){{
				get().pad(20);
				
				TextButton start = new TextButton("Select");
				start.add(new ColorImage(control.startColor())).size(26);
				start.clicked(()->{
					picker.setColor(control.startColor());
					colordialog.show();
				});
				
				new label("Start Color: ");
				add(start).grow();
				
				row();
				
				TextButton end = new TextButton("Select");
				end.add(new ColorImage(control.endColor())).size(26);
				end.clicked(()->{
					picker.setColor(control.endColor());
					colordialog.show();
				});
				
				new label("End Color: ").padBottom(10);
				add(end).grow().padBottom(10);
					
				row();
				
				new label("Iterations: ");
				get().addField(""+control.getIterations(), s->{
					int out = Strings.parseInt(s);
					if(out != Integer.MIN_VALUE){
						control.setIterations(out);
					}
				});
				
				row();
				
				new label("Angle: ");
				get().addField(""+control.getAngle(), s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NEGATIVE_INFINITY){
						control.setAngle(out);
					}
				});
				
				row();
				
				new label("Length: ");
				get().addField(""+control.getLength(), s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NEGATIVE_INFINITY){
						control.setLength(out);
					}
				});
				
				row();
				
				new label("Thickness: ").padBottom(20);
				get().addField(""+control.getThickness(), s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NEGATIVE_INFINITY){
						Draw.thick(out);
					}
				}).padBottom(20);
				
				row();
				

				new label("Sway Scale: ");
				get().addField(""+control.getSwayScale(), s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NEGATIVE_INFINITY){
						control.setSwayScale(out);
					}
				});
				
				row();
				
				new label("Sway Phase: ");
				get().addField(""+control.getSwayPhase(), s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NEGATIVE_INFINITY){
						control.setSwayPhase(out);
					}
				});
				
				row();
				
				new label("Sway Space: ");
				get().addField(""+control.getSwaySpace(), s->{
					float out = Strings.parseFloat(s);
					if(out != Float.NEGATIVE_INFINITY){
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
		
		//TODO have specific references to each character to prevent duplication bugs and such
		HashMap<Character, String> rules = control.rules();
		
		for(Character c : rules.keySet()){
			TextField field = Elements.newField(c+"", s->{
				if(s.isEmpty()) return;
					rules.put(s.toUpperCase().toCharArray()[0], rules.get(c));
				control.generate();
			});
			
			field.setMaxLength(1);
			field.setStyle(new TextFieldStyle(field.getStyle()));
			field.getStyle().fontColor = Color.YELLOW;
			
			ruletable.add(field).width(35).padRight(14).grow();
			
			ruletable.addField(rules.get(c), s->{
				rules.put(field.getText().toUpperCase().toCharArray()[0], s.toUpperCase());
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
	
	@Override
	public void update(){
		if(Inputs.keyUp(Keys.Q))
			visible = !visible;
		
		if(Inputs.buttonUp(Buttons.LEFT) && !hasMouse()){
			scene.setScrollFocus(null);
			scene.setKeyboardFocus(null);
		}
		
		if(visible){
			scene.act();
			scene.draw();
		}
	}
	
	public void showMessage(String string){
		showMessage(string, Color.WHITE);
	}
	
	public void showMessage(String string, Color color){
		Table table = fill();
		table.top();
		table.add(string, color);
		table.addAction(Actions.sequence(Actions.fadeOut(8f, Interpolation.fade), Actions.removeActor()));
	}
	
}
