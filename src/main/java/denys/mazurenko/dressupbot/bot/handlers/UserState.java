package denys.mazurenko.dressupbot.bot.handlers;

public enum UserState {
    IDLE,
    START,
    AD_CATEGORY,
    AWAITING_PHOTO,
    AWAITING_CATEGORY_NAME,
    NEXT_RECEIVED,
    PHOTO_RECEIVED,
    AWAITING_ITEM_NAME,
    AWAITING_DESCRIPTION,
    AWAITING_PRICE,
    COMPLETED,
    FIND,
    FIND_SELECTION,
    FIND_ALL,
    FIND_BY_CATEGORY
}
