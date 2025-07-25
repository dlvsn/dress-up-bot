package denys.mazurenko.dressupbot.bot.handlers;

import denys.mazurenko.dressupbot.bot.util.PhotoDownloader;
import denys.mazurenko.dressupbot.bot.util.TempFileStorage;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.File;
import java.util.function.BiConsumer;

@Component
public class PhotoHandler extends UpdateHandler {
    private final PhotoDownloader photoDownloader;

    public PhotoHandler(BotStateStorage botStateStorage,
                        PhotoDownloader photoDownloader,
                        TempFileStorage tempFileStorage) {
        super(botStateStorage, tempFileStorage);
        this.photoDownloader = photoDownloader;
    }

    @Override
    public String getKey() {
        return UpdateType.PHOTO.getType();
    }

    @Override
    public Reply getReply() {
        BiConsumer<BaseAbilityBot, Update> action = this::handle;
        return Reply.of(action,
                Flag.PHOTO,
                update ->
                    checkIsUserAdmin(update.getMessage().getChatId()));
    }

    private void handle(BaseAbilityBot bot,
                       Update update) {
        Long chatId = update.getMessage().getChatId();
        File photoFile = photoDownloader.fetchPhoto(update, bot, chatId);
        if (tempFileStorage.addFile(photoFile)) {
            bot.silent().send("Фото успішно завантажене", chatId);
            bot.silent().send("Якщо ти не хочеш більше додавати фотографії, скористайся командою /next", chatId);
        } else {
            bot.silent().send("Щось пішло не так, не вдалось завантажити фото", chatId);
        }
    }

    private boolean checkIsUserAdmin(Long id) {
        UserState chatState = botStateStorage.getChatState(id);
        return chatState == UserState.AWAITING_PHOTO;
    }
}
