package commands;

import execution.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class Ground extends Command {
    @Override
    public void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        List<User> mentioned = message.getMentionedUsers();
        if (mentioned.size() > 0) {
            mentioned.get(0).openPrivateChannel().queue(privateChannel -> {
                sendMessage("You know what, mister, I'm very disappointed in you. I put a roof over your head and this is how you " +
                        "treat me? I expect you to do all your chores from now on, or I'll take that damn computer away from you! Go back " +
                                "to your room and think about everything you've done, " + mentioned.get(0).getAsMention(),
                        channel, user);
            });
        }
        else sendMessage("Jeez, I can't take you anywhere! You have to mention one of my children, obviously", channel, user);
    }
}
