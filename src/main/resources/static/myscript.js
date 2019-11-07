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
    let sectionsLength = window.allSectionsAndTopics.length;
    for (let i = 0; i < sectionsLength; i += 1) {
        let section = window.allSectionsAndTopics[i].section;
        let topics = window.allSectionsAndTopics[i].topics;
        let btnPlusShow = (i == 0) ? 'style="visibility: hidden;"' : "";
        let btnMinusShow = (i == sectionsLength - 1) ? 'style="visibility: hidden;"' : "";
        li +=
`<li id="${section.position}">
    <button class="btnPlus" onclick="swapSections(this)" ${btnPlusShow}>+</button>&nbsp;
    <button class="btnMinus" onclick="swapSections(this)" ${btnMinusShow}>-&nbsp;</button>&nbsp;&nbsp;&nbsp;
    <span  style="font-weight: bold">${section.name}</span><ul>`;

        for (let j = 0; j < topics.length; j += 1) {
            let topic = topics[j];
            li += `<li id="${section.position}.${topic.id}">${topic.name}</li>`;
        }
        li += `</ul></li>`;
    }
    document.getElementById('sections').innerHTML = li;
}

function swapSections(btn) {
    let arr = window.allSectionsAndTopics;
    let li = btn.parentElement;
    let btnClass = btn.className;
    let swapSections = {};
    let id1, newPosition, id2, oldPosition;

    if (btnClass === "btnMinus") {
        id1 = arr[li.id - 1].section.id;
        newPosition = +li.id;
        id2 = arr[li.id].section.id;
        oldPosition = newPosition - 1;

        swapSections = {
            "id1": id1,
            "newPosition1": newPosition + 1,
            "id2": id2,
            "newPosition2": newPosition
        };
    }

    if (btnClass === "btnPlus") {
        id1 = arr[li.id - 1].section.id;
        newPosition = li.id - 2;
        id2 = arr[li.id - 2].section.id;
        oldPosition = newPosition + 1;

        swapSections = {
            "id1": id1,
            "newPosition1": newPosition + 1,
            "id2": id2,
            "newPosition2": newPosition + 2
        };
    }

    fetch("/api/admin/swapsections", {
        method: "PATCH",
        body: JSON.stringify(swapSections),
        headers: {'Accept': 'application/json, text/plain',
                  'Content-type': 'application/json;charset=UTF-8'}
    })
        .then(function(response){
            if (response.status !== 200) {
                alert('Error receiving data. Status Code: ' +  response.status);
                return;
            }

            if (btnClass === "btnMinus") {
                [arr[newPosition], arr[oldPosition]] = [arr[oldPosition], arr[newPosition]];
                arr[newPosition].section.position = newPosition + 1;
                arr[oldPosition].section.position = oldPosition + 1;
            }
            if (btnClass === "btnPlus") {
                [arr[newPosition], arr[oldPosition]] = [arr[oldPosition], arr[newPosition]];
                arr[newPosition].section.position = newPosition + 1;
                arr[oldPosition].section.position = oldPosition + 1;
            }
            showAllSectionsAndTopics();
        })
        .catch(function(err) {
            console.log('Fetch Error :-S', err);
        });

}
