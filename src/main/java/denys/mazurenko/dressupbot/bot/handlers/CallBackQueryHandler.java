package denys.mazurenko.dressupbot.bot.handlers;

import denys.mazurenko.dressupbot.bot.util.TempFileStorage;
import denys.mazurenko.dressupbot.bot.util.TextTemplates;
import denys.mazurenko.dressupbot.dto.AdvertisementDto;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import denys.mazurenko.dressupbot.service.AdvertisementService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.function.BiConsumer;

@Component
public class CallBackQueryHandler extends UpdateHandler {
    private final AdvertisementService advertisementService;

    public CallBackQueryHandler(BotStateStorage botStateStorage,
                                AdvertisementService advertisementService) {
        super(botStateStorage);
        this.advertisementService = advertisementService;
    }

    @Override
    public String getKey() {
        return UpdateType.CALLBACK_QUERY.getType();
    }

    @Override
    public Reply getReply() {
        BiConsumer<BaseAbilityBot, Update> action = this::handle;
        return Reply.of(action,
                Flag.CALLBACK_QUERY,
                update -> {
                    Long chatId = update.getCallbackQuery().getMessage().getChatId();
                    return botStateStorage.isUserActive(chatId);
                });
    }

    private void handle(BaseAbilityBot bot, Update update) {
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
