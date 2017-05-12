package io.anuke.lsystem;

import io.anuke.ucore.modules.Core;

public class LSystems extends Core {
	
	@Override
	public void init(){
		add(Vars.control = new Control());
		add(Vars.ui = new UI());
	}
	
}
