package main;

import commands.*;
import events.InteractiveOnMessage;
import events.OnMessage;
import events.ReactionEvent;
import execution.CommandFactory;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import org.apache.commons.io.IOUtils;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Shard {
    private int shardNumber;
    @Getter
    private int shardCount;
    public JDA jda;
    public CommandFactory factory = new CommandFactory();

    public Shard(int shardNumber, int shardCount) throws IOException, LoginException, InterruptedException, RateLimitedException {
        this.shardNumber = shardNumber;
        this.shardCount = shardCount;
        jda = new JDABuilder(AccountType.BOT)
                .setEventManager(new AnnotatedEventManager())
                .setToken(IOUtils.toString(new FileInputStream(new File("/root/Ardent/mombot.key"))))
                .setAutoReconnect(true)
                .setAudioEnabled(false)
                .setGame(Game.of("Type mom help"))
                .setStatus(OnlineStatus.ONLINE)
                .setBulkDeleteSplittingEnabled(true)
                .setEnableShutdownHook(true)
                .useSharding(shardNumber, shardCount)
                .buildBlocking();
        if (jda.getGuildById("260841592070340609") != null) Mom.botLogsShard = this;
        jda.addEventListener(new InteractiveOnMessage());
        jda.addEventListener(new OnMessage());
        jda.addEventListener(new ReactionEvent());
        factory.addCommand(new Help().with("help").setRatelimit(5).setDescription("See Mom's commands").setUsage
                ("help"));
        factory.addCommand(new Ground().with("ground").setRatelimit(60).setDescription("Ground a bad boy or girl").setUsage("ground " +
                "@User"));
        factory.addCommand(new GetTheCamera().with("get the camera", "camera", "get camera").setRatelimit(15).setDescription("Mom, get " +
                "the camera!").setUsage("get the camera"));
        factory.addCommand(new Sandwich().with("sandwich", "make me a sandwich", "make a sandwich").setRatelimit(5).setDescription("Let " +
                "me make you a sandwich").setUsage("sandwich"));
        factory.addCommand(new Weed().with("weed", "give me weed", "give me some weed").setRatelimit(30).setDescription("Let " +
                "me give you some weed").setUsage("weed"));
        factory.addCommand(new Roast().with("roast").setRatelimit(30).setDescription("I love to roast my children").setUsage("roast @User"));
        factory.addCommand(new Stats().with("stats").setDescription("Get my stats").setUsage("stats"));
        factory.addCommand(new Invite().with("invite").setDescription("Invite me to other servers so I can mom them!").setUsage("invite"));
    }

    public int getId() {
        return shardNumber;
    }
}
