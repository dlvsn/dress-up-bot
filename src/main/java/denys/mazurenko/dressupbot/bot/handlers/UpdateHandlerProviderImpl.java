package denys.mazurenko.dressupbot.bot.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateHandlerProviderImpl implements UpdateHandlerProvider {
    private final List<UpdateHandler> updateHandlers;

    @Override
    public UpdateHandler getUpdateHandler(String key) {
        return updateHandlers.stream()
                .filter(h -> h.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }
}
