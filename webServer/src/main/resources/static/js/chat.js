const url = 'http://localhost:8080';

let stompClient;
let selectedUser;
let userMain;
let newMessages = new Map();

function connectToChat(userName) {
    console.log("connecting to chat...")
    let socket = new SockJS(url + '/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to: " + frame);
        stompClient.subscribe("/topic/messages/" + userName, function (response) {
            let data = JSON.parse(response.body);
             if (selectedUser === data.fromLogin) {
                 /*  у меня мои сообщения*/
                 render(data.text, data.recipientName, data.doc);
             } else {
                 /*  у меня чужие сообщения*/
                newMessages.set(data.fromLogin, data.text, data.doc);
                $('#userNameAppender_' + data.fromLogin).append('<span id="newMessage_' + data.fromLogin + '" style="color: red">+1</span>');
            }
        });
    });
}

function sendMsg(from,text) {
    let recipientName = document.getElementById("recipientName").value;
    var name = document.getElementById('file');


    if(name.files.item(0) === null){
        stompClient.send("/app/chat/" + selectedUser + " " + userMain, {}, JSON.stringify({
            fromLogin: from,
            doc: null,
            recipientName: recipientName,
            text: text
        }));
    }else {
        var formData = new FormData();
        formData.append("file", name.files.item(0));

        $.ajax({
            url: "/upload",
            type: "POST",
            data: formData,
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function (res) {
                console.log(res);
            },
            error: function (err) {
                console.error(err);
            }
        });
        stompClient.send("/app/chat/" + selectedUser + " " + userMain, {}, JSON.stringify({
            fromLogin: from,
            doc: name.files.item(0).name,
            recipientName: recipientName,
            text: text
        }));
    }

}

function savefile(name){
    window.location.href = "/chats/save_file/" + name;
}
function selectUser() {

    let userName = document.getElementById("recipient").value;
    let user = document.getElementById("userName").value;
    console.log("selecting users: " + userName);
    selectedUser = userName;
    userMain = user;
    let isNew = document.getElementById("newMessage_" + userName) !== null;
    if (isNew) {
        let element = document.getElementById("newMessage_" + userName);
        element.parentNode.removeChild(element);
        render(newMessages.get(userName), userName);
    }
    connectToChat(user);
    MessageData();
}
