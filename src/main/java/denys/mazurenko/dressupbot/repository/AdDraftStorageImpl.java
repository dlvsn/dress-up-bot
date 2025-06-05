package denys.mazurenko.dressupbot.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdDraftStorageImpl implements AdDraftStorage {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveDraft(String key, Object value) {
        String keyPrefix = key + ":";
        redisTemplate.opsForValue().set(keyPrefix, value);
    }

    @Override
    public Object getDraft(String key) {
        String keyPrefix = key + ":";
        return redisTemplate.opsForValue().get(keyPrefix);
    }
}
