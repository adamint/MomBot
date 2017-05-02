package commands;

import execution.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class Weed extends Command {
    @Override
    public void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        sendMessage("Ok " + user.getName() + ", but just because I'm a great mom, here you go: :leaves:", channel, user);
    }
}
