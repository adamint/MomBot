package commands;

import execution.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import utils.GuildUtils;

public class Help extends Command {
    @Override
    public void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        EmbedBuilder builder = getDefaultEmbed(user);
        builder.setAuthor("Hi, I'm your discord mom", "https://ardentbot.tk", guild.getSelfMember().getUser().getEffectiveAvatarUrl());
        StringBuilder sb = new StringBuilder();
        sb.append("*Because you're obviously too stupid to figure them out on your own, here are my commands*\n");
        GuildUtils.getShard(guild).factory.getCommands().forEach(command -> {
            sb.append("\n - mom " + command.getUsage() + ": " + command.getDescription() + " | Wait time between usages: " + command.getTime() + " seconds");
        });
        builder.setDescription(sb.toString());
        sendEmbed(builder, channel, user);
    }
}
