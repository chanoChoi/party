$(document).ready(function () {
    $('#other-profile').empty()
    getCategories()
    let userId = new URLSearchParams(window.location.search).get('userId');
    if (userId) {
        getOtherProfile(userId); //검색어가 들어왔을때 검색적용
    }
});

//상대방 프로필 조회
function getOtherProfile(userId) {
    $.ajax({
        type: "GET",
        url: `/api/users/profile/` + userId,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success: function (response) {
            console.log("받아왔음" + response.data)
            if (response.code === 200) {
                let rows = response['data']
                let email = rows['email']
                let nickname = rows['nickName']
                let profileImg = rows['profileImg'] //프로필 사진
                let comment = rows['comment']
                let noshowcnt = rows['noshowcnt']
                let participationcnt = rows['participationCount']

                let other_profile_temp =
                    `
            <img class="circle-profile mt-5 mb-3" src="${profileImg}">
            <div id="nickname" class="nickname"><h2>${nickname}</h2></div>
            <div id="email" class="email"><h4>${email}</h4></div>
            <div id="status-message" class="status-message"><h5>상태 메세지</h5>
                <br> ${comment}
            </div>
    
            <div class="otherpartypost-list">
                <div class="otherpartypost mt-6">
                    <strong> 작성한 모집글 </strong>
                </div>
            </div>
            <div class="partypost-list">
                <div class="otherpartypost">
                    <strong> 신청한 모집글: ${participationcnt}</strong>
                </div>
            </div>
    
            <div class="noshowpartypost-list">
                <div class="otherpartypost mb-4">
                    <strong> 노쇼한 모집글 : ${noshowcnt}</strong>
                </div>
            </div>
            <button class="btn btn-warning rounded-pill" onclick="Block(${userId})">해당유저 차단
                        </button>
            <button class="btn btn-primary rounded-pill" onclick="clickReportUser(${userId})">해당유저 신고
            </button>
            `

                console.log("프로필정보" + nickname, comment, email, noshowcnt, participationcnt)

                $('#other-profile').append(other_profile_temp)
            }
        }
    });
}

//차단 등록
function Block(userId) {
    $.ajax({
        type: "POST",
        url: `/api/restriction/blocks/` + userId,
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

//유저 신고
function clickReportUser(userId) {
    $.ajax({
        type: "POST",
        url: `/api/restriction/report/users`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        contentType: "application/json; charset=UTF-8",
        data: JSON.stringify({
            userId: userId,
            reason: 'SPAM',
            detailReason: "광고",
        }),
        success(response) {
            console.log(response)
            alert("유저 신고가 완료되었습니다.")
            window.location.reload()
        },
        error(response, request) {
            console.log(response, request)
            alert("유저 신고 오류가 발생했습니다.")
        }
    });
}