package com.seulmae.seulmae.workplace.controller;

import com.seulmae.seulmae.workplace.service.WorkplaceFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workplace/file/v1")
public class WorkplaceFileController {

    private final WorkplaceFileService workplaceFileService;

    @GetMapping("")
    public ResponseEntity<?> getWorkplaceImage(@RequestParam Long workplaceImageId) throws IOException {
        return workplaceFileService.getWorkplaceImage(workplaceImageId);
    }

}
