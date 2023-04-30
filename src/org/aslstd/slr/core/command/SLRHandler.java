package org.aslstd.slr.core.command;

import java.util.ArrayList;
import java.util.List;

import org.aslstd.api.bukkit.command.BasicCommand;
import org.aslstd.api.bukkit.command.BasicCommandHandler;
import org.aslstd.api.bukkit.command.interfaze.ECommand;
import org.aslstd.api.bukkit.entity.util.EntityUtil;
import org.aslstd.slr.api.menu.MenuMode;
import org.aslstd.slr.api.menu.SellGUI;
import org.aslstd.slr.core.SLR;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SLRHandler extends BasicCommandHandler {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		final List<String> list = new ArrayList<>();
		if (command.getName().equalsIgnoreCase("aseller")) {
			if (args.length == 1) {
				list.add(getDefaultCommand().getName());
				for (final ECommand cmd : getRegisteredCommands())
					list.add(cmd.getName());

				return list;
			}
		}
		return null;
	}

	public SLRHandler() {
		super(SLR.instance(), "aseller");
		registerDefaultHelp()
		.registerCommand(new BasicCommand(this, "open", (s,args) -> { // /aseller open <mode> <player> [seller-id]
			if (args.length < 1)
				return "Provide a player name for this command /aseller <player>";

			final Player p = EntityUtil.getOnlinePlayer(args[0]);
			if (p == null)
				return "&cPlayer " + args[0] + " is not online or not exists";
			new SellGUI(p, MenuMode.getRegisteredModes().get("seller"));
			return null;
		}));
	}


}
