package denys.mazurenko.dressupbot.bot.util;

import denys.mazurenko.dressupbot.dto.AdvertisementPriceNameDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardFactoryImpl implements KeyboardFactory{

    @Override
    public InlineKeyboardMarkup getAdvertisementsListKeyboard(List<AdvertisementPriceNameDto> ads) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = ads.stream().map(ad -> {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(ad.name()+ " – " + ad.price() + " грн.");
            button.setCallbackData(String.valueOf(ad.id()));
            return button;
        }).toList();
        rows.add(buttons);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup getCategoryListKeyboard(List<CategoryResponseDto> categories) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> buttons = categories
                .stream()
                .map(c -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(c.name());
                    button.setCallbackData(String.valueOf(c.id()));
                    return button;
                }).toList();
        rows.add(buttons);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }
}
