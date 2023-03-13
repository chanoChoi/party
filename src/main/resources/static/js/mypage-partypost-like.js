$(document).ready(function () {
    getCategories();
    $('#mypartypost-like-list').empty();
    getPartyPostLike();
});

// 좋아요한 모집글 불러오기
function getPartyPostLike() {
    $.ajax({
        type: "GET",
        url: `/api/party-posts/likes`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error(error, response) {
            alert("좋아요한 모집글이 없습니다")
            console.log(error)
            console.log(response)
        },
        success: function (response) {
            console.log("받아왔음" + response.data)
            if (response.code === 200) {
                let rows = response['data']
                for (let i = 0; i < rows.length; i++) {
                    let postId = rows[i]['postId'];
                    let title = rows[i]['title'];
                    let partyOwner = rows[i]['partyOwner']
                    let partyDate = rows[i]['partyDate']
                    let partyAddress = rows[i]['partyAddress']

                    let like_partyPost_temp =
                        `<div id="mypartypost" class="mypartypost">
                                [${postId}] 제목: ${title} / 모임날짜 : ${partyDate} / 지역 : ${partyAddress}
                                <button class="btn btn-warning rounded-pill" onclick="movePost(${postId})">글이동</button>
                                <button class="btn btn-primary rounded-pill" onclick="dislike(${postId})">좋아요 취소</button>
                            </div>`

                    $('#mypartypost-like-list').append(like_partyPost_temp)
                }

            }


        }
    })
}

//좋아요 취소
function dislike(postId) {
    $.ajax({
        type: "POST",
        url: `/api/party-posts/${postId}/likes`,
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

//좋아요 취소
function movePost(postId) {
    window.location.href = `/page/partypost?partypostId=${postId}`;
}