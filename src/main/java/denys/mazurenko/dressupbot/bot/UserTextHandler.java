package denys.mazurenko.dressupbot.bot;

import denys.mazurenko.dressupbot.bot.util.KeyBoardBotFactory;
import denys.mazurenko.dressupbot.bot.util.TempFileStorage;
import denys.mazurenko.dressupbot.dto.AdvertisementPriceNameDto;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import denys.mazurenko.dressupbot.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import java.util.List;

@Component
@Qualifier("userTextHandler")
public class UserTextHandler extends UpdateHandler {
    private final AdvertisementService advertisementService;
    private final KeyBoardBotFactory keyBoardBotFactory;

    public UserTextHandler(BotStateStorage botStateStorage,
                           TempFileStorage tempFileStorage,
                           AdvertisementService advertisementService,
                           KeyBoardBotFactory keyBoardBotFactory) {
        super(botStateStorage, tempFileStorage);
        this.advertisementService = advertisementService;
        this.keyBoardBotFactory = keyBoardBotFactory;
    }

    @Override
    public void handle(DressUpBot bot, Update update, int maxUploads) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        SilentSender silentSender = bot.silent();
        if (text.equalsIgnoreCase("/find")) {
            List<AdvertisementPriceNameDto> ads = advertisementService.findAdWithNameAndPrice();
            sendButtons(silentSender, chatId, ads);
            /* silentSender.send("Знайдено " + ads.size() + " товарів", chatId);
            ads.forEach(e -> silentSender.send(
                    String.format(
                            TextTemplates.AD,
                            e.getImgUrls(),
                            e.getId(), e.getName(), e.getDescription(),
                            e.getPrice(), bot.creatorId()), chatId)
            ); */
            botStateStorage.updateChatState(chatId, UserState.FIND);
        }
        if (text.equalsIgnoreCase("/exit") && chatId.equals(bot.creatorId())) {
            silentSender.send("Якщо потрібна допомога, скористайтесь командою /help", bot.creatorId());
            botStateStorage.updateChatState(bot.creatorId(), UserState.START);
        }
    }
    public void sendButtons(SilentSender sender, Long chatId, List<AdvertisementPriceNameDto> ads) {
        InlineKeyboardMarkup inlineKeyboardMarkup = keyBoardBotFactory.adsKeyboard(ads);
        SendMessage message = new SendMessage();
        message.setText("Choose one of the following advertisements:");
        message.setChatId(chatId);
        message.setReplyMarkup(inlineKeyboardMarkup);
        sender.execute(message);
    }
}
