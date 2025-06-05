package denys.mazurenko.dressupbot.bot.util;

import denys.mazurenko.dressupbot.bot.DressUpBot;
import denys.mazurenko.dressupbot.bot.UpdateHandler;
import denys.mazurenko.dressupbot.dto.AdvertisementDto;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import denys.mazurenko.dressupbot.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Qualifier("callBackQueryHandler")
public class CallBackQueryHandler extends UpdateHandler{
    private final AdvertisementService advertisementService;

    public CallBackQueryHandler(BotStateStorage botStateStorage,
                                TempFileStorage tempFileStorage,
                                AdvertisementService advertisementService) {
        super(botStateStorage, tempFileStorage);
        this.advertisementService = advertisementService;
    }

    @Override
    public void handle(DressUpBot bot, Update update, int maxUploads) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        SilentSender silentSender = bot.silent();
        AdvertisementDto ad = advertisementService.findById(Long.parseLong(data));
            silentSender.send(String.format(
                            TextTemplates.AD,
                            ad.getImgUrls(),
                            ad.getId(),
                            ad.getName(),
                            ad.getDescription(),
                            ad.getPrice()),
                    chatId);
    }
}
