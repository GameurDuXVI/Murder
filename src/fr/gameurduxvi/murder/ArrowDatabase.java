package fr.gameurduxvi.murder;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ArrowDatabase {
	private Entity arrow;
	private Player player;
	
	public ArrowDatabase(Entity arrow, Player player) {
		this.arrow = arrow;
		this.player = player;
	}
	
	public Entity getArrow() {
		return arrow;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setArrow(Entity arrow) {
		this.arrow = arrow;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
}
