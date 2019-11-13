$(function(){
    // после загрузки документа делаем запрос на получение всех секций и подсекций.
    // после получения данныъ отображаем все секции и подсекции
    fetch("/api/allsectionsandsubsections", {
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
                $("#resultJSON").html(`JSON:<pre>All sections and subsections</pre>`);
                ////
                let div = '';
                data.forEach(sectionWithSubsectons => {
                    const section = sectionWithSubsectons.section;
                    const sectionId = section.id;
                    // отрисовываем section
                    div += `<div class="section" id="${sectionId}"><h2 class="sectionTitle">${section.name}</h2>`;
                    sectionWithSubsectons.subsections.forEach(subsection => {
                        // отрисовываем subsections в section
                        div += `<div class="subsection" id="${sectionId}.${subsection.id}">${subsection.name}</div>`;
                    });
                    div += '</div>';
                });
                $('.sections').html(div);
                addSortForSubsections();
            });
        })
        .catch(function(err) {
            console.log('Fetch Error :-S', err);
        });

    // включаем сортировку section в sections
    // если произошла сортировка секций, то отправляем измененные позиции секций (это массив id-шников элементов)
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
                        return;
                    }
                })
                .catch(function(err) {
                    console.log('Fetch Error :-S', err);
                });
        }
    });

    // включаем сортировку subsection в section после создания section в sections
    function addSortForSubsections() {
        function assembleData(object, arrSectionsAndSubsections) {
            let data = $(object).sortable('toArray');
            let sectionId = $(object).attr("id");

            if(!arrSectionsAndSubsections.hasOwnProperty(sectionId)) {
                arrSectionsAndSubsections[sectionId] = new Array();
            }

            for (let i = 0; i < data.length; i++) {
                let subsectionId = data[i];
                arrSectionsAndSubsections[sectionId].push(subsectionId);
            }
            return arrSectionsAndSubsections;
        }

        // данные для отправки на сервер
        let arrSectionAndSubsections = {};

        // включаем сортировку subsection в section
        $('.section').sortable({
            connectWith: '.section',
            items : ':not(.sectionTitle)',
            start : function() {
                arrSectionAndSubsections = {};
            },
            remove : function() {
                // Получить массив subsection в section, где мы удалили элемент
                arrSectionAndSubsections = assembleData(this, arrSectionAndSubsections);
            },
            /* That's fired thrird */
            receive : function() {
                // Получить массив subsection в section, где мы добавили элемент
                arrSectionAndSubsections = assembleData(this, arrSectionAndSubsections);
            },
            update: function(event, ui) {
                if (this === ui.item.parent()[0]) {
                    // Если изменение происходит в том же div
                    if (ui.sender == null) {
                        arrSectionAndSubsections = assembleData(this, arrSectionAndSubsections);
                    }
                }
            },
            stop : function() {
                // !!! для разработки - убрать из продакшена
                $("#resultJSON").html(`JSON:<pre>${JSON.stringify(arrSectionAndSubsections)}</pre>`);
                ////

                fetch("/api/admin/swapsubsections", {
                    method: "PATCH",
                    body: JSON.stringify(arrSectionAndSubsections),
                    headers: {'Accept': 'application/json, text/plain',
                        'Content-type': 'application/json;charset=UTF-8'}
                })
                    .then(function(response){
                        if (response.status !== 200) {
                            alert('Error receiving data. Status Code: ' +  response.status);
                            location.reload();
                            return;
                        }
                    })
                    .catch(function(err) {
                        console.log('Fetch Error :-S', err);
                    });
            }
        });
    }
});
