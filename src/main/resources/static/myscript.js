window.onload = function () {
    getAllSectionsAndTopics();
};

function getAllSectionsAndTopics() {
    fetch("/api/admin/allsectionsandtopics", {
        method: "GET",
        headers: {'Content-type': 'application/json;charset=UTF-8'}
    })
        .then(function(response){
            if (response.status !== 200) {
                alert('Error receiving data. Status Code: ' +  response.status);
                return;
            }

            response.json().then(function (data) {
                // console.log(data);
                window.allSectionsAndTopics = data;
                showAllSectionsAndTopics();
            });

        })
        .catch(function(err) {
            console.log('Fetch Error :-S', err);
        });
}

function showAllSectionsAndTopics() {
    let li = '';
    for (let i = 0; i < window.allSectionsAndTopics.length; i += 1) {
        let section = window.allSectionsAndTopics[i].section;
        let topics = window.allSectionsAndTopics[i].topics;
        li += `<li id="${section.position}"><span>${section.name}</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`;
        if (i !== 0) {
            li += `<button onclick="upSection(this)">+</button>&nbsp;`;
        }
        if (i !== window.allSectionsAndTopics.length - 1) {
            li += `<button onclick="downSection(this)">-&nbsp;</button>`;
        }
        li += `<ul>`;
        for (let j = 0; j < topics.length; j += 1) {
            let topic = topics[j];
            li += `<li id="${section.position}.${topic.id}">${topic.name}</li>`;
        }
        li += `</ul></li>`;
    }
    document.getElementById('sections').innerHTML = li;
}

function downSection(btn) {
    // здесь fetch на изменение данных в базе
    // после изменение на экране

    let arr = window.allSectionsAndTopics;
    let index = btn.parentElement.id - 1;
    [arr[index + 1], arr[index]] = [arr[index], arr[index + 1]];
    arr[index].section.position = index + 1;
    arr[index + 1].section.position = index + 2;
    showAllSectionsAndTopics();
}

function upSection(btn) {
    // здесь fetch на изменение данных в базе
    // после изменение на экране

    let arr = window.allSectionsAndTopics;
    let index = btn.parentElement.id - 1;
    [arr[index - 1], arr[index]] = [arr[index], arr[index - 1]];
    arr[index].section.position = index + 1;
    arr[index - 1].section.position = index;
    showAllSectionsAndTopics();
}
