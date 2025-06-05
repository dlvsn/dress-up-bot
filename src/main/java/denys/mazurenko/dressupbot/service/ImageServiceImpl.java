package denys.mazurenko.dressupbot.service;

import denys.mazurenko.dressupbot.dto.ImageResponseDto;
import denys.mazurenko.dressupbot.dto.external.ImgBBResponseDto;
import denys.mazurenko.dressupbot.mapper.ImgMapper;
import denys.mazurenko.dressupbot.service.externalApi.ImgBBClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImgBBClient imgBBClient;
    private final ImgMapper imgMapper;

    @Override
    public List<ImageResponseDto> saveImages(List<File> files) {
        List<ImageResponseDto> images = new LinkedList<>();
        for (File file : files) {
            try {
                ImgBBResponseDto responseDto = imgBBClient.uploadImage(file);
                ImageResponseDto imageResponseDto = imgMapper.fromExternalToInternalDto(responseDto);
                images.add(imageResponseDto);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return images;
    }
}
