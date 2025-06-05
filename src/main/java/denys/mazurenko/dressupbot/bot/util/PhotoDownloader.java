package denys.mazurenko.dressupbot.bot.util;

import denys.mazurenko.dressupbot.bot.DressUpBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.File;

public interface PhotoDownloader {
    File fetchPhoto(Update update, DressUpBot bot, Long chatId);

    boolean deleteFile(File file);
}
