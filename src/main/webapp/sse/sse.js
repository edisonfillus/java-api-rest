var stream;

function connect(){
    // Open a connection

    const host = document.location.host;
    const url = "http://" + host + "/api/events";

    stream = new EventSource(url);

    // When a connection is made
    stream.onopen = function () {
        console.log('Opened connection ');
        var log = document.getElementById("log");
        log.innerHTML += "Opened connection\n";
    };

    // A connection could not be made
    stream.onerror = function (event) {
        console.log(event);
        stream.close();
    };

    stream.addEventListener("updates", function(event) {
        var log = document.getElementById("log");
        var message = JSON.parse(event.data);
        log.innerHTML += message.id + " : " + message.ts + "\n";
      }, false);


    // When data is received
    stream.onmessage = function(event) {
        console.log("message");
        console.log(event.data);
        var log = document.getElementById("log");
        var message = JSON.parse(event.data);
        log.innerHTML += message.from + " : " + message.content + "\n";
    };

    // A connection was closed
    stream.onclose = function(code, reason) {
        console.log(code, reason);
    }

    // Close the connection when the window is closed
    window.addEventListener('beforeunload', function () {
        stream.close();
    });
}