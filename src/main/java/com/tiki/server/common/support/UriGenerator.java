package com.tiki.server.common.support;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class UriGenerator {

	public static URI getUri(String path, long id) {
		return ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path(path + id)
			.buildAndExpand()
			.toUri();
	}

	public static URI getUri(String path) {
		return ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path(path)
			.buildAndExpand()
			.toUri();
	}
}
