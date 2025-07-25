package denys.mazurenko.dressupbot.bot.util;

import denys.mazurenko.dressupbot.dto.AdvertisementPriceNameDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import java.util.List;

public interface KeyboardFactory {
    InlineKeyboardMarkup getAdvertisementsListKeyboard(List<AdvertisementPriceNameDto> ads);

    InlineKeyboardMarkup getCategoryListKeyboard(List<CategoryResponseDto> categories);
}
