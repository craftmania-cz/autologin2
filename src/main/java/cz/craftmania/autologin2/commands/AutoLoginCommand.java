package cz.craftmania.autologin2.commands;

import cz.craftmania.autologin2.AutoLogin;
import cz.craftmania.autologin2.utils.ChatInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AutoLoginCommand extends Command {

    public AutoLoginCommand() {
        super("autologin", "autologin.admin", "al");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length <= 0) {
            ChatInfo.error(sender, "Použití: /autologin check/add/remove [nick]");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "check":
                if (args.length < 2) {
                    ChatInfo.error(sender, "Použítí: /autologin check <hráč>");
                    break;
                }
                String targetPlayer1 = args[1];
                if (AutoLogin.getSqlManager().isInDatabase(targetPlayer1)) {
                    ChatInfo.info(sender, targetPlayer1 + " hraje za originální účet.");
                    break;
                }
                ChatInfo.info(sender, targetPlayer1 + " se nikdy nepřipojil za originální účet.");
                break;
            case "add":
                if (args.length < 2) {
                    ChatInfo.error(sender, "Použítí: /autologin add <hráč>");
                    break;
                }
                String targetPlayer2 = args[1];
                if (AutoLogin.getSqlManager().isInDatabase(targetPlayer2)) {
                    ChatInfo.error(sender, "Hráč " + targetPlayer2 + " již je v databázi.");
                    break;
                }
                if (!AutoLogin.getLoginManager().isOriginal(targetPlayer2)) {
                    ChatInfo.error(sender, "Nick " + targetPlayer2 + " není originální.");
                    break;
                }
                AutoLogin.getSqlManager().insertData(targetPlayer2);
                ChatInfo.success(sender, targetPlayer2 + " byl přidán do databáze, již se nepřipojí jako warez.");
                if (AutoLogin.getInstance().getProxy().getPlayer(targetPlayer2) != null) {
                    AutoLogin.getInstance().getProxy().getPlayer(targetPlayer2).disconnect(TextComponent.fromLegacyText("Byl jsi přidán jako originálka, připoj se znovu.", ChatColor.RED));
                }
                break;
            case "remove":
                if (args.length < 2) {
                    ChatInfo.error(sender, "Použítí: /autologin remove <hráč>");
                    break;
                }
                String targetPlayer3 = args[1];
                if (!AutoLogin.getSqlManager().isInDatabase(targetPlayer3)) {
                    ChatInfo.error(sender, "Hráč " + targetPlayer3 + " není v databázi.");
                    break;
                }
                AutoLogin.getSqlManager().remove(targetPlayer3);
                ChatInfo.success(sender, targetPlayer3 + " byl odebrán z databáze.");
                break;
        }
    }
}
