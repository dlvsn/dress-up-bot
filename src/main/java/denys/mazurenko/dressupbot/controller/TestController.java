package denys.mazurenko.dressupbot.controller;

import denys.mazurenko.dressupbot.dto.AdvertisementDto;
import denys.mazurenko.dressupbot.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {
    private final AdvertisementService advertisementService;

    @GetMapping("/test")
    public List<AdvertisementDto> test() {
        return advertisementService.findAllAdds();
    }
}
