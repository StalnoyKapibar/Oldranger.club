**_Message_**
  
{  
 sender: 'username',  
 text: 'some message text',  
 senderAvatar: 'avatar file name',  
 originalImg: 'image name if appended to message',  
 thumbnailImg: 'thumbnail image name if appended to message',  
 replyTo: 'reply to nickName',  
 type: 'MESSAGE' / 'LEAVE' / 'JOIN'  
}  

_Group Chat_  

/ws - endpoint для подключения через SockJs и StompClient  

Через метод send в StompClient:  
/chat/addUser - добавление пользователя в чат. Принимает Message, type 'JOIN'  
/chat/delUser - удаление пользователя. Принимает Message, type 'LEAVE'  
/chat/sendMessage - отправление сообщения на сервер. Принимает Message, type 'MESSAGE'  

_Private Chat_  

/user - endpoint для подключения через SockJs и StompClient  

Через метод send в StompClient:  
/chat/del/{chatToken} - удаление пользователя. Принимает Message, type 'LEAVE'  
/chat/add/{chatToken} - добавление пользователя в чат. Принимает Message, type 'JOIN'  
/chat/send/{chatToken} - отправление сообщения на сервер. Принимает Message, type 'MESSAGE'  