function createAlbum() {
    let titleField = document.getElementById("albumTitle");
    let title = titleField.value;
    let listAlbums = document.getElementById("listAlbums");
    if (title == "") {
        title = "Без имени";
    }
    listAlbums.innerHTML += "<li>" + title + "</li>";
    fetch('http://localhost:8888/api/albums', {
        method: 'POST',
        body: title
    });
}