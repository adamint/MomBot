package execution;

import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;

public class CommandFactory {
    @Getter private long messagesReceived = 0;
    @Getter private long commandsReceived = 0;
    private ArrayList<Command> commands = new ArrayList<>();

    public CommandFactory() {
    }

    public void addCommand(Command cmd) {
        commands.add(cmd);
    }

    public void pass(GuildMessageReceivedEvent event) {
        messagesReceived++;
        User user = event.getAuthor();
        Guild guild = event.getGuild();
        Message message = event.getMessage();
        String rawContent = message.getRawContent();
        if (rawContent.startsWith("<@309035601611587585>") || rawContent.startsWith("<@!309035601611587585>")) {
            rawContent = "mom" + rawContent.replace("<@309035601611587585>", "").replace("<@!309035601611587585>", "");
        }
        String[] rawArgs = rawContent.split(" ");
        if (rawArgs.length > 1) {
            if (rawArgs[0].equalsIgnoreCase("mombot") || rawArgs[0].equalsIgnoreCase("mom")) {
                String finalRawContent = rawContent;
                commands.forEach(command -> {
                    if (command.containsAlias(rawArgs[1]) || command.containsAlias(finalRawContent.replace(rawArgs[0] + " ", ""))) {
                        ArrayList<String> argsList = new ArrayList<>();
                        for (int i = 1; i < rawArgs.length; i++) argsList.add(rawArgs[i]);
                        String[] args = argsList.toArray(new String[argsList.size()]);
                        commandsReceived++;
                        command.pass(guild, event.getChannel(), user, args, message);
                    }
                });
            }
        }
        else if (message.getMentionedUsers().contains(event.getGuild().getSelfMember().getUser()) || rawContent.equalsIgnoreCase("mom")) {
            commands.forEach(command -> {
                if (command.containsAlias("help")) {
                    commandsReceived++;
                    command.pass(guild, event.getChannel(), user, rawArgs, message);
                }
            });
        }
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public int getLoadedCommands() {
        return commands.size();
    }
}
