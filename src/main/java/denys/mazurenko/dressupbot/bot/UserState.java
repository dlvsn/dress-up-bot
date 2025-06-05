package denys.mazurenko.dressupbot.bot;

public enum UserState {
    IDLE,
    START,
    AWAITING_PHOTO,
    NEXT_RECEIVED,
    PHOTO_RECEIVED,
    AWAITING_ITEM_NAME,
    AWAITING_DESCRIPTION,
    AWAITING_PRICE,
    COMPLETED,
    FIND
}
