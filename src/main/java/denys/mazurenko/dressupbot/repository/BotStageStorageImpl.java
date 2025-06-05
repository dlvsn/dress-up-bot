package denys.mazurenko.dressupbot.repository;

import denys.mazurenko.dressupbot.bot.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotStageStorageImpl implements BotStateStorage {
    private final static String CHAT_STATE_KEY = "chatState:%d";
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserState getChatState(Long chatId) {
        String key = String.format(CHAT_STATE_KEY, chatId);
        String userState = (String) redisTemplate.opsForValue().get(key);
        return UserState.valueOf(userState);
    }

    @Override
    public void updateChatState(Long chatId, UserState userState) {
        String key = String.format(CHAT_STATE_KEY, chatId);
        redisTemplate.opsForValue().set(key, userState.name());
    }

    @Override
    public void removeChatState(Long chatId) {
        String key = String.format(CHAT_STATE_KEY, chatId);
        redisTemplate.delete(key);
    }

    @Override
    public boolean isUserActive(Long chatId) {
        return getChatState(chatId) != null;
    }
}
