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

    public void addFile(File file) {
        files.add(file);
    }

    public void clearFileStorage() {
        files.forEach(this::deleteFiles);
        files.clear();
    }

    private boolean deleteFiles(File file) {
        System.out.println(file.exists());
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
