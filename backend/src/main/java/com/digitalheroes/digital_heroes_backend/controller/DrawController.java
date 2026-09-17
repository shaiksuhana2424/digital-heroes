package com.digitalheroes.digital_heroes_backend.controller;

import com.digitalheroes.digital_heroes_backend.service.DrawService;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/draws")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175"
})
public class DrawController {

    private final DrawService drawService;

    public DrawController(DrawService drawService) {
        this.drawService = drawService;
    }

    @GetMapping("/current")
    public Object getCurrent() {
        return drawService.getCurrent();
    }

    @GetMapping("/history")
    public Object getHistory() {
        return drawService.getHistory();
    }

    @PostMapping("/simulate")
    public Object simulate(
            @RequestParam String adminEmail,
            @RequestParam(required = false) String month
    ) {
        YearMonth drawMonth =
                month == null || month.isBlank()
                        ? YearMonth.now()
                        : YearMonth.parse(month);

        return drawService.simulate(adminEmail, drawMonth);
    }

    @PostMapping("/{drawId}/publish")
    public Object publish(
            @PathVariable Long drawId,
            @RequestParam String adminEmail
    ) {
        return drawService.publish(adminEmail, drawId);
    }
}