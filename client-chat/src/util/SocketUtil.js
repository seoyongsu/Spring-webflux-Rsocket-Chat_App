import { RSocketConnector } from "rsocket-core";
import { WebsocketClientTransport } from "rsocket-websocket-client";

async function createClient(){
    const outputDiv = document.querySelector("#output");

    const connector = new RSocketConnector({
        transport: new WebsocketClientTransport({
            url: "ws://localhost:9090",
            wsCreator: (url) => new WebSocket(url),
        }),
    });

    const rsocket = await connector.connect();


};

export default createClient;