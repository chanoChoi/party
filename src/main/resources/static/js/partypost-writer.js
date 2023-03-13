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

//페이지 로딩될때 실행되는 함수입니다
$(document).ready(function () {
    get_writer_category();

    // timepicker 옵션 설정
    $("#time").timepicker({
        timeFormat: 'HH:mm',
        interval: 60,
        minTime: '7',
        maxTime: '22:00',
        defaultTime: '11',
        startTime: '07:00',
        dynamic: false,
        dropdown: true,
        scrollbar: false
    });

    // 날짜 선택 제한
    // 현재 시간 이전은 선택할 수 없도록 제한
    const now = new Date();
    const currentDate = now.toISOString().slice(0, 10);
    document.getElementById("date").setAttribute("min", currentDate);

    // 오늘부터 일주일 뒤 까지 선택 가능하도록 제한
    const weekLater = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
    const weekLaterDate = weekLater.toISOString().slice(0, 10);
    document.getElementById("date").setAttribute("max", weekLaterDate);

    // //카카오 지도 관련
    const container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
    mapOption = {
        center: new kakao.maps.LatLng(33.450701, 126.570667), //지도의 중심좌표.
        level: 3
    };
});

// 취소 버튼 클릭 시 이전 페이지로 이동
function cancel() {
    window.history.back();
}

// json 만들어서 보내는 함수 만들기
function sendPartyPost() {
    $.ajax(
        {
            type: "POST",
            url: `/api/party-posts`,
            headers: {
                "Authorization": getCookieValue('Authorization')
            },
            contentType: "application/json; charset=UTF-8",
            data: JSON.stringify({
                "title": XSSCheck($('#title').val()),
                "content": XSSCheck($('#content').val()),
                "categoryId": XSSCheck($('#category').val()),
                "maxMember": XSSCheck($('#max-member').val()),
                "partyDate": $('#date').val() + ' ' + $('#time').val(),
                "partyAddress": XSSCheck($('#place-address').val()),
                "partyPlace": XSSCheck($('#place-name').val())
            }),
            success: function (json) {
                console.log("갔어요")
                console.log(json)
                alert(json.msg)

                let host = window.location.host;
                let url = host + '/page/indexPage';
                window.location.href = 'http://' + url;


            },
            error(error, response) {
                console.log("안갔어요")
                console.log(response)
                console.log(error)
                if (response.msg === undefined) {
                    alert("값을 모두 입력해주세요")

                } else if (response.msg !== null) {
                    alert(response.msg)
                } else {
                    alert("문제가 발생했습니다. 관리자에게 문의해주세요.")
                }

            }
        })
}

//카테고리 가져오기
function get_writer_category() {
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

                    $('#category').append(category_temp)
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