function createAlbum() {
    let titleField = document.getElementById("albumTitle");
    let title = titleField.value;
    let listAlbums = document.getElementById("listAlbums");
    if (title == "") {
        title = "Без имени";
    }
    fetch('http://localhost:8888/api/albums', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({"title": title})
    })
        .then(response => response.json())
        .then(album => {

            listAlbums.innerHTML += "<li><a href='http://localhost:8888/album/" + album.id + "'>" + title + "</a></li>";
        });
}

// function getAllAlbums() {
//     let listAlbums = document.getElementById("listAlbums");
//     fetch('http://localhost:8888/albums')
//         .then(response => response.json())
//         .then(albums => {
//             albums.forEach(album => {
//                 listAlbums.innerHTML += "<li><a href='http://localhost:8888/album/" + album.id + "'>" + album.title + "</a></li>";
//             });
//         });
// }
