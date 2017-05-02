package commands;

import execution.Command;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import utils.GuildUtils;
import utils.InternalStats;

public class Stats extends Command {
    @Override
    public void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        EmbedBuilder builder = getDefaultEmbed(user);
        builder.setAuthor("Mom stats", "https://ardentbot.tk", guild.getSelfMember().getUser().getAvatarUrl());
        InternalStats internalStats = InternalStats.collect();
        builder.addField("Messages Received", internalStats.getMessagesReceived() + "", true);
        builder.addField("Commands Received", internalStats.getCommandsReceived() + "", true);
        builder.addField("Loaded Commands", internalStats.getLoadedCommands() + "", true);
        builder.addField("Server Count", internalStats.getGuilds() + "", true);
        builder.addField("User Count", internalStats.getUsers() + "", true);
        builder.addField("Text Channels", internalStats.getTextChannelCount() + "", true);
        builder.addField("Role Count", internalStats.getRoleCount() + "", true);
        builder.addField("CPU Usage", internalStats.getCpu_usage() + "%", true);
        builder.addField("RAM Usage", internalStats.getUsed_ram() + " / " + internalStats.getTotal_ram(), true);
        sendEmbed(builder, channel, user);
    }
}
