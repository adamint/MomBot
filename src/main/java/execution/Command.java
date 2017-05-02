package execution;

import com.vdurmont.emoji.EmojiParser;
import events.ReactionEvent;
import lombok.Getter;
import lombok.NonNull;
import main.Mom;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static events.InteractiveOnMessage.lastMessages;
import static events.InteractiveOnMessage.queuedInteractives;

public abstract class Command {
    @Getter private int time = 0;
    @Getter private String[] aliases;
    @Getter private String description;
    @Getter private String usage;
    private HashMap<String, Long> ratelimited = new HashMap<>();

    public void sendMessage(@NonNull String content, @NonNull MessageChannel channel, @NonNull User user) {
        try {
            if (content.length() <= 2000) {
                channel.sendMessage(content).queue();
            }
            else {
                for (int i = 0; i < content.length(); i += 2000) {
                    if ((i + 2000) <= content.length()) {
                        channel.sendMessage(content.substring(i, i + 2000)).queue();
                    }
                    else {
                        channel.sendMessage(content.substring(i, content.length() - 1)).queue();
                    }
                }
            }
        }
        catch (PermissionException ex) {
            sendFailed(user, false);
        }
    }

    public Message sendEmbed(EmbedBuilder embedBuilder, TextChannel channel, User user, String... reactions) {
        try {
            Message message = channel.sendMessage(embedBuilder.build()).complete();
            for (String reaction : reactions) {
                message.addReaction(EmojiParser.parseToUnicode(reaction)).queue();
            }
            return message;
        }
        catch (PermissionException ex) {
            sendFailed(user, true);
        }
        return null;
    }

    protected void sendFailed(User user, boolean embed) {
        if (user != null) {
            user.openPrivateChannel().queue(privateChannel -> {
                try {
                    if (!embed) {
                        privateChannel.sendMessage("I don't have permission to type in this channel!").queue();
                    }
                    else {
                        privateChannel.sendMessage("I don't have permission to send embeds in this channel!").queue();
                    }
                }
                catch (Exception e) {
                    new BotException(e);
                }
            });
        }
    }

    public Command with(String... aliases) {
        this.aliases = aliases;
        return this;
    }

    public Command setUsage(String s) {
        usage = s;
        return this;
    }

    public Command setDescription(String s) {
        description = s;
        return this;
    }

    public Command setRatelimit(int seconds) {
        this.time = seconds;
        return this;
    }

    public boolean containsAlias(String s) {
        for (String q : aliases) if (s.equalsIgnoreCase(q)) return true;
        return false;
    }

    public void pass(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        if (time > 0) {
            long now = Instant.now().getEpochSecond();
            if (ratelimited.get(user.getId()) != null) {
                if (ratelimited.get(user.getId()) > now) {
                    channel.sendMessage("Trying to break the rules, eh " + user.getAsMention() + "? Mom won't stand for this. You can use" +
                            " " +
                            "that command again in " + String.valueOf(ratelimited.get(user.getId()) - now) + " seconds").queue();
                    return;
                }
            }
            else ratelimited.remove(user.getId());
        }
        onUsage(guild, channel, user, args, message);
        ratelimited.put(user.getId(), Instant.now().getEpochSecond() + time);
    }

    public abstract void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message);

    public String listWithCommas(List<String> strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            sb.append(strings.get(i));
            if (i < (strings.size() - 1)) sb.append(", ");
        }
        return sb.toString();
    }

    public static void interactiveReaction(MessageChannel channel, Message message, User user, int seconds,
                                           Consumer<MessageReaction> function) {
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        final int interval = 50;
        final int[] ranFor = {0};
        ex.scheduleAtFixedRate(() -> {
            if (ranFor[0] > 10000) {
                channel.sendMessage("Cancelled your reaction event because you didn't respond in time!").queue();
                ex.shutdown();
            }
            else {
                ranFor[0] += interval;
                for (Map.Entry<String, MessageReactionAddEvent> current : ReactionEvent.reactionEvents.entrySet()) {
                    String channelId = current.getKey();
                    MessageReactionAddEvent event = current.getValue();
                    if (channelId.equals(channel.getId())) {
                        if (event.getMessageId().equals(message.getId()) && event.getUser().getId().equals(user.getId())) {
                            function.accept(event.getReaction());
                            ex.shutdown();
                        }
                    }

                }
            }
        }, interval, interval, TimeUnit.MILLISECONDS);
    }

    public static void longInteractiveOperation(MessageChannel channel, Message message, User user, int seconds,
                                                Consumer<Message> function) {
        if (channel instanceof TextChannel) {
            queuedInteractives.put(message.getId(), user.getId());
            Mom.globalExecutorService.execute(() -> dispatchInteractiveEvent(message.getCreationTime(), (TextChannel) channel,
                    message, user, function, seconds * 1000, false));
        }
    }

    public static boolean longInteractiveOperation(MessageChannel channel, Message message, int seconds,
                                                   Consumer<Message> function) {
        final boolean[] succeeded = {false};
        if (channel instanceof TextChannel) {
            queuedInteractives.put(message.getId(), message.getAuthor().getId());
            Mom.globalExecutorService.execute(() -> succeeded[0] = dispatchInteractiveEvent(message.getCreationTime(), (TextChannel)
                            channel,
                    message, function, seconds * 1000, true));
            return succeeded[0];
        }
        return false;
    }

    public static boolean interactiveOperation(MessageChannel channel, Message message, Consumer<Message> function) {
        final boolean[] succeeded = {false};
        if (channel instanceof TextChannel) {
            queuedInteractives.put(message.getId(), message.getAuthor().getId());
            Mom.globalExecutorService.execute(() -> {
                succeeded[0] = dispatchInteractiveEvent(message.getCreationTime(), (TextChannel) channel,
                        message, function, 10000, true);
            });
            return succeeded[0];
        }
        return false;
    }

    private static boolean dispatchInteractiveEvent(OffsetDateTime creationTime, TextChannel channel, Message message, User user,
                                                    Consumer<Message> function, int time, boolean sendMessage) {
        final boolean[] success = {false};
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        final int interval = 50;
        final int[] ranFor = {0};
        ex.scheduleAtFixedRate(() -> {
            if (ranFor[0] >= time) {
                try {
                    if (sendMessage) {
                        if (time >= 15000) {
                            channel.sendMessage("Cancelled your interactive operation because you didn't respond within 15 seconds!")
                                    .queue();
                        }
                        else {
                            channel.sendMessage("Cancelled your reaction event because you didn't respond within **" + String.valueOf
                                    (time / 1000) + "** seconds").queue();
                        }
                    }
                }
                catch (Exception e) {
                    new BotException(e);
                }
                ex.shutdown();
                return;
            }
            Iterator<Message> iterator = lastMessages.keySet().iterator();
            while (iterator.hasNext()) {
                Message m = iterator.next();
                if (m.getCreationTime().isAfter(creationTime)) {
                    if (m.getAuthor().getId().equalsIgnoreCase(user.getId()) &&
                            m.getChannel().getId().equalsIgnoreCase(channel.getId()))
                    {
                        success[0] = true;
                        function.accept(m);
                        iterator.remove();
                        ex.shutdown();
                    }
                }
            }
            ranFor[0] += interval;
        }, interval, interval, TimeUnit.MILLISECONDS);
        return success[0];
    }

    private static boolean dispatchInteractiveEvent(OffsetDateTime creationTime, TextChannel channel, Message message, Consumer<Message>
            function, int time, boolean sendMessage) {
        final boolean[] success = {false};
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        final int interval = 50;
        final int[] ranFor = {0};
        ex.scheduleAtFixedRate(() -> {
            if (ranFor[0] >= time) {
                try {
                    if (sendMessage) {
                        if (time >= 15000) {
                            channel.sendMessage("Cancelled your interactive operation because you didn't respond within 15 seconds!")
                                    .queue();
                        }
                        else {
                            channel.sendMessage("Cancelled your reaction event because you didn't respond within **" + String.valueOf
                                    (time / 1000) + "** seconds").queue();
                        }
                    }
                }
                catch (Exception e) {
                    new BotException(e);
                }
                ex.shutdown();
                return;
            }
            Iterator<Message> iterator = lastMessages.keySet().iterator();
            while (iterator.hasNext()) {
                Message m = iterator.next();
                if (m.getCreationTime().isAfter(creationTime)) {
                    if (m.getAuthor().getId().equalsIgnoreCase(message.getAuthor().getId()) &&
                            m.getChannel().getId().equalsIgnoreCase(channel.getId()))
                    {
                        success[0] = true;
                        function.accept(m);
                        iterator.remove();
                        ex.shutdown();
                    }
                }
            }
            ranFor[0] += interval;
        }, interval, interval, TimeUnit.MILLISECONDS);
        return success[0];
    }

    public EmbedBuilder getDefaultEmbed(User author) {
        try {
            final Random random = new Random();
            final float hue = random.nextFloat();
            final float saturation = (random.nextInt(2000) + 1000) / 10000f;
            final float luminance = 2f;
            final Color color = Color.getHSBColor(hue, saturation, luminance);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(color);
            builder.setFooter("Requested by that idiot, {0}".replace("{0}", author.getName() + "#" + author.getDiscriminator()), author
                    .getAvatarUrl());
            return builder;
        }
        catch (Exception e) {
            new BotException(e);
            return null;
        }
    }
}
