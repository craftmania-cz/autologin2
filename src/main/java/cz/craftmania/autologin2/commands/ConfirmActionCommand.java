package cz.craftmania.autologin2.commands;

import cz.craftmania.autologin2.utils.actions.ConfirmAction;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;

public class ConfirmActionCommand extends Command {

    public ConfirmActionCommand() {
        super("confirmaction", null);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length < 1) return;
        String ID = args[0];

        Optional<ConfirmAction.Action> optionalAction = ConfirmAction.getAction(player, ID);
        if (!optionalAction.isPresent()) return;
        ConfirmAction.Action action = optionalAction.get();

        action.run();
        return;
    }
}
