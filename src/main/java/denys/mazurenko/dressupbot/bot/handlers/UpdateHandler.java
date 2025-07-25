package denys.mazurenko.dressupbot.bot.handlers;

import denys.mazurenko.dressupbot.bot.util.TempFileStorage;
import denys.mazurenko.dressupbot.bot.util.TextTemplates;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public abstract class UpdateHandler {
    protected final BotStateStorage botStateStorage;
    protected TempFileStorage tempFileStorage;

    protected UpdateHandler(BotStateStorage botStateStorage,
                            TempFileStorage tempFileStorage) {
        this.botStateStorage = botStateStorage;
        this.tempFileStorage = tempFileStorage;
    }

    protected UpdateHandler(BotStateStorage botStateStorage) {
        this.botStateStorage = botStateStorage;
    }

    abstract public String getKey();
    abstract public Reply getReply();

    public void handleStart(SilentSender sender, Update update, Long creatorId) {
        Long chatId = update.getMessage().getChatId();
        if (creatorId.equals(chatId)) {
            sender.send(String.format(TextTemplates.START, update.getMessage().getFrom().getUserName()), creatorId);
            sender.send(TextTemplates.HELP_ADMIN, creatorId);
        } else {
            String message = String.format("Привіт, %s!", update.getMessage().getFrom().getUserName());
            sender.send(message, chatId);
        }
        botStateStorage.updateChatState(chatId, UserState.START);
    }

    public void handleStop(SilentSender sender, Update update) {
        Long chatId = update.getMessage().getChatId();
        sender.send("Бувай!", chatId);
        botStateStorage.removeChatState(chatId);
    }

    public void handleHelp(SilentSender sender, Update update, Long creatorId) {
        Long chatId = update.getMessage().getChatId();
        sender.send(TextTemplates.HELP_USER, chatId);
        if (chatId.equals(creatorId)) {
            sender.send(TextTemplates.HELP_ADMIN, chatId);
        }
    }
}
