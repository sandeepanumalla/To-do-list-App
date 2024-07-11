const Stomp = require('stompjs');
const SockJS = require('sockjs-client');
const axios = require("axios");


const serverUrl = 'http://localhost:8085/ws'; // Adjust the WebSocket URL accordingly
const username = 'user'; // Set your username

const socket = new SockJS(serverUrl);

// Create a Stomp client over the SockJS connection
const stompClient = Stomp.over(socket);

// const response = signIn();

async function signIn() {
    try {
        const response = await axios.get('http://localhost:8086/hello',
            {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic YWRtaW46cGFzc3dvcmQ='
            }
        });
        return response.data.token; // Assuming the response contains a token
    } catch (error) {
        console.error('Error signing in:', error);
        throw error; // Rethrow the error for error handling further up the chain
    }
}

async function main() {
    try {
        const response = await signIn();
        console.log(response);
    } catch (error) {
        console.error('Error:', error);
    }
}

main();
;


stompClient.connect({}, (frame) => {

})

const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyIiwiaXNzIjoic2FuZGVlcCIsInJvbGUiOiJ0cnVlIiwiaWF0IjoxNzEzMDQ0NDc1LCJleHAiOjE3OTk0NDQ0NzV9.xkwyi379XzvYBPC5GTrkFigR4j07gUWYHapi9mocbpY";
stompClient.connect({
    'Authorization': 'Bearer '+ token,
}, (frame) => {
    console.log('Connected to STOMP over SockJS');

    stompClient.send('/app/topic/message', {}, JSON.stringify({
        sender: username,
        type: 'JOIN'
    }));

    // Send a STOMP message
    stompClient.send('/app/message', {}, JSON.stringify({
        sender: username,
        content: 'Hello, STOMP over HTTP!',
        type: 'CHAT'
    }));

    // Subscribe to a STOMP destination
    stompClient.subscribe('/topic/notification', (message) => {
        const receivedMessage = JSON.parse(message.body);
        console.log('Received message:', receivedMessage);

        // Add your assertions here to validate the received message.
    });
});
