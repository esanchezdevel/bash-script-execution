package com.esanchez.devel.domain.services;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface BashService {

	public SseEmitter executeCommand(String argument1, String argument2);
}
