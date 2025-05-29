package denys.mazurenko.dressupbot.bot;

import org.telegram.abilitybots.api.sender.SilentSender;

public interface MessageHandler {
    void sendOnStart(SilentSender sender, Long chatId, String userName);
}
