$(document).ready(function () {
    getCategories();
    $('#mypartypost-apply-list').empty();
    getPartyPostApply();
});

//참석한 모집글 불러오기
function getPartyPostApply() {
    $.ajax({
        type: "GET",
        url: `/api/party-posts/my-join-list`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error(error, response) {
            alert(response.msg)
            console.log(error)
            console.log(response)
        },
        success: function (response) {
            console.log(response.data)
            if (response.code === 200) {
                let rows = response['data']
                for (let i = 0; i < rows.length; i++) {
                    let partyPostId = response['data'][i]['id'];
                    let OwnerUserId = response['data'][i]['ownerUserId']
                    let title = response['data'][i]['title'];
                    let closeDate = response['data'][i]['closeDate'];
                    let address = response['data'][i]['address'];
                    let status = response['data'][i]['status'];
                    let temp_html = `
                            <div id="myAppliyPartyPost" class="mypartypost">
                            ${partyPostId} 제목 : ${title} / 마감일 : ${closeDate} / 지역 : ${address} / 모집글상태 : ${status}
                            <button class="btn btn-warning rounded-pill" onclick="movePost(${partyPostId})">글이동</button>                
                            </div>`

                    $('#mypartypost-apply-list').append(temp_html);

                    //NO_SHOW_REPORTING 활성상태인경우 파티장 노쇼 버튼 활성화
                    if (status === '노쇼 투표 진행중') {
                        let owner_noshow_report_btn_temp = `<button class="btn btn-warning rounded-pill" onclick="clickNoShowReportApply(${OwnerUserId}, ${partyPostId})">파티장 노쇼!</button>`
                        $('#mypartypost-apply-list').append(owner_noshow_report_btn_temp);
                    }

                    let joinMemberRows = response['data'][i]['joinMember']
                    for (let j = 0; j < joinMemberRows.length; j++) {
                        let applicationId = joinMemberRows[j]['id']
                        let memberUserId = joinMemberRows[j]['userId']
                        let memberNickname = joinMemberRows[j]['nickname']
                        let memberStatus = joinMemberRows[j]['status']
                        let noShowCnt = joinMemberRows[j]['noShowCnt']

                        //end 상태인 경우의 기본 양식
                        let partypost_member_notPending_temp_html = `
                        <div class="user" id="user">[${applicationId}] 참여자 - 닉네임: ${memberNickname} / 신청 상태 : ${memberStatus} / 노쇼포인트 : ${noShowCnt}
                    </div>`

                        //NO_SHOW_REPORTING 상태인경우 노쇼 신고버튼 활성화
                        let noshow_report_btn_temp_html = `<div class="user" id="user">참여자 -  [${applicationId}] ${memberNickname} / 신청 상태 : ${memberStatus} / 노쇼포인트 : ${noShowCnt}
                    <button class="btn btn-warning rounded-pill" onclick="clickNoShowReportApply(${memberUserId}, ${partyPostId})">노쇼했어요!</button></div>`


                        if (status === '노쇼 투표 진행중') {
                            $('#mypartypost-apply-list').append(noshow_report_btn_temp_html);
                        } else {
                            $('#mypartypost-apply-list').append(partypost_member_notPending_temp_html);
                        }

                        console.log("신고되는 유저 " + applicationId + "/" + memberUserId + "/" + memberNickname + "/" + memberStatus + "/" + noShowCnt);
                    }


                }
            }

        }
    })
}

function movePost(postId) {
    window.location.href = `/page/partypost?partypostId=${postId}`;
}

//노쇼신고
function clickNoShowReportApply(userId, partyPostId) {
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