package denys.mazurenko.dressupbot.bot.util;

import denys.mazurenko.dressupbot.dto.AdvertisementPriceNameDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;

@Component
public class KeyBoardBotFactory {

    public InlineKeyboardMarkup adsKeyboard(List<AdvertisementPriceNameDto> ads) {
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
}
