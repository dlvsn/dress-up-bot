package denys.mazurenko.dressupbot.bot;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import denys.mazurenko.dressupbot.config.BotConfig;
import denys.mazurenko.dressupbot.exception.BotInitializationException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelegramBot extends AbilityBot {
    private final Long creatorId;
    private final MessageHandler messageHandler;
    private final Map<Object, Object> db = new ConcurrentHashMap<>();

    protected TelegramBot(BotConfig botConfig,
                          MessageHandler messageHandler) {
        super(botConfig.getBotToken(), botConfig.getBotName());
        this.messageHandler = messageHandler;
        this.creatorId = botConfig.getCreatorId();
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi tgBot = new TelegramBotsApi(DefaultBotSession.class);
            tgBot.registerBot(this);
        } catch (TelegramApiException e) {
            throw new BotInitializationException(
                    String.format("Can't initialize bot "
                            + this.getClass().getSimpleName()
                            + " %s", e));
        }
    }

    public Ability start() {
        return Ability.builder()
                .name("start")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> messageHandler.sendOnStart(silent, ctx.chatId(), ctx.user().getUserName()))
                .build();
    }

    @Override
    public long creatorId() {
        return creatorId;
    }
}
