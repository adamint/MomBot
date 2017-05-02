package commands;

import execution.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Random;

public class GetTheCamera extends Command {
    @Override
    public void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        if (new Random().nextBoolean()) {
            StringBuilder msg = new StringBuilder();
            msg.append("\n```css\n");
            channel.getHistory().retrievePast(5).queue(messages -> {
                messages.forEach(m -> msg.append("\n" + m.getContent().replace("`", "\"").replace("\n", "") + " - " + m.getAuthor().getName() + "#" + m.getAuthor().getDiscriminator()));
                msg.append("\n```");
                sendMessage("Ok son! Here's the picture: " + msg.toString(), channel, user);
            });
        }
        else sendMessage("You're a teenager now, do I have to do everything for you? Get it yourself!", channel, user);
    }
}
