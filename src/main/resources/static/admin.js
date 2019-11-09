window.onload = function () {
    getAllSectionsAndTopics();
    $( function() {
        $( "#sections" ).sortable({
            revert: true,
            cursor: 'move',
            update: function (event, ui) {
                var data = $(this).sortable('serialize', {key: 'sectionPosition'});
                // var data = $(this).sortable('toArray');
                $('span').text(data);
                // POST to server using $.post or $.ajax
                // $.ajax({
                //     data: data,
                //     type: 'POST',
                //     url: '/api/admin/swapsections'
                // });
            }
        });

    } );
};

function getAllSectionsAndTopics() {
    fetch("/api/admin/allsectionsandsubsections", {
        method: "GET",
        headers: {'Content-type': 'application/json;charset=UTF-8'}
    })
        .then(function(response){
            if (response.status !== 200) {
                alert('Error receiving data. Status Code: ' +  response.status);
                return;
            }
            response.json().then(function (data) {
                window.allSectionsAndSubSections = data;
                showAllSectionsAndTopics();
            });
        })
        .catch(function(err) {
            console.log('Fetch Error :-S', err);
        });
}

function showAllSectionsAndTopics() {
    let li = '';
    let sectionsLength = window.allSectionsAndSubSections.length;
    for (let i = 0; i < sectionsLength; i += 1) {
        let section = window.allSectionsAndSubSections[i];
        li += `<li id="item-${section.position}">${section.name}</li>`;
    }
    document.getElementById('sections').innerHTML = li;
}

