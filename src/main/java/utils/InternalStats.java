package utils;

import execution.CommandFactory;
import lombok.Getter;
import main.Shard;
import net.dv8tion.jda.core.entities.Guild;

import static main.ShardManager.getShards;

public class InternalStats {
    @Getter
    private long messagesReceived;
    @Getter
    private long commandsReceived;
    @Getter
    private long loadedCommands;
    @Getter
    private long guilds;
    @Getter
    private long users;
    @Getter
    private long roleCount;
    @Getter
    private long textChannelCount;
    @Getter
    private long voiceChannelCount;
    @Getter
    private long musicPlayers;
    @Getter
    private double used_ram;
    @Getter
    private double total_ram;
    @Getter
    private double cpu_usage;
    public InternalStats(long messagesReceived, long commandsReceived, long loadedCommands, long guilds, long users, long roleCount, long
            textChannelCount,
                         long voiceChannelCount, long musicPlayers) {
        this.messagesReceived = messagesReceived;
        this.commandsReceived = commandsReceived;
        this.loadedCommands = loadedCommands;
        this.guilds = guilds;
        this.users = users;
        this.roleCount = roleCount;
        this.textChannelCount = textChannelCount;
        this.voiceChannelCount = voiceChannelCount;
        this.musicPlayers = musicPlayers;
        this.total_ram = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        this.used_ram = total_ram - Runtime.getRuntime().freeMemory() / 1024 / 1024;
        try {
            this.cpu_usage = UsageUtils.getProcessCpuLoad();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static InternalStats collect() {
        long messagesReceived = 0;
        long commandsReceived = 0;
        long loadedCommands = 0;
        long guilds = 0;
        long users = 0;
        long roleCount = 0;
        long textChannelCount = 0;
        long voiceChannelCount = 0;
        long musicPlayers = 0;
        for (Shard shard : getShards()) {
            CommandFactory factory = shard.factory;
            messagesReceived += factory.getMessagesReceived();
            commandsReceived += factory.getCommandsReceived();
            if (loadedCommands == 0) loadedCommands = factory.getLoadedCommands();
            guilds += shard.jda.getGuilds().size();
            users += shard.jda.getUsers().size();
            for (Guild guild : shard.jda.getGuilds()) {
                roleCount += guild.getRoles().size();
                textChannelCount += guild.getTextChannels().size();
                voiceChannelCount += guild.getVoiceChannels().size();
            }
        }
        return new InternalStats(messagesReceived, commandsReceived, loadedCommands, guilds, users, roleCount, textChannelCount,
                voiceChannelCount, musicPlayers);
    }
}
