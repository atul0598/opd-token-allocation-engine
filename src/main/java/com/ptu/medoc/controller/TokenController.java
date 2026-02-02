package com.ptu.medoc.controller;

import com.ptu.medoc.allocation.TokenEngine;
import com.ptu.medoc.dto.BookedTokenResponse;
import com.ptu.medoc.dto.PageTokenResponse;
import com.ptu.medoc.dto.TokenRequest;
import com.ptu.medoc.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Token APIs", description = "OPD Token Allocation APIs")
@RestController
@RequestMapping(
        value = "/api/v1",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class TokenController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private TokenEngine tokenEngine;

// Token Generation End-Point
@Operation(summary = "Allocate a token",
        description = "Allocates a token based on slot availability and priority rules")
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> allocate(
            @Valid @RequestBody TokenRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tokenService.allocate(request));
    }

//    Token Cancellation
    @DeleteMapping("/token/{id}")
    public ResponseEntity<Map<String, Object>> cancelToken(@PathVariable Long id) {
        Map<String, Object> response = tokenEngine.cancelToken(id);

    return ResponseEntity.ok(response);
    }


//    @GetMapping("/token/booked")
//    public PageTokenResponse<BookedTokenResponse> getBookedTokens(
//            @RequestParam(required = false) String startDate,
//            @RequestParam(required = false) String endDate,
//            @RequestParam(required = false, defaultValue = "1") int page,
//            @RequestParam(required = false, defaultValue = "100") int pageSize
//    ) {
//
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//        LocalDate today = LocalDate.now();
//
//        LocalDate startDateFormat = (startDate != null && !startDate.isBlank())
//                ? LocalDate.parse(startDate, formatter)
//                : today;
//
//        LocalDate endDateFormat = (endDate != null && !endDate.isBlank())
//               ? LocalDate.parse(endDate, formatter)
//               : today;
//
//        if(endDateFormat.isBefore(startDateFormat)) {
//            throw new IllegalArgumentException("End date cannot be before start date");
//        }
//
//        // Convert 1-based page → 0-based
//        int pageIndex = Math.max(page - 1, 0);
//
//        Pageable pageable = PageRequest.of(
//                pageIndex,
//                pageSize,
//                Sort.by("createdAt").descending()
//        );
//
//        Page<BookedTokenResponse> pageResult = tokenService.getBookedTokens(
//                startDateFormat.atStartOfDay(),
//                endDateFormat.atTime(LocalTime.MAX),
//                pageable
//        );
//
//        return new PageTokenResponse<>(pageResult);
//    }



    @GetMapping("/token/booked")
    public PageTokenResponse<BookedTokenResponse> getBookedTokens(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int pageSize
    ) {

        LocalDate today = LocalDate.now();

        LocalDate startDateFormat =
                (startDate != null) ? startDate : today;

        LocalDate endDateFormat =
                (endDate != null) ? endDate : today;

        if (endDateFormat.isBefore(startDateFormat)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        // Convert 1-based page → 0-based
        int pageIndex = Math.max(page - 1, 0);

        Pageable pageable = PageRequest.of(
                pageIndex,
                pageSize,
                Sort.by("createdAt").descending()
        );

        Page<BookedTokenResponse> pageResult =
                tokenService.getBookedTokens(
                        startDateFormat.atStartOfDay(),
                        endDateFormat.atTime(LocalTime.MAX),
                        pageable
                );

        return new PageTokenResponse<>(pageResult);
    }


    @PostMapping("/token/{tokenId}/no-show")
    public ResponseEntity<Map<String, Object>> markNoShow(
            @PathVariable Long tokenId
    ) {
        return ResponseEntity.ok(
                tokenEngine.markNoShowAndReassign(tokenId)
        );
    }
}



