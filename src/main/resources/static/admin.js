$(function(){
    // после загрузки документа делаем запрос на получение всех секций и подсекций.
    // после получения данныъ отображаем все секции и подсекции
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
                // !!! для разработки - убрать из продакшена
                $("#resultJSON").html(`JSON:<pre>[${data.map(e => "\"" + e.id + "\"")}]</pre>`);
                ////
                let div = '';
                for (let i = 0; i < data.length; i += 1) {
                    let section = data[i];
                    div += `<div class="section" id="${section.id}"><h2 class="sectionTitle">${section.name}</h2></div>`;
                }
                $('.sections').html(div);
            });
        })
        .catch(function(err) {
            console.log('Fetch Error :-S', err);
        });

    // включаем сортировку секций
    // если произошла сортировка секций, то отправляем измененные позиции секий (это массив id-шников элементов)
    // если сервер вернул не 200, то восстанавливаем порядок
    $('.sections').sortable({
        axis: "y",
        update: function () {
            let data = $(this).sortable('toArray');
            // !!! для разработки - убрать из продакшена
            $("#resultJSON").html(`JSON:<pre>${JSON.stringify(data)}</pre>`);
            ////
            fetch("/api/admin/swapsections", {
                method: "PATCH",
                body: JSON.stringify(data),
                headers: {'Accept': 'application/json, text/plain',
                    'Content-type': 'application/json;charset=UTF-8'}
            })
                .then(function(response){
                    if (response.status !== 200) {
                        alert('Error receiving data. Status Code: ' +  response.status);
                        location.reload();
                    }
                })
                .catch(function(err) {
                    console.log('Fetch Error :-S', err);
                });
        }
    });



});

