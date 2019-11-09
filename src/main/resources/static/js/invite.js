function test() {

    fetch('http://localhost:8888/api/invite', {method: 'POST'})
        .then(response => response.json())
        .then(key => {
            let p = document.getElementById("innerFieldText");
            p.innerText = "http://localhost:8888/invite?key=" + key;
        })
        .catch(error => alert("The invitation has already been sent"));

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