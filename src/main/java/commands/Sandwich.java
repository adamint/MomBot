package commands;

import execution.Command;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import utils.MathUtils;

import java.security.SecureRandom;
import java.util.ArrayList;

public class Sandwich extends Command {
    public String[] ingredients = {"lettuce", "salami", "tomatos", "cheese", "love", "poison", "chicken", "egg", "bacon", "ham"};

    @Override
    public void onUsage(Guild guild, TextChannel channel, User user, String[] args, Message message) {
        sendMessage("How many ingredients do you want with that?", channel, user);
        longInteractiveOperation(channel, message, user,15, amountMessage -> {
            String content = amountMessage.getContent();
            if (MathUtils.isNumber(content)) {
                int amount = Integer.parseInt(content);
                if (amount <= 0) sendMessage("Nice try honey!", channel, user);
                else if (amount > ingredients.length) sendMessage("That's more ingredients than what's in my kitchen!", channel, user);
                else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(user.getAsMention() + ", here's your sandwich :meat_on_bone:, made with love and including the following ingredient(s): ");
                    ArrayList<String> stuff = new ArrayList<>();
                    for (int i = 0; i < amount; i++) {
                        String s = ingredients[new SecureRandom().nextInt(ingredients.length)];
                        if (stuff.contains(s)) i--;
                        else stuff.add(s);
                    }
                    sb.append(listWithCommas(stuff));
                    sendMessage(sb.toString(), channel, user);
                }
            }
            else sendMessage("Do I really need to teach you what numbers are?", channel, user);
        });
    }
}
