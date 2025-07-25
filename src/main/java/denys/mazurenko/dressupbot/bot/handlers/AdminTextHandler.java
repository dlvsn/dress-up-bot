package denys.mazurenko.dressupbot.bot.handlers;

import denys.mazurenko.dressupbot.bot.util.TempFileStorage;
import denys.mazurenko.dressupbot.bot.util.TextTemplates;
import denys.mazurenko.dressupbot.config.BotProperties;
import denys.mazurenko.dressupbot.dto.AdvertisementDto;
import denys.mazurenko.dressupbot.dto.CategoryRequestDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;
import denys.mazurenko.dressupbot.dto.ImageResponseDto;
import denys.mazurenko.dressupbot.repository.AdDraftStorage;
import denys.mazurenko.dressupbot.service.AdvertisementService;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import denys.mazurenko.dressupbot.service.CategoryService;
import denys.mazurenko.dressupbot.service.ImageService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiConsumer;

@Component
public class AdminTextHandler extends UpdateHandler {
    private static final List<String> addSelection = List.of("/ad", "/category");
    private static final int AD = 0;
    private static final int CATEGORY = 1;
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PRICE = "price";
    private final AdDraftStorage adDraftStorage;
    private final ImageService imageService;
    private final AdvertisementService advertisementService;
    private final CategoryService categoryService;
    private final Long creatorId;

    public AdminTextHandler(BotStateStorage botStateStorage,
                            TempFileStorage tempFileStorage,
                            ImageService imageService,
                            AdvertisementService advertisementService,
                            AdDraftStorage adDraftStorage,
                            CategoryService categoryService,
                            BotProperties botProperties) {
        super(botStateStorage, tempFileStorage);
        this.imageService = imageService;
        this.advertisementService = advertisementService;
        this.adDraftStorage = adDraftStorage;
        this.categoryService = categoryService;
        this.creatorId = botProperties.getCreatorId();
    }

    @Override
    public String getKey() {
        return UpdateType.ADMIN_TEXT.getType();
    }

    @Override
    public Reply getReply() {
        BiConsumer<BaseAbilityBot, Update> action = this::handle;
        return Reply.of(action,
                Flag.TEXT,
                update -> checkIsUserAdmin(update.getMessage().getChatId())
        );
    }

    private void handle(BaseAbilityBot bot,
                       Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        SilentSender silentSender = bot.silent();
        if (text != null && text.equalsIgnoreCase("/stop")) {
            handleStop(silentSender, update);
        }
        if (text != null && text.equalsIgnoreCase("/next")) {
            botStateStorage.updateChatState(chatId, UserState.PHOTO_RECEIVED);
        }
        switch (botStateStorage.getChatState(chatId)) {
            case START -> handleCreate(
                    silentSender,
                    update);

            case AD_CATEGORY -> handleSelection(
                    silentSender,
                    update);

            case AWAITING_CATEGORY_NAME -> handleCreateCategory(
                    silentSender,
                    update);

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

    private void handleCreateCategory(SilentSender silentSender, Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        CategoryRequestDto categoryRequestDto = new CategoryRequestDto(text);
        CategoryResponseDto categoryResponseDto = categoryService.saveCategory(categoryRequestDto);
        silentSender.send("Категорія успішно збережена " + categoryResponseDto, chatId);
        botStateStorage.updateChatState(chatId, UserState.START);
        silentSender.send(TextTemplates.ADD_CATEGORY_OR_AD, chatId);
    }

    private void handleCreate(SilentSender sender,
                              Update update) {
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        if (message.equalsIgnoreCase("/create")) {
            sender.send(TextTemplates.ADD_CATEGORY_OR_AD, chatId);
            botStateStorage.updateChatState(chatId, UserState.AD_CATEGORY);
        }
    }

    private void handleSelection(SilentSender sender, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (text.equalsIgnoreCase(addSelection.get(CATEGORY))) {
            sender.send("Введи назву категорії", chatId);
            botStateStorage.updateChatState(chatId, UserState.AWAITING_CATEGORY_NAME);
        } else if (text.equalsIgnoreCase(addSelection.get(AD)) && categoryService.isCategoryExists()) {
            sender.send("Надішли будь ласка 2 фотографії", chatId);
            botStateStorage.updateChatState(chatId, UserState.AWAITING_PHOTO);
        } else if (!categoryService.isCategoryExists()){
            sender.send("Додай спочатку категорії", chatId);
        } else {
            sender.send("Обери одне з двох!", chatId);
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
            sender.send("Якщо бажаєте додати замовлення, скористайтесь командою /create\nЩоб переглянути завантажені замовлення /find", chatId);
            botStateStorage.updateChatState(chatId, UserState.START);
            tempFileStorage.clearFileStorage();
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
        if (files != null) {
            return imageService.saveImages(files)
                    .stream()
                    .map(ImageResponseDto::url)
                    .toList();
        }
        return List.of("EmptyList");
    }

    private AdvertisementDto initAddDto(List<String> imgUrls) {
        AdvertisementDto advertisementDto = new AdvertisementDto();
        advertisementDto.setImgUrls(imgUrls);
        advertisementDto.setName(
                (String) adDraftStorage.getDraft(KEY_NAME)
        );
        advertisementDto.setDescription(
                (String) adDraftStorage.getDraft(KEY_DESCRIPTION)
        );
        advertisementDto.setPrice(BigDecimal.valueOf(
                Integer.parseInt(
                (String) adDraftStorage.getDraft(KEY_PRICE))
                )
        );
        return advertisementDto;
    }

    private boolean checkIsUserAdmin(Long chatId) {
        return botStateStorage.getChatState(creatorId) != UserState.FIND && chatId.equals(creatorId);
    }
}
