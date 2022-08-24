package com.example.demo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class NewsController {
	
	public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	//method for client subscription
	@CrossOrigin
	@RequestMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
	public SseEmitter subscribe() throws IOException {
		SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
		sseEmitter.send(sseEmitter.event().name("INIT"));
		sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
		emitters.add(sseEmitter);
		return sseEmitter;
	}
	
	//method for dispatching events to all clients
	@PostMapping(value = "/dispatchevent")
	public void dispatchEventToClients(@RequestParam String title, @RequestParam String text ) {
		
		String eventFormatted = new JSONObject().put("title", title).put("text", text).toString();
		for( SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("latestNews").data(eventFormatted));
			}catch (IOException e) {
				emitters.remove(emitter);
			}
			
		}
	}
}
