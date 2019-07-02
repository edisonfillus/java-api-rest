var ws;

function connect() {
    var username = document.getElementById("username").value;

    const host = document.location.host;
    const url = "ws://" + host + "/chat/" + username;
    //var pathname = document.location.pathname;

    // Create WebSocket connection.
    ws = new WebSocket(url);

    // Connection opened
    ws.addEventListener('open', function (event) {
        var json = JSON.stringify({
            "content": 'Hello Server!'
        });
        ws.send(json);
    });

    // Listen for messages
    ws.onmessage = function (event) {
        var log = document.getElementById("log");
        console.log(event.data);
        var message = JSON.parse(event.data);
        log.innerHTML += message.from + " : " + message.content + "\n";
    };
}

function send() {
    var msg = {
        content: document.getElementById("msg").value
    };
    ws.send(JSON.stringify(msg));
}