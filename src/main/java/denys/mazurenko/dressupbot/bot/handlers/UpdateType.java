package denys.mazurenko.dressupbot.bot.handlers;

import lombok.Getter;

@Getter
public enum UpdateType {
    USER_TEXT("userText"),
    ADMIN_TEXT("adminText"),
    PHOTO("photo"),
    CALLBACK_QUERY("callbackQuery");
    private final String type;

    UpdateType(String type) {
        this.type = type;
    }
}
