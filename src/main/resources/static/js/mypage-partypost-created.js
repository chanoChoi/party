$(document).ready(function () {
    getCategories();
    $('#mypartypost-created-list').empty();
    getPartyPostCreated();

});

function getPartyPostCreated() {
    $.ajax({
        type: "GET",
        url: `/api/party-posts/mylist`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error(error, response) {
            alert(response.msg)
            console.log(error)
            console.log(response)
        },
        success: function (response) {
            console.log("받아왔음" + response.data)
            if (response.code === 200) {
                let rows = response['data']

                for (let i = 0; i < rows.length; i++) {
                    let temp_html = `<div id="mypartypost"> </div>`
                    $('#mypartypost-created-list').append(temp_html)

                    let partyPostId = response['data'][i]['id'];
                    let title = response['data'][i]['title'];
                    let closeDate = response['data'][i]['closeDate'];
                    let address = response['data'][i]['address'];
                    let status = response['data'][i]['status']

                    let partypost_temp_html = ` <div id = "createdPartyPostInfo" class="mypartypost" > <strong>  [${partyPostId}] 제목 :${title} <button class="btn btn-primary rounded-pill" onclick="movePost(${partyPostId})">글 이동</button><br> 마감일자 : ${closeDate} / 위치 : ${address} / 모집 상태 : ${status}</strong>
                     </div>`
                    $('#mypartypost-created-list').append(partypost_temp_html)


                    let joinMemberRows = response['data'][i]['joinMember']
                    for (let j = 0; j < joinMemberRows.length; j++) {
                        let applicationId = joinMemberRows[j]['id']
                        let memberUserId = joinMemberRows[j]['userId']
                        let memberNickname = joinMemberRows[j]['nickname']
                        let memberStatus = joinMemberRows[j]['status']
                        let noShowCnt = joinMemberRows[j]['noShowCnt']

                        //수락/거절 버튼 있는 양식
                        let partypost_member_temp_html = `
                        <div class="user" id="user">참여자 - 닉네임 : ${memberNickname} / 신청 상태 : ${memberStatus} / 노쇼포인트 : ${noShowCnt}
                        <button class="btn btn-primary rounded-pill" onclick="applicationAccept(${applicationId})">수락</button>
                        <button class="btn btn-dark rounded-pill" onclick="applicationReject(${applicationId})">거절</button>
                    </div>`
                        //노쇼신고버튼 있는 양식
                        let partyPost_member_noshowReporting_temp_html = `
                        <div class="user" id="user">참여자 - 닉네임 : ${memberNickname} / 신청 상태 : ${memberStatus} / 노쇼포인트 : ${noShowCnt}
                        <button class="btn btn-primary rounded-pill" onclick="clickNoShowReport(${memberUserId},${partyPostId})">노쇼했어요!</button>                 
                    </div>
                        `
                        //end 상태인 경우의 양식
                        let partypost_member_notPending_temp_html = `
                        <div class="user" id="user">참여자 - 닉네임 : ${memberNickname} / 신청 상태 : ${memberStatus} / 노쇼포인트 : ${noShowCnt}
                    </div>`

                        //노쇼 리포팅 상태인경우 노쇼 버튼 있게 변경
                        if (memberStatus === '대기중') {
                            $('#mypartypost-created-list').append(partypost_member_temp_html)
                        } else if (status === '노쇼 투표 진행중') {
                            $('#mypartypost-created-list').append(partyPost_member_noshowReporting_temp_html)
                        } else {
                            $('#mypartypost-created-list').append(partypost_member_notPending_temp_html)
                        }


                    }


                }
            }

        }
    })

}

//수락버튼 함수
function applicationAccept(applicationId) {
    $.ajax({
        type: "POST",
        url: `/api/applications/accept/${applicationId}`,
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

//거절버튼 함수
function applicationReject(applicationId) {
    $.ajax({
        type: "POST",
        url: `/api/applications/reject/${applicationId}`,
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

//노쇼신고
function clickNoShowReport(userId, partyPostId) {
    $.ajax({
        type: "POST",
        url: `/api/restriction/noShow`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        contentType: "application/json; charset=UTF-8",
        data: JSON.stringify({
            userId: userId,
            partyPostId: partyPostId
        }),
        error(error, response) {
            alert("이미 노쇼신고 하셨습니다. 그렇지 않은경우 관리자에게 문의해주세요.")
            console.log(error)
            console.log(response)
        },
        success: function (response) {
            alert("노쇼신고 성공")
            console.log(response)
            // window.location.reload()

        }
    })
}

function movePost(postId) {
    window.location.href = `/page/partypost?partypostId=${postId}`;
}