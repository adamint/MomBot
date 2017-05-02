package commands;

import execution.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class Invite extends Command {
    @Override
    public void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        sendMessage("Invite me by going to this URL: https://ardentbot.tk/mombot - be careful, the interwebs are scary!", channel, user);
    }
}
