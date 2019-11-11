function test() {

    fetch('http://localhost:8888/api/invite', {method: 'POST'})
        .then(response => response.json())
        .then(key => {
            let p = document.getElementById("innerFieldText");
            p.innerText = "http://localhost:8888/invite?key=" + key;
        });
}

function sendDataUser() {
    fetch('http://localhost:8888/api/users', {
        method: 'POST',
        body: $_GET('key')
    });
}

function $_GET(key) {
    let p = window.location.search;
    p = p.match(new RegExp(key + '=([^&=]+)'));
    return p ? p[1] : false;
}

function sendInviteOnMail() {
    let field = document.getElementById("mail");
    let mail = field.value;
    if (mail != 0) {
        fetch('http://localhost:8888/api/invite/onmail', {method: 'POST', body: mail});
        field.value = "";
        alert("Приглашение успешно отправлено на почту: " + mail);
    } else {
        alert("Адрес отправки не указан!");
    }


}