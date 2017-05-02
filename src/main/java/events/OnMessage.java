package events;


import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import utils.GuildUtils;

public class OnMessage {
    @SubscribeEvent
    public void onEvent(GuildMessageReceivedEvent event) {
        GuildUtils.getShard(event.getGuild()).factory.pass(event);
    }

    @SubscribeEvent
    public void onPM(PrivateMessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            event.getChannel().sendMessage("MomBot commands can only be used in servers! type `mombot help` to get help").queue();
        }
    }
}
