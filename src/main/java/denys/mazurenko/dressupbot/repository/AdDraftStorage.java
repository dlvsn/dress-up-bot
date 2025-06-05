package denys.mazurenko.dressupbot.repository;

public interface AdDraftStorage {
    void saveDraft(String key, Object value);

    Object getDraft(String key);
}
