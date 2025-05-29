package denys.mazurenko.dressupbot.bot;

import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.sender.SilentSender;

@Component
public class MessageHandlerImpl implements MessageHandler {

    @Override
    public void sendOnStart(SilentSender sender, Long chatId, String userName) {
        sender.send(String.format(MessageUtil.message, userName), chatId);
    }

    private static class MessageUtil {
        private static final String message = "Hello, %s. Welcome to DressUpBot";
    }
}
