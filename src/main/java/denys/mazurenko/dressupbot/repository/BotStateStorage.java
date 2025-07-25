package denys.mazurenko.dressupbot.repository;

import denys.mazurenko.dressupbot.bot.handlers.UserState;

public interface BotStateStorage {
    UserState getChatState(Long chatId);

    void updateChatState(Long chatId, UserState userState);

    void removeChatState(Long chatId);

    boolean isUserActive(Long chatId);
}
