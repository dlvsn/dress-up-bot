package denys.mazurenko.dressupbot.service.externalApi;

import denys.mazurenko.dressupbot.dto.external.ImgBBResponseDto;

import java.io.File;
import java.io.IOException;

public interface ImgBBClient {
    ImgBBResponseDto uploadImage(File file) throws IOException, InterruptedException;
}
