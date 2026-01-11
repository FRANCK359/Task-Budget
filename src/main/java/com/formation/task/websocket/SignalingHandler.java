package com.formation.task.websocket;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignalingHandler extends TextWebSocketHandler {

    // roomId -> set of sessionIds
    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();

    // sessionId -> session
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // sessionId -> userId or username (optionnel)
    private final Map<String, String> sessionUser = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject json = new JSONObject(message.getPayload());
        String type = json.optString("type");

        switch (type) {
            case "join": handleJoin(session, json); break;
            case "leave": handleLeave(session, json); break;
            case "offer":
            case "answer":
            case "candidate":
                forwardToTarget(json);
                break;
            case "chat":
                broadcastToRoom(json);
                break;
            default:
                // unknown
        }
    }

    private void handleJoin(WebSocketSession session, JSONObject json) throws IOException {
        String roomId = json.getString("room");
        String username = json.optString("username", session.getId());

        sessionUser.put(session.getId(), username);

        rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session.getId());

        // Build participants list (exclude current)
        JSONArray participants = new JSONArray();
        for (String sid : rooms.get(roomId)) {
            if (!sid.equals(session.getId())) {
                JSONObject p = new JSONObject();
                p.put("sessionId", sid);
                p.put("username", sessionUser.get(sid));
                participants.put(p);
            }
        }

        // send participants to the new comer
        JSONObject reply = new JSONObject();
        reply.put("type", "participants");
        reply.put("participants", participants);
        session.sendMessage(new TextMessage(reply.toString()));

        // notify others that new user joined
        JSONObject notify = new JSONObject();
        notify.put("type", "new-join");
        notify.put("sessionId", session.getId());
        notify.put("username", username);
        notify.put("room", roomId);
        broadcastExcept(roomId, session.getId(), notify);
    }

    private void handleLeave(WebSocketSession session, JSONObject json) throws IOException {
        String roomId = json.getString("room");
        removeFromRoom(session.getId(), roomId);
    }

    private void forwardToTarget(JSONObject json) throws IOException {
        // messages must contain "to" field = target sessionId
        String to = json.getString("to");
        WebSocketSession target = sessions.get(to);
        if (target != null && target.isOpen()) {
            target.sendMessage(new TextMessage(json.toString()));
        }
    }

    private void broadcastToRoom(JSONObject json) throws IOException {
        String roomId = json.getString("room");
        Set<String> members = rooms.getOrDefault(roomId, Collections.emptySet());
        for (String sid : members) {
            WebSocketSession s = sessions.get(sid);
            if (s != null && s.isOpen() && !s.getId().equals(json.optString("from"))) {
                s.sendMessage(new TextMessage(json.toString()));
            }
        }
    }

    private void broadcastExcept(String roomId, String excludeSessionId, JSONObject message) {
        Set<String> members = rooms.getOrDefault(roomId, Collections.emptySet());
        for (String sid : members) {
            if (sid.equals(excludeSessionId)) continue;
            WebSocketSession s = sessions.get(sid);
            try {
                if (s != null && s.isOpen()) s.sendMessage(new TextMessage(message.toString()));
            } catch (IOException e) { /* log */ }
        }
    }

    private void removeFromRoom(String sessionId, String roomId) {
        Set<String> members = rooms.get(roomId);
        if (members != null) {
            members.remove(sessionId);
            String username = sessionUser.get(sessionId);
            // notify remaining
            JSONObject left = new JSONObject();
            left.put("type", "participant-left");
            left.put("sessionId", sessionId);
            left.put("username", username);
            left.put("room", roomId);
            broadcastExcept(roomId, sessionId, left);
            if (members.isEmpty()) rooms.remove(roomId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        sessionUser.remove(session.getId());

        // remove from all rooms
        rooms.forEach((roomId, set) -> {
            if (set.remove(session.getId())) {
                JSONObject left = new JSONObject();
                left.put("type", "participant-left");
                left.put("sessionId", session.getId());
                left.put("room", roomId);
                broadcastExcept(roomId, session.getId(), left);
            }
            if (set.isEmpty()) rooms.remove(roomId);
        });
    }
}

