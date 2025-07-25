package denys.mazurenko.dressupbot.bot.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
@Getter
public class TempFileStorage {
    private final List<File> files;

    public boolean addFile(File file) {
        return files.add(file);
    }

    public void clearFileStorage() {
        files.clear();
    }
}
