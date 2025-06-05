package denys.mazurenko.dressupbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public MultipartBody.Builder multipartBodyBuilder() {
        return new MultipartBody.Builder();
    }

    @Bean
    public Request.Builder request() {
        return new Request.Builder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
