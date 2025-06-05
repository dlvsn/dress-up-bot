package denys.mazurenko.dressupbot.service;

import denys.mazurenko.dressupbot.dto.ImageResponseDto;
import java.io.File;
import java.util.List;

public interface ImageService {
    List<ImageResponseDto> saveImages(List<File> files);
}
