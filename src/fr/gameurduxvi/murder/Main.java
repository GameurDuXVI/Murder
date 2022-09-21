package fr.gameurduxvi.murder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin implements Listener {
	private boolean inGame = false;
	private Player murder = null;
	private Player detective = null;
	
	private Location tpLocation;
	
	private Location pos1A;
	private Location pos2A;
	private Location pos1B;
	private Location pos2B;
	private List<Location> goldLocations = new ArrayList<>();
	
	public ScoreboardManager manager;
	
	private ArrayList<ArrowDatabase> arrowDatabase = new ArrayList<>();
	
	
	
	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage("Loading Event Murder");
		
		manager = Bukkit.getScoreboardManager();
		
		getCommand("start").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);
		
		tpLocation = new Location(Bukkit.getWorld("world"), 3.5, 70, 23.5, 180, 0);

		goldLocations.add(new Location(Bukkit.getWorld("world"), 0, 0, 0));
		
		
		pos1A = new Location(Bukkit.getWorld("world"), -30, 69, 24);
		pos2A = new Location(Bukkit.getWorld("world"), 36, 78, -16);
		
		pos1B = new Location(Bukkit.getWorld("world"), -30, 69, 24);
		pos2B = new Location(Bukkit.getWorld("world"), 36, 77, -16);
		refreschScoreboard();
		
		Collection<Entity> mobs = pos1A.getWorld().getNearbyEntities(pos1A, 100, 100, 100);
		for(Entity e: mobs) {
			if((e instanceof Item)) {
				e.remove();
			}
		}
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("Unloading Event Murder");	
		inGame = false;
		refreschScoreboard();
	}
	
	public Location pickLocation(Location loc1, Location loc2) {
		int diffX = getHighNumber((int) loc1.getX(), (int) loc2.getX()) - getLowNumber((int) loc1.getX(), (int) loc2.getX());
		int diffY = getHighNumber((int) loc1.getY(), (int) loc2.getY()) - getLowNumber((int) loc1.getY(), (int) loc2.getY());
		int diffZ = getHighNumber((int) loc1.getZ(), (int) loc2.getZ()) - getLowNumber((int) loc1.getZ(), (int) loc2.getZ());
		
		Random rand = new Random();
		
		int randX = rand.nextInt(diffX);
		int randY = rand.nextInt(diffY);
		int randZ = rand.nextInt(diffZ);
		
		int X = getLowNumber((int) loc1.getX(), (int) loc2.getX()) + randX;
		int Y = getLowNumber((int) loc1.getY(), (int) loc2.getY()) + randY;
		int Z = getLowNumber((int) loc1.getZ(), (int) loc2.getZ()) + randZ;
		
		Location finalLoc = new Location(loc1.getWorld(), X, Y, Z);
		
		return finalLoc;
	}
	
	public int getHighNumber(int num1, int num2) {
		if(num1 >= num2) {
			return num1;
		}
		else {
			return num2;
		}
	}
	
	public int getLowNumber(int num1, int num2) {
		if(num1 <= num2) {
			return num1;
		}
		else {
			return num2;
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), 3.5, 70, 23.5, 180, 0));
		if(inGame) {
			event.getPlayer().setGameMode(GameMode.SPECTATOR);
		}
		else {
			event.getPlayer().setGameMode(GameMode.ADVENTURE);
		}
		//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "skin set " + event.getPlayer().getName() + " Spooky");
		refreschScoreboard();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		refreschScoreboard();
	}
	
	@SuppressWarnings("deprecation")
	public void refreschScoreboard() {
		
		Scoreboard board = manager.getMainScoreboard();
		for(Team loopTeam: board.getTeams()) {
			if(loopTeam.getName().equals("Invisible")) {
				loopTeam.unregister();
			}
		}
		Team nameless = board.registerNewTeam("Invisible");
		nameless.setNameTagVisibility(NameTagVisibility.NEVER);
		/*for(Player p: Bukkit.getOnlinePlayers()) {
			nameless.addPlayer(p);
			p.setScoreboard(board); 
		}*/
		
		//if(true) return;
		for(Player p: Bukkit.getOnlinePlayers()) {
			nameless.addPlayer(p);
			for(Team loopTeam: board.getTeams()) {
				if(loopTeam.getName().equals(p.getName())) {
					loopTeam.unregister();
				}
			}
			Team team = board.registerNewTeam(p.getName());
			team.setNameTagVisibility(NameTagVisibility.NEVER);
			team.addPlayer(p);
			
			for(Objective loopObjective: board.getObjectives())
			{
				if(loopObjective.getName().equals(p.getName())) {
					loopObjective.unregister();
				}
			}
			Objective objective = board.registerNewObjective(p.getName(), "dummy");
			
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			
			objective.setDisplayName("§4§lMurder");
			
			if(inGame) {
				Score score1 = objective.getScore("§1");
				Score score2 = objective.getScore("Joueurs en vie: §6" + getAlive().size());
				Score score3 = objective.getScore("§3");
				
				score1.setScore(3);
				score2.setScore(2);
				score3.setScore(1);
			}
			else {
				Score score1 = objective.getScore("§1");
				Score score2 = objective.getScore("Joueurs connectés: " + Bukkit.getOnlinePlayers().size());
				Score score3 = objective.getScore("§3");
				
				score1.setScore(3);
				score2.setScore(2);
				score3.setScore(1);
			}
		    p.setScoreboard(board); 
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String mes, String[] arg) {
		if(sender instanceof Player) {
			if(cmd.getName().equals("start")) {
				Bukkit.broadcastMessage("§6Démarrage de la partie");
				inGame = true;
				Random rand = new Random();
				List<Player> list = new ArrayList<>();
				
				for(Player p: Bukkit.getOnlinePlayers()) {
					list.add(p);
					p.setExp(0);
				}
				
				murder = list.get(rand.nextInt(list.size()));
				
				list.remove(murder);
				
				detective = list.get(rand.nextInt(list.size()));
				
				for(Player p: Bukkit.getOnlinePlayers()) {
					p.getInventory().clear();
					p.setGameMode(GameMode.ADVENTURE);			
					p.teleport(tpLocation);
				}
				Bukkit.broadcastMessage("§6Les rôles vont être attribués dans 10 secondes!");
				
				Bukkit.getScheduler().runTaskLater(this, new Runnable() {
					
					@Override
					public void run() {
						for(Player p: Bukkit.getOnlinePlayers()) {
							p.getInventory().clear();
							if(p.equals(murder)) {
								p.sendMessage("§4Vous êtes le murder !");
								p.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
							}
							else if(p.equals(detective)) {
								p.sendMessage("§9Vous êtes le détective !");
								ItemStack bow = new ItemStack(Material.BOW);
								ItemMeta bowMeta = bow.getItemMeta();
								bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
								bow.setItemMeta(bowMeta);
								p.getInventory().addItem(bow);
								p.getInventory().addItem(new ItemStack(Material.ARROW));								
							}
							else {
								p.sendMessage("§aVous êtes innocent !");
							}
						}
						startTimer();
					}
				}, 200);
				refreschScoreboard();
				
				
				
				Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
					
					@Override
					public void run() {						
						Location loc1 = pickLocation(pos1A, pos2A);
						Location loc2 = pickLocation(pos1B, pos2B);
						
						loc1.getWorld().dropItem(loc1, new ItemStack(Material.DIAMOND));
						
						loc2.getWorld().dropItem(loc2, new ItemStack(Material.DIAMOND));
					}
				}, 200, 100);
			}
		}
		return false;
	}
	
	@EventHandler
    public void OnPlayerPickup(PlayerPickupItemEvent event) {
		if(inGame) {
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				
				@Override
				public void run() {
					if(!event.getPlayer().equals(murder) && !event.getPlayer().equals(detective)) {
						int diamond = 0;
						for(ItemStack item: event.getPlayer().getInventory().getContents()) {
							try {
								if(item.getType().equals(Material.DIAMOND)) {
									diamond += item.getAmount();
								}
							}catch (Exception e) {
							}
							
						}
						if(event.getPlayer().getInventory().contains(Material.BOW)) {
							event.getPlayer().getInventory().remove(Material.DIAMOND);
						}
						else {
							if(diamond >= 10) {
								event.getPlayer().getInventory().remove(Material.DIAMOND);
								ItemStack bow = new ItemStack(Material.BOW);
								ItemMeta bowMeta = bow.getItemMeta();
								bowMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
								bow.setItemMeta(bowMeta);
								event.getPlayer().getInventory().addItem(bow);
								event.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW));
							}
						}
					}
				}
			}, 1);
		}
	}    
	
	public void startTimer() {
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			
			@Override
			public void run() {
				if(inGame) {
					if(getAlive().contains(murder) && getAlive().size() == 1) {
						Bukkit.broadcastMessage("Le Murder a gagné !");
						Bukkit.broadcastMessage("§7" + murder.getName() + " était le Murder");
						inGame = false;
					}else if(!getAlive().contains(murder)) {
						Bukkit.broadcastMessage("Les Innocents ont gagné !");
						Bukkit.broadcastMessage("§7Joueurs en vie: ");
						for(Player p: getAlive()) {
							String message = "";
							if(p.equals(detective)) {
								message = " §7(§bDétective§7)";
							}
							Bukkit.broadcastMessage("§7>> " + p.getName() + message);
						}
						inGame = false;
					}
				}
			}
		}, 0, 1);
	}
	
	public List<Player> getAlive(){
		List<Player> list = new ArrayList<>();
		for(Player p: Bukkit.getOnlinePlayers()) {
			if(p.getGameMode().equals(GameMode.ADVENTURE)) {
				list.add(p);
			}
		}
		return list;
	}
	
	private boolean murderCanLaunch = true;
	@EventHandler
	public void OnInteract(PlayerInteractEvent event) {
		if(inGame) {
			if(event.getAction() == Action.RIGHT_CLICK_AIR) {
				if(event.getPlayer().equals(murder)) {
					if(murderCanLaunch) {
						event.getPlayer().launchProjectile(Snowball.class);
						murderCanLaunch = false;
						Bukkit.getScheduler().runTaskLater(this, new Runnable() {
							
							@Override
							public void run() {
								murderCanLaunch = true;
							}
						}, 300);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	
	public void deadSound() {
		for(Player p: Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), Sound.BAT_HURT, 5, 1);
		}
	}
	
	@EventHandler
    public void arrowShot(EntityShootBowEvent e){
		if(!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		arrowDatabase.add(new ArrowDatabase(e.getProjectile(), p));
		p.getInventory().remove(Material.ARROW);
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			
			@Override
			public void run() {
				try {
					int i = 0;
					for(ArrowDatabase data: arrowDatabase) {
						if(data.getArrow().equals(e.getProjectile())) {
							arrowDatabase.remove(arrowDatabase.get(i));
						}
						i++;
					}
				} catch (Exception e2) {
				}
			}
		}, 100);
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				p.getInventory().addItem(new ItemStack(Material.ARROW));
			}
		}, 120);
	}
	
	@EventHandler
	public void onFoodLevelChange(final FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamageEvent(EntityDamageEvent e) {
		if(!e.getCause().equals(DamageCause.PROJECTILE) || !e.getCause().equals(DamageCause.ENTITY_ATTACK))
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamageEvent(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			if(inGame) {
				Player p = (Player) e.getEntity();
				
				if(e.getDamager().getName().equals("Arrow")) {
					p.setGameMode(GameMode.SPECTATOR);
					deadSound();
					Player killer = null;
					
					for(ArrowDatabase data: arrowDatabase) {
						if(e.getDamager().getUniqueId().equals(data.getArrow().getUniqueId())) {
							killer = data.getPlayer();
						}
					}
					p.sendMessage("§cVous êtes mort par " + killer.getName() + " !");
				}
				else if(e.getDamager().getName().equals("Snowball")) {
					p.setGameMode(GameMode.SPECTATOR);
					deadSound();
					p.sendMessage("§cVous êtes mort par " + murder.getName() + " !");
				}
				else if(e.getDamager().equals(murder)) {
					if(murder.getInventory().getItemInHand().getType().equals(Material.STONE_SWORD)) {
						p.setGameMode(GameMode.SPECTATOR);
						deadSound();
						p.sendMessage("§cVous êtes mort par " + murder.getName() + " !");
					}
				}
				else {
					e.setCancelled(true);
				}
			}
		}
		refreschScoreboard();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		Bukkit.broadcastMessage(colorize("§8" + event.getPlayer().getName() + " &7>> &f" + event.getMessage()));
	}
	
	public String colorize(String string) {
		return ChatColor.translateAlternateColorCodes('&', "" + string);
	}
}
