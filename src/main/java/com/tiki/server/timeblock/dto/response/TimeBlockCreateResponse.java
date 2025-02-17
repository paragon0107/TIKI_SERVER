package com.tiki.server.timeblock.dto.response;

import static lombok.AccessLevel.PRIVATE;

import lombok.Builder;

@Builder(access = PRIVATE)
public record TimeBlockCreateResponse(
	long timeBlockId
) {

	public static TimeBlockCreateResponse of(long timeBlockId) {
		return TimeBlockCreateResponse.builder()
			.timeBlockId(timeBlockId)
			.build();
	}
}
