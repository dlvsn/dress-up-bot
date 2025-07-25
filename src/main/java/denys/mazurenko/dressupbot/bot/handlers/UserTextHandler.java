package denys.mazurenko.dressupbot.bot.handlers;

import denys.mazurenko.dressupbot.bot.util.KeyboardFactory;
import denys.mazurenko.dressupbot.dto.AdvertisementPriceNameDto;
import denys.mazurenko.dressupbot.dto.CategoryResponseDto;
import denys.mazurenko.dressupbot.repository.BotStateStorage;
import denys.mazurenko.dressupbot.service.AdvertisementService;
import denys.mazurenko.dressupbot.service.CategoryService;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import java.util.List;
import java.util.function.BiConsumer;

@Component
public class UserTextHandler extends UpdateHandler {
    private static final List<String> userSelection = List.of("/all", "/categories");
    private static final int ALL_CODE = 0;
    private static final int CATEGORY_CODE = 1;
    private final AdvertisementService advertisementService;
    private final CategoryService categoryService;
    private final KeyboardFactory keyBoardFactory;

    public UserTextHandler(KeyboardFactory keyBoardFactory,
                           BotStateStorage botStateStorage,
                           AdvertisementService advertisementService,
                           CategoryService categoryService) {
        super(botStateStorage);
        this.advertisementService = advertisementService;
        this.categoryService = categoryService;
        this.keyBoardFactory = keyBoardFactory;
    }

    @Override
    public String getKey() {
        return UpdateType.USER_TEXT.getType();
    }

    @Override
    public Reply getReply() {
        BiConsumer<BaseAbilityBot, Update> action = this::handle;
        return Reply.of(
                action,
                Flag.TEXT,
                update ->
                        botStateStorage.isUserActive(update.getMessage().getChatId())
        );
    }

    private void handle(BaseAbilityBot bot, Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        SilentSender silentSender = bot.silent();
        if (isTextNullAndEquals(text, "/stop")) {
            handleStop(silentSender, update);
        } else if (botStateStorage.getChatState(chatId) == UserState.FIND_SELECTION) {
            handleSelection(silentSender, update);
        } else if (isTextNullAndEquals(text, "/find")) {
            silentSender.send("Оберіть як шукати товар /all або /categories", chatId);
            botStateStorage.updateChatState(chatId, UserState.FIND_SELECTION);
        } else if (isTextNullAndEquals(text, "/exit") && chatId.equals(bot.creatorId())) {
            silentSender.send("Якщо потрібна допомога, скористайтесь командою /help", bot.creatorId());
            botStateStorage.updateChatState(bot.creatorId(), UserState.START);
        } else {
            silentSender.send("Скористайся командо /find", bot.creatorId());
        }
    }

    private void handleSelection(SilentSender sender, Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (text.equalsIgnoreCase(userSelection.get(ALL_CODE))) {
            List<AdvertisementPriceNameDto> ads = advertisementService.findAdWithNameAndPrice();
            sendButtonsWithAdvertisements(sender, chatId, ads);
            botStateStorage.updateChatState(chatId, UserState.FIND);
            sender.send("code all", chatId);
        } else if (text.equalsIgnoreCase(userSelection.get(CATEGORY_CODE))) {
            List<CategoryResponseDto> categories = categoryService.getAllCategories();
            sender.send("category code", chatId);
            sendButtonsWithCategories(sender, chatId, categories);
            botStateStorage.updateChatState(chatId, UserState.FIND);
        }
    }

    private void sendButtonsWithAdvertisements(SilentSender sender,
                                               Long chatId,
                                               List<AdvertisementPriceNameDto> ads) {
        InlineKeyboardMarkup inlineKeyboardMarkup = keyBoardFactory.getAdvertisementsListKeyboard(ads);
        SendMessage message = new SendMessage();
        message.setText("Choose one of the following advertisements:");
        message.setChatId(chatId);
        message.setReplyMarkup(inlineKeyboardMarkup);
        sender.execute(message);
    }

    private void sendButtonsWithCategories(SilentSender sender,
                                           Long chatId,
                                           List<CategoryResponseDto> categories) {
        InlineKeyboardMarkup inlineKeyboardMarkup = keyBoardFactory.getCategoryListKeyboard(categories);
        SendMessage message = new SendMessage();
        message.setText("Choose one of the following categories:");
        message.setChatId(chatId);
        message.setReplyMarkup(inlineKeyboardMarkup);
        sender.execute(message);
    }

    private boolean isTextNullAndEquals(String text, String command) {
        return text != null && text.equalsIgnoreCase(command);
    }
}
