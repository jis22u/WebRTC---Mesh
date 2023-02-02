package com.webrtc.mesh.socket;


import com.corundumstudio.socketio.SocketIOClient;
import com.webrtc.mesh.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


// 7


@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {



	/*
	 서버에서 브라우저로 보내
	 sendSocketOffer()
	 */
	public void sendSocketOffer(SocketIOClient senderClient, Message sdpMessage) {
		System.out.println("7. SocketService - 채팅쳤을 때 : saveMessage");

		for (
				SocketIOClient client : senderClient.getNamespace().getRoomOperations(sdpMessage.getRoomName()).getClients()) {
			if (!client.getSessionId().equals(senderClient.getSessionId())) {
				client.sendEvent("offer", sdpMessage);
			}
		}
	}

	/*
	서버에서 브라우저로 보내
	sendSocketAnswer()
	*/
	public void sendSocketAnswer(SocketIOClient senderClient, Message sdpMessage) {
		System.out.println("7. SocketService - 채팅쳤을 때 : saveMessage");

		for (
				SocketIOClient client : senderClient.getNamespace().getRoomOperations(sdpMessage.getRoomName()).getClients()) {
			if (!client.getSessionId().equals(senderClient.getSessionId())) {
				client.sendEvent("answer", sdpMessage);
			}
		}
	}

}