# WebSoket과 Roket의 차이

WebSocket과 RSocket은 모두 양방향 통신을 지원하는 프로토콜입니다. 하지만 두 프로토콜은 목적과 사용 방법, 동작 방식 등에서 차이가 있습니다.

WebSocket은 브라우저와 서버 간의 양방향 통신을 가능하게 하는 프로토콜입니다. 클라이언트가 서버에 HTTP 요청을 보내면, 서버는 응답으로 WebSocket 연결을 열고 이후 메시지를 주고 받을 수 있습니다. WebSocket은 단순하며, HTTP와 유사한 요청-응답 모델을 사용하기 때문에 일반적인 웹 애플리케이션에서 사용됩니다. 또한, WebSocket은 HTTP 연결을 재사용하고, 메시지의 프레임을 끊어서 보낼 수 있기 때문에, 대규모 실시간 애플리케이션에서 많은 양의 데이터를 처리하는 데 적합합니다.

반면에 RSocket은 다양한 언어와 프레임워크에서 사용할 수 있는 Reactive Streams를 기반으로 한 브로커리스, 다중 패턴, 다중 프로토콜, 양방향 스트림 통신을 지원하는 프로토콜입니다. RSocket은 전이중, 요청-응답, 요청-스트림, 스트림-요청 및 채널 모드의 다양한 통신 패턴을 지원하며, 메시지 프로토콜로는 바이너리, JSON 및 Protobuf를 지원합니다. RSocket은 애플리케이션 간에 메시지의 흐름을 조절할 수 있기 때문에, 대규모 실시간 애플리케이션에서 유용합니다.

따라서, WebSocket은 HTTP 연결을 기반으로 하며, 단순하고 사용하기 쉬우며, 대부분의 웹 애플리케이션에서 사용됩니다. 반면에, RSocket은 Reactive Streams 기반의 브로커리스, 다중 패턴, 다중 프로토콜, 양방향 스트림 통신을 지원하며, 메시지의 흐름을 조절할 수 있기 때문에, 대규모 실시간 애플리케이션에서 유용합니다.

# Spring-webflux-Rsocket-Chat_App

해당 예제는 
Auth-service
	Spring Webflux,
	oauth2
	spring security
	Jwt
	의 간단한 예제를 포함하고 있습니다.
	

chat-service-rsocket
	Spring webflux 및 Rsocket을 활용한
	Rsocket server
	Reactive API의 간단한 예제를 포함하고 있습니다.
	해당 예제에서는 Handler Function을 사용하지 않고,
	RestController를 사용하였습니다.
	

chat-service-websocket
	Spring webflux와
	WebSocket을 활용한 간단한 메세지 서비스를 작성하였으며
	
	
client-chat (Client)
	React를 활용하여 간단한 화면 구성을 하였습니다.