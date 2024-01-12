package main.java.elementalmp4.command.nicknames;

import main.java.elementalmp4.service.NicknameService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetNicknameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String nickname = args[0];
        if (nickname.length() < 1 || nickname.length() > 32) {
            sender.sendMessage("Nickname must be between 1 and 32 characters");
            return true;
        }

        NicknameService.updateNickname(sender.getName(), nickname);
        sender.sendMessage("Updated nickname to " + nickname);

        return true;
    }
}
