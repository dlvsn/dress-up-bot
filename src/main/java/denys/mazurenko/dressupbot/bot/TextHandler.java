package denys.mazurenko.dressupbot.bot;

import denys.mazurenko.dressupbot.bot.util.TempFileStorage;
import denys.mazurenko.dressupbot.dto.AdvertisementDto;
import denys.mazurenko.dressupbot.dto.ImageResponseDto;
import denys.mazurenko.dressupbot.repository.AdDraftStorage;
import denys.mazurenko.dressupbot.service.AdvertisementService;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import denys.mazurenko.dressupbot.service.ImageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

@Component
@Qualifier("textHandler")
public class TextHandler extends UpdateHandler {
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PRICE = "price";
    private final AdDraftStorage adDraftStorage;
    private final ImageService imageService;
    private final AdvertisementService advertisementService;

    public TextHandler(BotStateStorage botStateStorage,
                       TempFileStorage tempFileStorage,
                       ImageService imageService,
                       AdvertisementService advertisementService,
                       AdDraftStorage adDraftStorage) {
        super(botStateStorage, tempFileStorage);
        this.imageService = imageService;
        this.advertisementService = advertisementService;
        this.adDraftStorage = adDraftStorage;
    }

    @Override
    public void handle(DressUpBot bot,
                       Update update,
                       int maxUploads) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        SilentSender silentSender = bot.silent();
        if (text != null && text.equalsIgnoreCase("/next")) {
            botStateStorage.updateChatState(chatId, UserState.PHOTO_RECEIVED);
        }
        handleStop(silentSender, update);
        switch (botStateStorage.getChatState(chatId)) {
            case START -> handleCreate(
                    silentSender,
                    update,
                    maxUploads);

            case AWAITING_PHOTO ->
                    handleAwaitingPhoto(
                            silentSender,
                            update);

            case PHOTO_RECEIVED -> {
                handleNext(
                        silentSender,
                        update);
            }

            case AWAITING_ITEM_NAME ->
                    handleItemName(
                    silentSender,
                    update);

            case AWAITING_DESCRIPTION ->
                    handleDescription(
                    silentSender,
                    update);

            case AWAITING_PRICE ->
                    handlePrice(
                            silentSender,
                            update);

            case COMPLETED -> saveAdd(
                    silentSender,
                    update);
        }
    }

    private void handleCreate(SilentSender sender,
                              Update update,
                              int maxUploads) {
        String message = update.getMessage().getText();
        if (message.equalsIgnoreCase("/create")) {
            String text = "Ви можете додати до оголошення " + maxUploads + " фото. Будь ласка, надішліть мені фото.";
            sender.send(text, update.getMessage().getChatId());
            botStateStorage.updateChatState(update.getMessage().getChatId(), UserState.AWAITING_PHOTO);
        }
    }

    private void handleNext(SilentSender sender,
                            Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (text != null && text.equalsIgnoreCase("/next")) {
            sender.send("Додай імя оголошенню", chatId);
            botStateStorage.updateChatState(chatId, UserState.AWAITING_ITEM_NAME);
        }
    }

    private void handleItemName(SilentSender sender,
                                Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (text != null && !text.isBlank()) {
            sender.send("Імя оголошення " + text, chatId);
            adDraftStorage.saveDraft(KEY_NAME, text);
            sender.send("Додай короткий опис до оголошення", chatId);
            botStateStorage.updateChatState(chatId, UserState.AWAITING_DESCRIPTION);
        }
    }

    private void handleDescription(SilentSender sender,
                                   Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        adDraftStorage.saveDraft(KEY_DESCRIPTION, text);
        sender.send("Опис оголошення " + text, chatId);
        sender.send("Додай вартість товару", chatId);
        botStateStorage.updateChatState(chatId, UserState.AWAITING_PRICE);
    }

    private void handlePrice(SilentSender sender,Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        adDraftStorage.saveDraft(KEY_PRICE, text);
        sender.send("Вартість товару " + text, chatId);
        sender.send("Що б зберегети натисни /save " + text, chatId);
        botStateStorage.updateChatState(chatId, UserState.COMPLETED);
    }

    private void saveAdd(SilentSender sender, Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (text.equalsIgnoreCase("/save")) {
            List<String> imageUrls = getImageUrls();
            AdvertisementDto advertisementDto = advertisementService.saveAdd(initAddDto(imageUrls));
            sender.send("Замовлення успішно збережене! Id заомлення: " + advertisementDto.getId(), chatId);
        }
    }

    private void handleAwaitingPhoto(SilentSender sender, Update update) {
        String text = update.getMessage().getText();
        if (text != null && text.equalsIgnoreCase("/next")) {
            sender.send("Додай імя оголошенню", update.getMessage().getChatId());
            botStateStorage.updateChatState(update.getMessage().getChatId(), UserState.AWAITING_ITEM_NAME);
        } else {
            sender.send("Це не фото! Будь ласка, надішли фотографію або використай /next для пропуску.", update.getMessage().getChatId());
        }
    }

    private void handleUnknown(SilentSender sender, Update update) {
        sender.send("Невідома команда", update.getMessage().getChatId());
    }

    private List<String> getImageUrls() {
        List<File> files = tempFileStorage.getFiles();
        return imageService.saveImages(files)
                .stream()
                .map(ImageResponseDto::url)
                .toList();
    }

    private AdvertisementDto initAddDto(List<String> imgUrls) {
        AdvertisementDto advertisementDto = new AdvertisementDto();
        advertisementDto.setImgUrls(imgUrls);
        String itemName = (String) adDraftStorage.getDraft(KEY_NAME);
        String description = (String) adDraftStorage.getDraft(KEY_DESCRIPTION);
        String price = (String) adDraftStorage.getDraft(KEY_PRICE);
        advertisementDto.setName(itemName);
        advertisementDto.setDescription(description);
        advertisementDto.setPrice(BigDecimal.valueOf(Integer.parseInt(price)));
        return advertisementDto;
    }
}
