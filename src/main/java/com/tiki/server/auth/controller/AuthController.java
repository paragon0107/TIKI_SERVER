package com.tiki.server.auth.controller;

import com.tiki.server.auth.dto.request.SignInRequest;
import com.tiki.server.auth.dto.response.ReissueGetResponse;
import com.tiki.server.auth.dto.response.SignInGetResponse;
import com.tiki.server.common.dto.SuccessResponse;
import com.tiki.server.common.support.UriGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tiki.server.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

import static com.tiki.server.auth.message.SuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<SuccessResponse<SignInGetResponse>> signIn(@RequestBody SignInRequest request) {
        val response = authService.signIn(request);
        return ResponseEntity.created(UriGenerator.getUri("/"))
                .body(SuccessResponse.success(SUCCESS_SIGN_IN.getMessage(), response));
    }

    @GetMapping("/reissue")
    public ResponseEntity<SuccessResponse<ReissueGetResponse>> reissue(HttpServletRequest httpServletRequest) {
        val response = authService.reissueToken(httpServletRequest);
        return ResponseEntity.created(UriGenerator.getUri("/"))
                .body(SuccessResponse.success(SUCCESS_REISSUE_ACCESS_TOKEN.getMessage(), response));
    }
}
