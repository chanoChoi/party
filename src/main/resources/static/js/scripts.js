//쿠키값 가져오는 함수(cookieName 자리에 Authorization사용)
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

// 로그아웃 메소드
function logout() {
    $.ajax({
        type: "POST",
        url: `/api/users/signout`,
        contentType: "application/json",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success: function () {
            document.cookie = 'Authorization=; expires=Thu, 01 Jan 1970 00:00:01 UTC; path=/page;'
            document.cookie = 'rfToken=; expires=Thu, 01 Jan 1970 00:00:01 UTC; path=/;'
            let host = window.location.host;
            let url = host + '/page/loginPage';
            window.location.href = 'http://' + url;
        }
    })

}


// 상단 카테고리 메뉴 불러오기
function getCategories() {
    $.ajax({
        type: "GET",
        url: `/api/categories`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error(error, response) {
            console.error(error);
            console.error(response);
            return 0
        },
        success: function (response) {
            console.log(response.data)
            if (response.code === 200) {
                let rows = response['data']
                for (let i = 0; i < rows.length; i++) {
                    let category_name = rows[i]['name']
                    let category_id = rows[i]['id']
                    let category_temp = `<li class="nav-item"><a class="nav-link" onclick="gotoCategory(${category_id})">${category_name} |</a></li>`

                    $('#categories').append(category_temp)
                }
            }

            return 1
        }
    });
}

//카테고리별 조회로 넘어가기
function gotoCategory(categoryId) {
    window.location.href = `/page/search?categoryId=` + categoryId;
}

// 1:1채팅 버튼 클릭시
function clickChat() {
    alert("현재 1:1 채팅은 미구현 상태입니다")
}