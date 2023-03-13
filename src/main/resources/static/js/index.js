jQuery(document).ready(function () {
    LoginCheck();
    getCategories();
    getHotPartyPost()
    getNearPartyPost()
})

function LoginCheck() {
    $.ajax({
        type: "GET",
        url: `/api/users/loginCheck`,
        contentType: "application/json",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success: function () {
            // 로그인함
            $('#signupBtn').hide();
            $('#loginBtn').hide();
            $('#logoutBtn').show();
            $('#mypageBtn').show();
        }, error() {
            //로그인 안함
            $('#signupBtn').show();
            $('#loginBtn').show();
            $('#logoutBtn').hide();
            $('#mypageBtn').hide();
        }
    })
}


// 토큰값 분리
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

// 검색할 문자를 가져와 , 주소 이동과함께 같이 보냄
function handleSearchButtonClick() {
    // input 요소에서 검색어를 가져옵니다.
    const searchText = document.getElementById("search").value;
    // 검색어를 인코딩합니다.
    const encodedSearchText = encodeURIComponent(searchText);
    // 검색 결과 페이지 URL을 생성합니다. ex)/search?searchText=검색어
    const searchResultPageUrl = `/page/search?searchText=` + encodedSearchText;
    // 검색 결과 페이지로 이동합니다.
    window.location.href = searchResultPageUrl;
}

function partypostClick(postId) {
    $.ajax({
        type: "GET",
        url: `/api/users/loginCheck`,
        contentType: "application/json",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success: function () {
            // 로그인함
            const partypostPageUrl = `/page/partypost?partypostId=` + postId;
            window.location.href = partypostPageUrl;


        }, error: function () {
            //로그인 안함
            alert("로그인 페이지로 이동합니다")
            window.location.href = `/page/loginPage`;
        }
    })
}

//조회수 높은 핫한글 모집글 조회
function getHotPartyPost() {

    $('#hotPartyposts').empty()

    $.ajax({
        url: "/api/party-posts/hot",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        type: "GET",
        success: function (response) {
            let responseData = response['data']
            for (let i = 0; i < responseData.length; i++) {
                let obj = responseData[i];
                let postId = obj['postId']
                let title = obj['title']
                let partyOwner = obj['partyOwner']
                let status = obj['status']
                let acceptedMember = obj['acceptedMember']
                let maxMember = obj['maxMember']
                let partyDate = obj['partyDate']
                let closeDate = obj['closeDate']
                let partyAddress = obj['partyAddress']
                let partyPlace = obj['partyPlace']
                let profileImg = obj['profileImg']
                let tempHtml = `
        <div class="col-lg-4 my-5" >
            <div class="card h-100 shadow border-0">
                <div class="card-body p-4" onclick="partypostClick(${postId})">
                    <div class="badge bg-primary bg-gradient rounded-pill mb-2">모집상태 :${status}</div>
                    <a class="text-decoration-none link-dark stretched-link" href="#!"><h5 class="card-title mb-3">${title}</h5></a>
                    <p class="card-text mb-0">위치 정보: ${partyAddress} / ${partyPlace}</p>
                    <p class="card-text mb-0">모임 일자: ${partyDate}</p>
                    <p class="card-text mb-0">모집 인원 ${acceptedMember}/${maxMember}</p>
                </div>
                <div class="card-footer p-4 pt-0 bg-transparent border-top-0">
                    <div class="d-flex align-items-end justify-content-between">
                        <div class="d-flex align-items-center">
                            <img class="circle" src="${profileImg}" />
                            <div class="small">
                                <div class="fw-bold">파티장 : ${partyOwner}</div>
                                <div class="text-muted">마감일: ${closeDate}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
          `
                $('#hotPartyposts').append(tempHtml)
            }
        }
    });
}

//내 주소 기준으로 주면 모집글 조회
function getNearPartyPost() {
    $('#nearPartyposts').empty()

    let Address = "서울 마포구 연남동"; //임시로 주소 고정 입력
    $.ajax({
        url: "/api/party-posts/near/" + Address,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        type: "GET",
        contentType: "application/json; charset=UTF-8",
        success: function (response) {
            let responseData = response['data']
            console.log("프로필이미지:" + responseData)
            for (let i = 0; i < responseData.length; i++) {
                let obj = responseData[i];
                let postId = obj['postId']
                let title = obj['title']
                let partyOwner = obj['partyOwner']
                let status = obj['status']
                let acceptedMember = obj['acceptedMember']
                let maxMember = obj['maxMember']
                let partyDate = obj['partyDate']
                let closeDate = obj['closeDate']
                let partyAddress = obj['partyAddress']
                let partyPlace = obj['partyPlace']
                let profileImg = obj['profileImg']
                let tempHtml = `
        <div class="col-lg-4 my-5">
            <div class="card h-100 shadow border-0">
                <div class="card-body p-4" onclick="partypostClick(${postId})">
                    <div class="badge bg-primary bg-gradient rounded-pill mb-2">모집상태 :${status}</div>
                    <a class="text-decoration-none link-dark stretched-link"><h5 class="card-title mb-3">${title}</h5></a>
                    <p class="card-text mb-0">위치 정보: ${partyAddress} / ${partyPlace}</p>
                    <p class="card-text mb-0">모임 일자: ${partyDate}</p>
                    <p class="card-text mb-0">모집 인원 ${acceptedMember}/${maxMember}</p>
                </div>
                <div class="card-footer p-4 pt-0 bg-transparent border-top-0">
                    <div class="d-flex align-items-end justify-content-between">
                        <div class="d-flex align-items-center">
                            <img class="circle" src="${profileImg}" />
                            <div class="small">
                                <div class="fw-bold">파티장 : ${partyOwner}</div>
                                <div class="text-muted">마감일: ${closeDate}</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
          `
                console.log(postId)

                $('#nearPartyposts').append(tempHtml)
            }
        }
    });
}

function handleKeyUp(e) {
    if (e.keyCode === 13) {
        handleSearchButtonClick();
    }
}

function clickToPostWriter() {
    $.ajax({
        type: "GET",
        url: `/api/users/loginCheck`,
        contentType: "application/json",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success: function () {
            // 로그인함
            window.location.href = `/page/partypost/writer`


        }, error: function () {
            //로그인 안함
            alert("로그인 페이지로 이동합니다")
            window.location.href = `/page/loginPage`;
        }
    })
}