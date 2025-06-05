package denys.mazurenko.dressupbot.bot;

import denys.mazurenko.dressupbot.bot.util.TempFileStorage;
import denys.mazurenko.dressupbot.bot.util.TextTemplates;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public abstract class UpdateHandler {
    protected final BotStateStorage botStateStorage;
    protected final TempFileStorage tempFileStorage;

    abstract public void handle(DressUpBot bot, Update update, int maxUploads);

    protected void handleStart(SilentSender sender, Update update) {
        String message = String.format("Привіт, %s!", update.getMessage().getFrom().getUserName());
        Long chatId = update.getMessage().getChatId();
        sender.send(message, chatId);
        botStateStorage.updateChatState(chatId, UserState.START);
    }

    protected void handleStop(SilentSender sender, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (text != null && text.equalsIgnoreCase("/stop")) {
            sender.send("Бувай!", chatId);
            botStateStorage.removeChatState(chatId);
        }
    }

    protected void handleHelp(SilentSender sender, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (text != null && text.equalsIgnoreCase("/help")) {
            sender.send(TextTemplates.HELP_ADMIN, chatId);
        }
    }
}
