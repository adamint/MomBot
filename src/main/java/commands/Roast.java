package commands;

import execution.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.security.SecureRandom;
import java.util.List;

public class Roast extends Command {
    public String[] roasts = {"go get some friends"};
    @Override
    public void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        List<User> mentioned = message.getMentionedUsers();
        if (mentioned.size() > 0) {
            sendMessage(mentioned.get(0).getAsMention() + ", " + roasts[new SecureRandom().nextInt(roasts.length)], channel, user);
        }
        else sendMessage("Jeez, I can't take you anywhere! You have to mention one of my children, obviously", channel, user);
    }
}
