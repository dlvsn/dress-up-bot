package denys.mazurenko.dressupbot.bot;

import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import denys.mazurenko.dressupbot.bot.util.CallBackQueryHandler;
import denys.mazurenko.dressupbot.config.BotProperties;
import denys.mazurenko.dressupbot.exception.BotInitializationException;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import java.util.function.BiConsumer;

@Component
public class DressUpBot extends AbilityBot {
    private final Long creatorId;
    private final BotStateStorage botStateStorage;
    private int maxUploads;

    private final UpdateHandler photoHandler;
    private final UpdateHandler textHandler;
    private final UpdateHandler userTextHandler;
    private final CallBackQueryHandler callBackQueryHandler;

    protected DressUpBot(BotProperties botProperties,
                         @Qualifier("photoHandler")
                         UpdateHandler photoHandler,
                         @Qualifier("textHandler")
                         UpdateHandler textHandler,
                         @Qualifier("userTextHandler")
                         UpdateHandler userTextHandler,
                         CallBackQueryHandler callBackQueryHandler,
                         BotStateStorage botStateStorage) {
        super(botProperties.getBotToken(), botProperties.getBotName());
        this.creatorId = botProperties.getCreatorId();
        this.maxUploads = botProperties.getMaxUploads();
        this.photoHandler = photoHandler;
        this.textHandler = textHandler;
        this.userTextHandler = userTextHandler;
        this.callBackQueryHandler = callBackQueryHandler;
        this.botStateStorage = botStateStorage;
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
                    textHandler.handleStart(silent, ctx.update());
                }).build();
    }

    public Ability stop() {
        return Ability.builder()
                .name("stop")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx ->
                        textHandler.handleStop(silent, ctx.update()))
                .build();
    }

    public Ability help() {
        return Ability.builder()
                .name("help")
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx ->
                        textHandler.handleHelp(silent, ctx.update())
                )
                .build();
    }

    public Reply handleUserTextMessages() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, update) -> {
            userTextHandler.handle(this, update, maxUploads);
        };
        return Reply.of(action,
                Flag.TEXT,
                update -> {
                    Long chatId = update.getMessage().getChatId();
                    return isUserActive(chatId);
                });
    }

    public Reply handleAdminTextMessages() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, update) -> {
            textHandler.handle(this, update, maxUploads);
        };
        return Reply.of(action,
                Flag.TEXT,
                update -> {
                    Long chatId = update.getMessage().getChatId();
                    return isUserActive(chatId)
                            && chatId.equals(creatorId)
                            && botStateStorage.getChatState(chatId) != UserState.FIND;
                });
    }

    public Reply handlePhotoMessages() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, update) -> {
            photoHandler.handle(this, update, maxUploads);
            maxUploads--;
        };
        return Reply.of(action,
                Flag.PHOTO,
                update -> {
                    Long chatId = update.getMessage().getChatId();
                    return photoHandler.botStateStorage.isUserActive(chatId)
                            && isUserStateAwaitingPhoto(chatId);
                });
    }

    public Reply handleCallBackQuery() {
        BiConsumer<BaseAbilityBot, Update> action = (bot, update) -> {
            callBackQueryHandler.handle(this, update, maxUploads);
        };
        return Reply.of(action,
                Flag.CALLBACK_QUERY,
                update -> {
                    Long chatId = update.getCallbackQuery().getMessage().getChatId();
                    return isUserActive(chatId);
                });
    }

    @Override
    public long creatorId() {
        return creatorId;
    }

    private boolean isUserActive(Long chatId) {
        return textHandler.botStateStorage.isUserActive(chatId);
    }

    private boolean isUserStateAwaitingPhoto(Long chatId) {
        return isUserActive(chatId)
                && photoHandler
                .botStateStorage
                .getChatState(chatId) == UserState.AWAITING_PHOTO;
    }
}
