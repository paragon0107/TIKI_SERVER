package com.tiki.server.team.dto.response;

import static lombok.AccessLevel.PRIVATE;

import com.tiki.server.team.entity.Team;

import lombok.Builder;

@Builder(access = PRIVATE)
public record TeamCreateResponse(
        long teamId
) {

    public static TeamCreateResponse from(Team team) {
        return TeamCreateResponse.builder()
                .teamId(team.getId())
                .build();
    }
}
