package denys.mazurenko.dressupbot.bot.util;

import denys.mazurenko.dressupbot.exception.DataProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.File;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PhotoDownloaderImpl implements PhotoDownloader {
    /* private final TempFileStorage tempFileStorage;
    private static final String PHOTO_PATH = "src/main/resources/uploads/images/";
    private static final String PHOTO_FILE_NAME = "user_%s_photo_%s.jpg"; */

    @Override
    public File fetchPhoto(Update update, BaseAbilityBot bot, Long chatId) {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize photoSize = getPhotoSize(photos);
        try {
            // org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFileMethod(photoSize));
            return bot.downloadFile(bot.execute(getFileMethod(photoSize)));
            //Path path = Paths.get(PHOTO_PATH);
            // String localFileName = LocalDateTime.now().toString() + ".jpg";
            //String localFileName = "user_" + chatId + "_photo_" + System.currentTimeMillis() + ".jpg";
            //Path destination = path.resolve(localFileName);
            //Files.copy(downloadedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                /*bot.send("Photo saved locally as: " + localFileName, chatId);
                updateChatState(chatId, UserState.PHOTO_RECEIVED);
                */
                // Опціонально: видалити тимчасовий файл, завантажений бібліотекою
        } catch (TelegramApiException e) {
            throw new DataProcessingException(
                    String.format(
                            "Can't fetch data from telegram %s", e.getMessage()));
        }
    }

    private GetFile getFileMethod(PhotoSize photoSize) {
        String fileId = photoSize.getFileId();
        GetFile getFileMethod = new GetFile();
        getFileMethod.setFileId(fileId);
        return getFileMethod;
    }

    private PhotoSize getPhotoSize(List<PhotoSize> photos) {
        return photos.stream()
                .max(Comparator.comparingInt(PhotoSize::getFileSize))
                .orElseThrow(() ->
                        new DataProcessingException("Can't find largest photo")
                );
    }
}
