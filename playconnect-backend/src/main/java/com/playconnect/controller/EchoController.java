package com.playconnect.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * TEMPORARY Day 52 verification endpoint — proves the WebSocket/STOMP
 * plumbing actually works before Day 53 builds real chat on top of it.
 * A client sends any text to /app/echo, and everyone subscribed to
 * /topic/echo receives it back. Not a real feature on its own.
 */
@Controller
public class EchoController {

    @MessageMapping("/echo")
    @SendTo("/topic/echo")
    public String echo(String message) {
        return "Echo: " + message;
    }
}