package cz.craftmania.autologin2.commands;

import cz.craftmania.autologin2.AutoLogin;
import cz.craftmania.autologin2.utils.ChatInfo;
import cz.craftmania.autologin2.utils.TextComponentBuilder;
import cz.craftmania.autologin2.utils.actions.ConfirmAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AutoLoginCommand extends Command {

    public AutoLoginCommand() {
        super("autologin", null, "al");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length <= 0) {
            if (!(sender instanceof ProxiedPlayer)) return;
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (!AutoLogin.getLoginManager().isOriginal(player.getName())) {
                ChatInfo.error(player, "Tento nick není originální - nemůžeš si zapnout AutoLogin.");
                return;
            }

            if (AutoLogin.getSqlManager().isInDatabase(player.getName())) {
                ChatInfo.error(player, "Již máš zapnutou funkci AutoLogin.");
                return;
            }

            try {
                ConfirmAction.Action action = new ConfirmAction.Builder()
                        .setPlayer(player)
                        .generateIdentifier()
                        .addComponent(a -> new TextComponentBuilder("&aJako originálka si můžeš zapnout funkci &eAutoLogin&a pomocí, které se nemusíš nadále přihlašovat pomocí hesla. &cTohle taky zamezí připájení za warez na tvůj účet.").getComponent())
                        .addComponent(a -> new TextComponentBuilder("§e[ Klikni zde pro zapnutí funkce AutoLogin ]").setTooltip("Klikni pro zapnutí funkce AutoLogin").setPerformedCommand(a.getConfirmationCommand()).getComponent())
                        .setDelay(30L)
                        .setRunnable(p -> {
                            AutoLogin.getSqlManager().insertData(AutoLogin.getLoginManager().getOriginalNickUUID(p.getName()), p.getName());
                            p.disconnect(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&cByl jsi zaregistrován jako originálka, připoj se znovu.")));
                        })
                        .setExpireRunnable(p -> ChatInfo.error(p, "AutoLogin zapnutí expirovalo."))
                        .build();

                action.sendTextComponents();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                ChatInfo.error(player, "Nastala chyba při inicializaci AutoLoginu.");
            }

            if (sender.hasPermission("autologin.admin"))
                ChatInfo.error(sender, "Použití: /autologin check/add/remove [nick]");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "check":
                if (!sender.hasPermission("autologin.admin")) break;
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
                if (!sender.hasPermission("autologin.admin")) break;
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

                AutoLogin.getSqlManager().insertData(AutoLogin.getLoginManager().getOriginalNickUUID(targetPlayer2), targetPlayer2);
                ChatInfo.success(sender, targetPlayer2 + " byl přidán do databáze, již se nepřipojí jako warez.");
                if (AutoLogin.getInstance().getProxy().getPlayer(targetPlayer2) != null) {
                    AutoLogin.getInstance().getProxy().getPlayer(targetPlayer2).disconnect(TextComponent.fromLegacyText("Byl jsi přidán jako originálka, připoj se znovu.", ChatColor.RED));
                }
                break;
            case "remove":
                if (!sender.hasPermission("autologin.admin")) break;
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
