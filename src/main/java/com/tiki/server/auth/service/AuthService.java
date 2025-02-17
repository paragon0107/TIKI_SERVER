package com.tiki.server.auth.service;

import com.tiki.server.auth.dto.request.SignInRequest;
import com.tiki.server.auth.dto.response.SignInGetResponse;
import com.tiki.server.auth.dto.response.ReissueGetResponse;
import com.tiki.server.auth.exception.AuthException;
import com.tiki.server.auth.jwt.JwtProvider;
import com.tiki.server.auth.token.adapter.TokenFinder;
import com.tiki.server.auth.token.adapter.TokenSaver;
import com.tiki.server.auth.token.entity.Token;
import com.tiki.server.member.adapter.MemberFinder;
import com.tiki.server.member.entity.Member;
import com.tiki.server.member.exception.MemberException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tiki.server.auth.jwt.JwtGenerator;
import com.tiki.server.auth.jwt.UserAuthentication;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.thymeleaf.util.StringUtils;

import static com.tiki.server.auth.message.ErrorCode.EMPTY_JWT;
import static com.tiki.server.auth.message.ErrorCode.UNMATCHED_TOKEN;
import static com.tiki.server.member.message.ErrorCode.INVALID_MEMBER;
import static com.tiki.server.member.message.ErrorCode.UNMATCHED_PASSWORD;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtGenerator jwtGenerator;
    private final JwtProvider jwtProvider;
    private final MemberFinder memberFinder;
    private final TokenSaver tokenSaver;
    private final TokenFinder tokenFinder;
    private final PasswordEncoder passwordEncoder;

    public SignInGetResponse signIn(SignInRequest request) {
        val member = checkMemberEmpty(request);
        checkPasswordMatching(member, request.password());
        val authentication = createAuthentication(member.getId());
        val accessToken = jwtGenerator.generateAccessToken(authentication);
        val refreshToken = jwtGenerator.generateRefreshToken(authentication);
        tokenSaver.save(Token.of(member.getId(), refreshToken));
        return SignInGetResponse.from(accessToken, refreshToken);
    }

    public ReissueGetResponse reissueToken(HttpServletRequest request) {
        val refreshToken = jwtProvider.getTokenFromRequest(request);
        checkTokenEmpty(refreshToken);
        val memberId = jwtProvider.getUserFromJwt(refreshToken);
        val token = tokenFinder.findById(memberId);
        checkRefreshToken(refreshToken, token);
        val authentication = createAuthentication(memberId);
        val accessToken = jwtGenerator.generateAccessToken(authentication);
        return ReissueGetResponse.from(accessToken);
    }

    private Member checkMemberEmpty(SignInRequest request) {
        return memberFinder.findByEmail(request.email()).orElseThrow(() -> new MemberException(INVALID_MEMBER));
    }

    private void checkTokenEmpty(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new AuthException(EMPTY_JWT);
        }
    }

    private void checkRefreshToken(String getRefreshToken, Token token) {
        log.info("받은 토큰 : " + getRefreshToken);
        log.info("저장 토큰 : " + token.refreshToken());
        if (!token.refreshToken().equals(getRefreshToken)) {
            throw new AuthException(UNMATCHED_TOKEN);
        }
    }

    private void checkPasswordMatching(Member member, String password) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(UNMATCHED_PASSWORD);
        }
    }

    private Authentication createAuthentication(long memberId) {
        return new UserAuthentication(memberId, null, null);
    }
}
