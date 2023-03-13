let postId = new URLSearchParams(window.location.search).get('postId');

function getCookieValue(cookieName) {
    var name = cookieName + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var cookieParts = decodedCookie.split(';');
    for (var i = 0; i < cookieParts.length; i++) {
        var cookiePart = cookieParts[i].trim();
        if (cookiePart.indexOf(name) === 0) {
            return cookiePart.substring(name.length, cookiePart.length);
        }
    }
    return "";
}


$(document).ready(function () {
    get_edit_category();
    loadPartyPost(postId);
})

function loadPartyPost(postId) {
    $.ajax({
        type: "GET",
        url: `/api/party-posts/${postId}`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error() {
            alert("오류가 발생했습니다.")
            window.history.back()
        },
        success: function (response) {
            let data = response['data']

            let title = data['title']
            let content = data['content']
            let address = data['address']
            let category = data['categoryId']
            let detailAddress = data['detailAddress']
            let partyPlace = data['partyPlace']

            $('#title').val(title);
            $('#content').val(content);
            $('#place-address').val(address + ' ' + detailAddress)
            $('#place-name').val(partyPlace)
            $('#keyword').val(partyPlace)
            $('#category-edit').val(category)
        }
    })
}

//수정 실행
function submitUpdatePartyPost(postId) {


    $.ajax({
        type: "PATCH",
        url: `/api/party-posts/${postId}`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        contentType: "application/json; charset=UTF-8",
        data: JSON.stringify({
            title: XSSCheck($('#title').val()),
            content: XSSCheck($('#content').val()),
            categoryId: XSSCheck($('#category-edit').val()),
            partyAddress: XSSCheck($('#place-address').val()),
            partyPlace: XSSCheck($('#place-name').val())
        }),
        error(request, response) {
            console.log("아래는 request 임")
            console.log(request)
            console.log("아래는 response 임")
            console.log(response)
            alert(response.msg)
        },
        success: function () {
            alert("모집글 수정완료")
            window.location.href = `/page/partypost?partypostId=` + postId
        }

    })
}

//카테고리 가져오기
function get_edit_category() {
    $.ajax({
        type: "GET",
        url: `/api/categories`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error(error, response) {
            alert(response.msg)
            console.error(error);
            console.error(response);
        },
        success: function (response) {
            console.log(response.data)
            if (response.code === 200) {
                let rows = response['data']
                for (let i = 0; i < rows.length; i++) {
                    console.log(response)
                    console.log(rows)
                    let category_name = rows[i]['name']
                    let category_temp = `<option value= ${i + 1}> ${category_name} </option>`

                    $('#category-edit').append(category_temp)
                }
            }
        }

    });
}

function XSSCheck(str) {
    str = str.replace(/\</g, "&lt;");
    str = str.replace(/\>/g, "&gt;");
    return str;
}