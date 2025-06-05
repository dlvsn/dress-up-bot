package denys.mazurenko.dressupbot.bot;

import denys.mazurenko.dressupbot.bot.util.PhotoDownloader;
import denys.mazurenko.dressupbot.bot.util.TempFileStorage;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.File;

@Component
@Qualifier("photoHandler")
public class PhotoHandler extends UpdateHandler {
    private final PhotoDownloader photoDownloader;

    public PhotoHandler(BotStateStorage botStateStorage,
                        PhotoDownloader photoDownloader,
                        TempFileStorage tempFileStorage) {
        super(botStateStorage, tempFileStorage);
        this.photoDownloader = photoDownloader;
    }

    @Override
    public void handle(DressUpBot bot,
                       Update update,
                       int maxUploads) {
        Long chatId = update.getMessage().getChatId();
        if (maxUploads != 0) {
            bot.silent().send("Фото отримано. Ти можеш додати ще " + (maxUploads - 1) + " фотографії", chatId);

            File photoPath = photoDownloader.fetchPhoto(update, bot, chatId);
            tempFileStorage.addFile(photoPath);

            bot.silent().send("Фото успішно завантажене", chatId);
            bot.silent().send("Якщо ти не хочеш більше додавати фотографії, скористайся командою /next", chatId);
        } else {
            bot.silent().send("Ти не можеш додавати більше ніж " +  maxUploads, chatId);
            botStateStorage.updateChatState(chatId, UserState.PHOTO_RECEIVED);
        }
    }
}
