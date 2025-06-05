package denys.mazurenko.dressupbot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BotProperties {
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${telegram.bot.creatorId}")
    private Long creatorId;
    @Value("${telegram.bot.max.uploads}")
    private int maxUploads;
}
