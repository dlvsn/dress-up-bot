package denys.mazurenko.dressupbot.bot;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import denys.mazurenko.dressupbot.bot.handlers.UpdateHandler;
import denys.mazurenko.dressupbot.bot.handlers.UpdateHandlerProvider;
import denys.mazurenko.dressupbot.bot.handlers.UpdateType;
import denys.mazurenko.dressupbot.config.BotProperties;
import denys.mazurenko.dressupbot.exception.BotInitializationException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class DressUpBot extends AbilityBot {
    private final Long creatorId;
    private final UpdateHandlerProvider handlerProvider;

    protected DressUpBot(BotProperties botProperties,
                         UpdateHandlerProvider handlerProvider) {
        super(botProperties.getBotToken(), botProperties.getBotName());
        this.creatorId = botProperties.getCreatorId();
        this.handlerProvider = handlerProvider;
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
                .action(ctx -> {
                    UpdateHandler updateHandler = handlerProvider.getUpdateHandler(UpdateType.USER_TEXT.getType());
                    updateHandler.handleStart(silent, ctx.update(), creatorId());
                })
                .build();
    }

    public Ability stop() {
        return Ability.builder()
                .name("stop")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx ->
                        handlerProvider
                                .getUpdateHandler(UpdateType.USER_TEXT.getType())
                                .handleStop(silent, ctx.update()))
                .build();
    }

    public Ability help() {
        return Ability.builder()
                .name("help")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx ->
                        handlerProvider.getUpdateHandler(UpdateType.USER_TEXT.getType())
                                .handleHelp(silent, ctx.update(), creatorId))
                .build();
    }

    public Reply handleUserTextMessages() {
        return handlerProvider
                .getUpdateHandler(UpdateType.USER_TEXT.getType())
                .getReply();
    }

    public Reply handleAdminTextMessages() {
        return handlerProvider
                .getUpdateHandler(UpdateType.ADMIN_TEXT.getType())
                .getReply();
    }

    public Reply handlePhotoMessages() {
        return handlerProvider
                .getUpdateHandler(UpdateType.PHOTO.getType())
                .getReply();
    }

    public Reply handleCallBackQuery() {
        return handlerProvider
                .getUpdateHandler(UpdateType.CALLBACK_QUERY.getType())
                .getReply();
    }

    @Override
    public long creatorId() {
        return creatorId;
    }
}
