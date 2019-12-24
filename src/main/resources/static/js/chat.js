$(document).ready(function () {

    let greeting = $("#greeting"),
        greetingForm = $('#greetingForm'),
        chat = $('#chat'),
        messageForm = $('#messageForm'),
        fileInput = $('#messageForm #file-input'),
        message = $('#message'),
        messageArea = $('#messageArea'),
        connecting = $('.connecting'),
        close = $('.close'),
        usersOnline = $('html .user-list ul'),
        forbiddenForm = $('#messBanId'),
        username = null,
        userAvatar = null,
        replyTo = null,
        stompClient = null,
        page = 0,
        isForbidden = false;


    greetingForm.on('submit', connect);
    messageForm.on('submit', sendMessage);
    close.on('click', disconnect);
    $(window).on('beforeunload', disconnect);


    function getUsersOnline() {
        usersOnline.empty();
        $.get("/api/chat/users", function (data) {
            $.each(data, function (key, value) {
                let item = $('<li>');
                let profileLink = $('<a target="_blank">');
                let username;
                username = key;
                profileLink.attr("href", "http://localhost:8888/profile/" + value);
                profileLink.append(username);
                item.append(profileLink);
                usersOnline.append(item);
            })
        });
    }


    function connect(e) {
        e.preventDefault();
        $.get("/api/chat/isForbidden", function (data) {
            isForbidden = data;
        });
        if (isForbidden) {
            greeting.addClass('hidden');
            forbiddenForm.removeClass('hidden');
        } else {
            $.get("/api/chat/user", function (data) {
                username = data["username"];
                userAvatar = data["ava"];
            });
            if (username) {
                greeting.addClass('hidden');
                chat.removeClass('hidden');
                stompClient = Stomp.over(new SockJS('/ws'));
                stompClient.connect({}, onConnected, onError);
            }
        }
    }

    function disconnect(e) {
        e.preventDefault();
        chat.addClass('hidden');
        greeting.removeClass('hidden');
        stompClient.send("/chat/delUser", {}, JSON.stringify({sender: username, type: 'LEAVE'}));
        stompClient.disconnect();
        page = 0;
    }

    function onConnected() {
        stompClient.subscribe('/channel/public', onMessageReceived, {});
        stompClient.send("/chat/addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}));
        connecting.addClass('hidden');
        messageArea.empty();
        getMessages();
    }

    function onError(er) {
        connecting.text('Не удалось подключиться к серверу. Обновите страницу и попробуйте войти еще раз!');
        connecting.css('color', 'red');
    }

    function sendMessage(e) {
        e.preventDefault();
        let messageContent = message.val().trim();
        let originalImg;
        let thumbnailImg;


        if (fileInput.get(0).files.length !== 0) {
            let formData = new FormData(messageForm[0]);
            $.ajax({
                type: "POST",
                async: false,
                url: '/api/chat/image',
                cache: false,
                contentType: false,
                processData: false,
                data: formData,
                success: function (data) {
                    originalImg = data["originalImg"];
                    thumbnailImg = data["thumbnailImg"];
                }
            });
        }

        if (messageContent || (fileInput.get(0).files.length !== 0)) {
            let chatMessage = {
                sender: username,
                text: message.val(),
                senderAvatar: userAvatar,
                originalImg: originalImg,
                thumbnailImg: thumbnailImg,
                replyTo: replyTo,
                type: 'MESSAGE'
            };
            stompClient.send("/chat/sendMessage", {}, JSON.stringify(chatMessage));
            message.val('');
            fileInput.val('');
            replyTo = null;
        }
    }

    function onMessageReceived(payload) {
        let message = JSON.parse(payload.body);
        drawMessage(message);
        getUsersOnline();
    }

    function drawMessage(message, prepend) {
        let messageElement = $('<li>');
        switch (message.type) {
            case 'JOIN':
                messageElement.addClass('event-message');
                message.text = message.sender + ' присоединился!';
                break;
            case 'LEAVE':
                messageElement.addClass('event-message');
                message.text = message.sender + ' покинул чат!';
                break;
            case 'MESSAGE':
                messageElement.addClass('chat-message');
                if (message.replyTo === username) {
                    messageElement.addClass('reply');
                }
                let avatarElement = $('<i>');
                avatarElement.css("background", "url(/img/" + message.senderAvatar + ")");
                avatarElement.css("background-size", "cover");
                messageElement.append(avatarElement);

                let usernameElement = $('<span class="sender">');
                let usernameText = document.createTextNode(message.sender);
                usernameElement.append(usernameText);
                messageElement.append(usernameElement);

                let dateElement = $('<span class="message-date">');
                let dateText = document.createTextNode(message.messageDate);
                dateElement.append(dateText);
                messageElement.append(dateElement);

                if (message.originalImg !== null) {
                    let link = $('<a target="_blank" class="image-link">');
                    link.attr("href", "http://localhost:8888/img/chat/" + message.originalImg);
                    let img = $('<img>');
                    img.attr("src", "/img/chat/" + message.thumbnailImg);
                    link.append(img);
                    messageElement.append(link);

                }

        }

        let textElement = $('<p>');
        let messageText = document.createTextNode(message.text);
        textElement.append(messageText);
        messageElement.append(textElement);
        if (prepend) {
            messageArea.prepend(messageElement);
        } else {
            messageArea.append(messageElement);
        }
        messageArea.scrollTop(messageArea[0].scrollHeight);
    }

    function getMessages(page) {
        if (page === undefined) {
            page = 0;
        }
        $.ajax({
            url: '/api/chat/messages?page=' + page,
            datatype: 'json',
            async: false,
            type: "get",
            contentType: 'application/json',
            success: function (data) {
                if (page === 0) {
                    for (i = (data.length - 1); i >= 0; i--) {
                        let test = data[i];
                        drawMessage(test);
                    }

                } else {
                    for (i = 0; i < data.length; i++) {
                        let test = data[i];
                        drawMessage(test, true);
                    }
                }

                if ($('#messageArea li').length > 19) {
                    $("<button class='prev-msg'><span class='arrow up'></span></button>").prependTo(messageArea);
                }
            },
            error: function (data) {
                $('#messageArea button.prev-msg').hide();
            }
        });
    }

    $(document).on('click', 'button.prev-msg', function (e) {

        let messageElement = $('#messageArea li'),
            firstMsg = $('#messageArea li:first');

        $(this).hide();
        getMessages(++page);
        messageArea.scrollTop(firstMsg.offset().top);
    });

    $(document).on('click', 'span.sender', function (e) {
        let sender = $(this).text();
        message.val(sender + ', ');
        replyTo = sender;

    });

});