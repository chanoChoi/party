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

//partypost 상세정보 조회
function get_partypost(postId) {
    $('#partypost').empty()
    $.ajax({
        type: "GET",
        url: '/api/party-posts/' + postId,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success: function (response) {
            const token = getCookieValue('Authorization');
            const tokenParts = token.split('.');
            const tokenPayload = JSON.parse(atob(tokenParts[1]));
            const loginUserId = tokenPayload.id;
            console.log(loginUserId)

            let responseData = response['data']
            console.log(response)
            let userId = responseData['userId'] //파티장Id
            let nickname = responseData['nickname']
            let profileImg = responseData['profileImg']
            let title = responseData['title']
            let content = responseData['content']
            let categoryId = responseData['categoryId']
            let categoryName = responseData['categoryName']
            let status = responseData['status']
            let acceptedMember = responseData['acceptedMember']
            let maxMember = responseData['maxMember']
            let partyDate = responseData['partyDate']
            let closeDate = responseData['closeDate']
            let address = responseData['address']
            let detailAddress = responseData['detailAddress']
            let partyPlace = responseData['partyPlace']
            let viewCnt = responseData['viewCnt']
            let joinMember = responseData['joinMember']
            let tempHtml = `

            <!-- 파티장이 아닌 일반 유저가 보는 페이지 -->
            <div id="partypost">
                <section class="py-5">
                    <div class="container px-5 my-5">
                        <div class="row gx-5">
                            <div class="col-lg-3">
                                <div class="d-flex align-items-center mt-lg-5 mb-4">
                                    <img class="circle" src="${profileImg}" />
                                    <div class="ms-3">
                                        <div class="fw-bold">파티장 닉네임: ${nickname}</div>
                                        <button class="btn btn-warning rounded-pill" onclick="otherProfilePageClick(${userId}, ${loginUserId})">유저정보
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-9">
                                <!-- Post content-->
                                <article>
                                    <!-- Post header-->
                                    <header class="mb-4 mt-5">
                                        <!-- Post title-->
                                        <h1 class="fw-bolder mb-1">${title}</h1>
                                        <!-- Post meta content-->
                                        <div class="fw-bold fst-italic mb-2 fs-5 text-end">마감일자 : ${closeDate}</div>
                                        <div class="text-muted fst-italic text-end">조회수: ${viewCnt}</div>
                                        <!-- Post categories-->
                                        <a class="badge bg-secondary text-decoration-none link-light">모집 상태: ${status}</a>
                                        <a class="badge bg-secondary text-decoration-none link-light">카테고리 : ${categoryName}</a>
                                    </header>
                                    <!-- Post content-->
                                    <section>
                                        <div class="card bg-light mb-3">
                                            <div class="card-body my-2 fs-2">
                                                ${content}
                                            </div>
                                        </div>
                                    </section>
                                    <!-- Post feature-->
                                        <div>
                                            <a class="badge bg-secondary text-decoration-none link-light p-sm-3 rounded-pill float-sm-end ms-2" onclick="clickReportPartyPost(${postId})">&nbsp;&nbsp; 신고 &nbsp;&nbsp;</a>
                                            <a class="badge bg-secondary text-decoration-none link-light p-sm-3 rounded-pill float-sm-end" onclick="clicklike(${postId})">좋아요</a>
                                        </div>
                                        <div class ="mt-6 mb-5">
                                            <p class="fs-5 fw-bold">주소: ${address} ${detailAddress}</p>
                                            <p class="fs-5 fw-bold">장소: ${partyPlace}</p>
                                            <p class="fs-5 fw-bold">모임 일자: ${partyDate} </p>
                                            <p class="fs-5 fw-bold">모집 인원: ${acceptedMember} / ${maxMember}</p>
                                            <p class="fs-5 fw-bold">현재 파티원: </p>
                                        <div id="joinmember"></div>
                                        </div>
                                </article>
                                <div id="setbutton">
                                <a class="btn-long btn-primary btn-xl-long rounded-pill" onclick="clickParticipation(${postId})">신청</a>
                           </div>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
                `

            if (loginUserId === userId) {
                tempHtml = `

            <!-- 파티장이 보는 페이지 -->
            <div id="partypost">
                <section class="py-5">
                    <div class="container px-5 my-5">
                        <div class="row gx-5">
                            <div class="col-lg-3">
                                <div class="d-flex align-items-center mt-lg-5 mb-4">
                                    <img class="circle" src="${profileImg}" />
                                    <div class="ms-3">
                                        <div class="fw-bold">파티장 닉네임: ${nickname}</div>
                                        <button class="btn btn-warning rounded-pill" onclick="otherProfilePageClick(${userId}, ${loginUserId})">유저정보
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="col-lg-9">
                                <!-- Post content-->
                                <article>
                                    <!-- Post header-->
                                    <header class="mb-4 mt-5">
                                        <!-- Post title-->
                                        <h1 class="fw-bolder mb-1">${title}</h1>
                                        <!-- Post meta content-->
                                        <div class="fw-bold fst-italic mb-2 fs-5 text-end">마감일자 : ${closeDate}</div>
                                        <div class="text-muted fst-italic text-end">조회수: ${viewCnt}</div>
                                        <!-- Post categories-->
                                        <a class="badge bg-secondary text-decoration-none link-light">모집 상태: ${status}</a>
                                        <a class="badge bg-secondary text-decoration-none link-light">카테고리 : ${categoryName}</a>
                                    </header>
                                    <!-- Post content-->
                                    <section>
                                        <div class="card bg-light mb-3">
                                            <div class="card-body my-2 fs-2">
                                                ${content}
                                            </div>
                                        </div>
                                    </section>
                                    <!-- Post feature-->
                                    <section class="mb-5 mt-6">
                                        <p class="fs-5 fw-bold">주소: ${address} ${detailAddress}</p>
                                        <p class="fs-5 fw-bold">장소: ${partyPlace}</p>
                                        <p class="fs-5 fw-bold">모임 일자: ${partyDate}</p>
                                        <p class="fs-5 fw-bold">모집 인원: ${acceptedMember} / ${maxMember}</p>
                                        <p class="fs-5 fw-bold">현재 파티원: </p>
                                        <div id="joinmember"></div>
                                    </section>
                                </article>
                                <div id="setbutton">
                                <a class="btn-long btn-primary btn-xl-long rounded-pill" onclick="clickUpdate(${postId})">수정하기</a>
                                <a class="btn-long btn-primary btn-xl-long rounded-pill mt-3" onclick="deletePostClick(${postId})">삭제하기</a>
                           </div>
                            </div>
                        </div>
                    </div>
                </section>
            </div> `

            }


            console.log("포스트 정보" + nickname, title, content, profileImg, categoryName, status, acceptedMember, maxMember, partyDate,
                closeDate, address, detailAddress, partyPlace, viewCnt, joinMember)

            $('#partypost').append(tempHtml)

            //참가신청한 멤버 불러오기
            let joinMemberRows = response['data']['joinMember']
            for (let j = 0; j < joinMemberRows.length; j++) {
                let applicationId = joinMemberRows[j]['id']
                let memberUserId = joinMemberRows[j]['userId'] //신청한 유저 Id
                let memberNickname = joinMemberRows[j]['nickname']
                let memberProfileImg = joinMemberRows[j]['profileImg'] //파티원 프로필 이미지
                let memberStatus = joinMemberRows[j]['status']

                let partypost_member_temp_html = `
                    <div class="container" onclick="">
                        <div class="d-flex align-items-center mb-1">
                            <img class="circle-joinmember" src="${memberProfileImg}"/>
                            <div class="ms-3">
                                <div class="fw-bold mb-2">파티원 닉네임: ${memberNickname} / 수락 상태: ${memberStatus}</div>
                                <button class="btn btn-warning rounded-pill" onclick="otherProfilePageClick(${memberUserId})">유저정보
                                </button>
                            </div>
                        </div>
                            `

                $('#joinmember').append(partypost_member_temp_html)
            }
        },
        error: function (error) {
            alert("오류가 발생했습니다.")
            window.history.back();
        }
    });
}

//수정페이지로 옮겨가기
function clickUpdate(postId) {
    //postId값을 주소에 넣는다
    const updatePartypostUrl = `/page/partypost/edit?postId=` + postId;
    //수정페이지로 이동
    window.location.href = updatePartypostUrl;

}

//참여신청
function clickParticipation(postId) {
    $.ajax({
        type: "POST",
        url: '/api/applications/join/' + postId,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success(response) {
            console.log(response)
            alert("참가신청이 완료됐습니다. 파티장의 응답을 기다려주세요.")
            window.location.reload()
        },
        error(response) {
            console.log(response)
            alert("오류가 발생했습니다")
            window.history.back()
        }


    })
}

// 모집글에 좋아요
function clicklike(postId) {
    $.ajax({
        type: "POST",
        url: '/api/party-posts/' + postId + '/likes',
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success(response) {
            console.log(response)
            alert(response.msg)
            window.location.reload()
        },
        error(response) {
            console.log(response)
            alert("오류가 발생했습니다")
            window.history.back()
        }
    })
}


function clickReportPartyPost(PostId) {
    $.ajax({
        type: "POST",
        url: '/api/restriction/report/party-posts',
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        contentType: "application/json; charset=UTF-8",
        data: JSON.stringify({
            postId: PostId,
            reason: "SPAM",
            detailReason: "광고"
        }), success(response) {
            console.log(response)
            alert("모집글 신고가 완료되었습니다.")
            window.location.reload()
        }, error(request, status, error) {
            console.log(request);
            console.log(status);
            console.log(error);
            alert("모집글 신고 오류가 발생했습니다")
            // window.history.back()
        }

    })
}

//특정유저 상세정보 페이지로 가기
function otherProfilePageClick(userId, loginUserId) {
    console.log(userId)
    console.log(loginUserId)
    if (userId === loginUserId) {
        window.location.href = `/page/myPage/profile`;
    } else {
        const otherProfilePageUrl = `/page/others/profile?userId=` + userId;
        window.location.href = otherProfilePageUrl;
    }
}

//모집글 삭제하기
function deletePostClick(postId) {
    $.ajax({
        type: "DELETE",
        url: `/api/party-posts/${postId}`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error(error) {
            console.log("아래는 error 임")
            console.log(error)
            alert("해당 모집글은 삭제가 불가능합니다.")
        },
        success: function () {
            alert("모집글 삭제완료")
            window.location.href = `/page/indexPage`
        }

    })
}