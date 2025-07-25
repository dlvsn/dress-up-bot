package denys.mazurenko.dressupbot.service.externalApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import denys.mazurenko.dressupbot.dto.external.ImgBBResponseDto;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class ImgBBClientImpl implements ImgBBClient {
    private static final String BASE_URL ="https://api.imgbb.com/1/upload";
    private final Request.Builder requestBuilder;
    private final MultipartBody.Builder multipartBodyBuilder;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public ImgBBClientImpl(Request.Builder requestBuilder,
                           MultipartBody.Builder multipartBodyBuilder,
                           OkHttpClient okHttpClient,
                           ObjectMapper objectMapper,
                           @Value("${igm.bb.api.key}")
                           String apiKey) {
        this.requestBuilder = requestBuilder;
        this.multipartBodyBuilder = multipartBodyBuilder;
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
    }

    @Override
    public ImgBBResponseDto uploadImage(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File don't found " + file.getPath());
        }
        RequestBody requestBody = buildRequestBody(file);
        Request request = buildRequest(requestBody);
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code());
            } else {
                return objectMapper.readValue(response.body().string(), ImgBBResponseDto.class);
            }
        }
    }

    private RequestBody buildRequestBody(File file) {
        return multipartBodyBuilder.setType(MultipartBody.FORM)
                .addFormDataPart("key", apiKey)
                .addFormDataPart("name", String.valueOf(LocalDateTime.now()))
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(file,
                                MediaType.parse("image/png")))
                .build();
    }

    private Request buildRequest(RequestBody requestBody) {
        return requestBuilder.url(BASE_URL)
                .post(requestBody)
                .build();
    }
}
