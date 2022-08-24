package com.example.demo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NewsController {
	
	public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	//method for client subscription
	@CrossOrigin
	@RequestMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
	public SseEmitter subscribe() throws IOException {
		SseEmitter sseEmitter = new SseEmitter();
		sseEmitter.send(sseEmitter.event().name("INIT"));
		emitters.add(sseEmitter);
		return sseEmitter;
	}
	
	//method for dispatching events to all clients
	
}
