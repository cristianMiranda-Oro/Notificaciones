package com.example.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	//public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	public Map<String, SseEmitter> emitters = new HashMap<>();
	
	
	//method for client subscription
	@CrossOrigin
	@RequestMapping(value = "/subscribe", consumes = MediaType.ALL_VALUE)
	public SseEmitter subscribe(@RequestParam String userID) throws IOException {
		SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
		
		sendInitEvent(sseEmitter);
		//sseEmitter.send(SseEmitter.event().name("INIT"));
		
		sseEmitter.send(SseEmitter.event().name("latestNews").data("hello world"));
		
		emitters.put(userID, sseEmitter);
		
		//emitters.add(sseEmitter);
		sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
		sseEmitter.onTimeout(() -> emitters.remove(sseEmitter));
		sseEmitter.onError((e) -> emitters.remove(sseEmitter));
		return sseEmitter;
	}
	
	private void sendInitEvent(SseEmitter sseEmitter) throws IOException {
		sseEmitter.send(SseEmitter.event().name("INIT"));
	}
	
	//method for dispatching events to all clients
	@PostMapping(value = "/dispatchevent")
	public void dispatchEventToClients(@RequestParam String title, 
			@RequestParam String text, @RequestParam String userID ) {
		
		String eventFormatted = new JSONObject().put("title", title).put("text", text).toString();
		
		SseEmitter sseEmitter = emitters.get('4');
		
		System.out.print("****************"+emitters);
		
		if(sseEmitter != null) {
			try {
				sseEmitter.send(SseEmitter.event().name("latestNews").data(eventFormatted));
			}catch (IOException e) {
				emitters.remove(sseEmitter);
			}
		}
		
		/*for( SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("latestNews").data(eventFormatted));
			}catch (IOException e) {
				emitters.remove(emitter);
			}
		}*/
	}
}
