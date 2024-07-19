package main.java.elementalmp4.command.nicknames;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.service.NicknameService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

@SebUtilsCommand
public class SetNicknameCommand extends AbstractCommand {

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

    @Override
    public String getCommandName() {
        return "nickname";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
