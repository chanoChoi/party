$(document).ready(function () {
    getBlockList()
    getCategories()
});

// 블락리스트 불러오기
function getBlockList() {
    $('#block-list').empty()

    $.ajax({
        type: "GET",
        url: `/api/restriction/blocks`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        data: {
          page: 1
        },
        error(error, response) {
            alert("차단 유저가 없습니다")
            console.log(error)
            console.log(response)
        },
        success: function (response) {
            console.log("받아왔음" + response.data)
            if (response.code === 200) {
                let rows = response['data']
                for (let i = 0; i < rows.length; i++) {
                    let blockedId = rows[i]['blockedId'];
                    let nickname = rows[i]['nickname']

                    let block_list_temp =
                        `
                    <div class="mypartypost">
                    <div id="myblocklist" class="myblocklist">
                    닉네임 : ${nickname}
                    <button class="btn btn-warning rounded-pill" onclick="unBlock(${blockedId})">차단 해제
                    </button>
                    <button class="btn btn-warning rounded-pill" onclick="otherProfilePageClick(${blockedId})">유저정보
                    </button>
                    </div>
                    </div>
                        `
                    $('#block-list').append(block_list_temp)
                }
            }
        }
    })
}

//차단 해제
function unBlock(blockedId) {
    $.ajax({
        type: "DELETE",
        url: `/api/restriction/blocks/` + blockedId,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error(error, response) {
            alert(response.msg)
            console.log(error)
            console.log(response)
        },
        success: function (response) {
            alert(response.msg)
            window.location.reload()
        }
    })
}

function otherProfilePageClick(userId) {
    console.log(userId)
    const otherProfilePageUrl = `/page/others/profile?userId=` + userId;

    // 검색 결과 페이지로 이동합니다.
    window.location.href = otherProfilePageUrl;
}

