package denys.mazurenko.dressupbot.bot.handlers;

public interface UpdateHandlerProvider {
    UpdateHandler getUpdateHandler(String key);
}
