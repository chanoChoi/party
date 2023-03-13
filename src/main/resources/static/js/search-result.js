$(document).ready(function () {
    LoginCheck();
    //검색어 값으로 받음
    let searchText = new URLSearchParams(window.location.search).get('searchText');
    let categoryId = new URLSearchParams(window.location.search).get('categoryId');
    if (searchText) {
        firstsearchPartyPost(searchText); //검색어가 들어왔을때 검색적용
    } else if (categoryId) {
        searchPartyPostCategory(categoryId)
    } else {
        alert("검색값이 들어오지 않았습니다")
        window.history.back();
    }

});

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

function partypostClick(postId) {
    $.ajax({
        type: "GET",
        url: `/api/users/loginCheck`,
        contentType: "application/json",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        success: function () {
            const partypostPageUrl = `/page/partypost?partypostId=` + postId;

            // 검색 결과 페이지로 이동합니다.
            window.location.href = partypostPageUrl;


        }, error: function () {
            //로그인 안함
            alert("로그인 페이지로 이동합니다")
            window.location.href = `/page/loginPage`;
        }
    })

}

//페이지 바로 로딩시 검색어가 들어왔을때
function firstsearchPartyPost(inputVal) {
    $('#searchPartyPostResult').empty();
    $("#search").val(inputVal);
    $.ajax({
        url: "/api/party-posts/search?",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        data: {
            searchText: inputVal
        },
        type: "GET",
        success: function (response) {
            console.log(response);
            let responseData = response['data']
            for (let i = 0; i < responseData.length; i++) {
                let obj = responseData[i];
                let postId = obj['postId'];
                let title = obj['title'];
                let partyOwner = obj['partyOwner'];
                let status = obj['status'];
                let acceptedMember = obj['acceptedMember'];
                let maxMember = obj['maxMember'];
                let partyDate = obj['partyDate'];
                let closeDate = obj['closeDate'];
                let partyAddress = obj['partyAddress'];
                let partyPlace = obj['partyPlace'];
                let profileImg = obj['profileImg'];
                let tempHtml = `
        <div class="col-lg-4 my-5">
            <div class="card h-100 shadow border-0">
                <div class="card-body p-4" onclick="partypostClick(${postId})">
                    <div class="badge bg-primary bg-gradient rounded-pill mb-2">모집상태 :${status}</div>
                    <a class="text-decoration-none link-dark stretched-link" href="#!"><h5 class="card-title mb-3">${title}</h5></a>
                    <p class="card-text mb-0">위치 정보: ${partyAddress} / ${partyPlace}</p>
                    <p class="card-text mb-0">모임일: ${partyDate}</p>
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

                $('#searchPartyPostResult').append(tempHtml)

                $('#pageNavigation').empty();

                tempHtml = `<nav aria-label="Page navigation example">
                                    <ul>
                                        <li><a href="#" onclick="searchPartyPost(1)">1</a></li>
                                        <li><a href="#" onclick="searchPartyPost(2)">2</a></li>
                                        <li><a href="#" onclick="searchPartyPost(3)">3</a></li>
                                        <li><a href="#" onclick="searchPartyPost(4)">4</a></li>
                                        <li><a href="#" onclick="searchPartyPost(5)">5</a></li>
                                        <li><a href="#" onclick="searchPartyPost(6)">6</a></li>
                                        <li><a href="#" onclick="searchPartyPost(7)">7</a></li>
                                        <li><a href="#" onclick="searchPartyPost(8)">8</a></li>
                                        <li><a href="#" onclick="searchPartyPost(9)">9</a></li>
                                        <li><a href="#" onclick="searchPartyPost(10)">10</a></li>
                                    </ul>
                                </nav>`

                $('#pageNavigation').append(tempHtml);

            }
        }
    });
}

//로딩 이후 따로 재검색시 이용
function searchPartyPost(page) {
    var inputVal = document.getElementById("search").value;

    console.log("입력된 값은 " + inputVal + "입니다.");

    $('#searchPartyPostResult').empty()

    $.ajax({
        url: "/api/party-posts/search",
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        data: {
            searchText: inputVal,
            page: page
        },
        type: "GET",
        success: function (response) {
            console.log(response);
            let responseData = response['data']
            for (let i = 0; i < responseData.length; i++) {
                let obj = responseData[i];
                let postId = obj['postId'];
                let title = obj['title'];
                let partyOwner = obj['partyOwner'];
                let status = obj['status'];
                let acceptedMember = obj['acceptedMember'];
                let maxMember = obj['maxMember'];
                let partyDate = obj['partyDate'];
                let closeDate = obj['closeDate'];
                let partyAddress = obj['partyAddress'];
                let partyPlace = obj['partyPlace'];
                let tempHtml = `
        <div class="col-lg-4 my-5">
            <div class="card h-100 shadow border-0">
                <div class="card-body p-4" onclick="partypostClick(${postId})">
                    <div class="badge bg-primary bg-gradient rounded-pill mb-2">모집상태 :${status}</div>
                    <a class="text-decoration-none link-dark stretched-link" href="#!"><h5 class="card-title mb-3">${title}</h5></a>
                    <p class="card-text mb-0">위치 정보: ${partyAddress} / ${partyPlace}</p>
                    <p class="card-text mb-0">모임일: ${partyDate}</p>
                    <p class="card-text mb-0">모집 인원 ${acceptedMember}/${maxMember}</p>
                </div>
                <div class="card-footer p-4 pt-0 bg-transparent border-top-0">
                    <div class="d-flex align-items-end justify-content-between">
                        <div class="d-flex align-items-center">
                            <img class="rounded-circle me-3" src="https://dummyimage.com/40x40/ced4da/6c757d"
                                 alt="..."/>
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

                $('#searchPartyPostResult').append(tempHtml)

                $('#pageNavigation').empty();

                tempHtml = `<nav aria-label="Page navigation example">
                                    <ul>
                                        <li><a href="#" onclick="searchPartyPost(1)">1</a></li>
                                        <li><a href="#" onclick="searchPartyPost(2)">2</a></li>
                                        <li><a href="#" onclick="searchPartyPost(3)">3</a></li>
                                        <li><a href="#" onclick="searchPartyPost(4)">4</a></li>
                                        <li><a href="#" onclick="searchPartyPost(5)">5</a></li>
                                        <li><a href="#" onclick="searchPartyPost(6)">6</a></li>
                                        <li><a href="#" onclick="searchPartyPost(7)">7</a></li>
                                        <li><a href="#" onclick="searchPartyPost(8)">8</a></li>
                                        <li><a href="#" onclick="searchPartyPost(9)">9</a></li>
                                        <li><a href="#" onclick="searchPartyPost(10)">10</a></li>
                                    </ul>
                                </nav>`

                $('#pageNavigation').append(tempHtml);

            }
        }
    });
}

//카테고리 조회로 들어왔을때
function searchPartyPostCategory(categoryId) {
    $('#searchPartyPostResult').empty();
    $.ajax({
        url: `/api/party-posts/categories/${categoryId}`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        type: "GET",
        success: function (response) {
            console.log(response);
            let responseData = response['data']
            for (let i = 0; i < responseData.length; i++) {
                let obj = responseData[i];
                let postId = obj['postId'];
                let title = obj['title'];
                let partyOwner = obj['partyOwner'];
                let status = obj['status'];
                let acceptedMember = obj['acceptedMember'];
                let maxMember = obj['maxMember'];
                let partyDate = obj['partyDate'];
                let closeDate = obj['closeDate'];
                let partyAddress = obj['partyAddress'];
                let partyPlace = obj['partyPlace'];
                let tempHtml = `
        <div class="col-lg-4 my-5">
            <div class="card h-100 shadow border-0">
                <div class="card-body p-4" onclick="partypostClick(${postId})">
                    <div class="badge bg-primary bg-gradient rounded-pill mb-2">모집상태 :${status}</div>
                    <a class="text-decoration-none link-dark stretched-link" href="#!"><h5 class="card-title mb-3">${title}</h5></a>
                    <p class="card-text mb-0">위치 정보: ${partyAddress} / ${partyPlace}</p>
                    <p class="card-text mb-0">모임일: ${partyDate}</p>
                    <p class="card-text mb-0">모집 인원 ${acceptedMember}/${maxMember}</p>
                </div>
                <div class="card-footer p-4 pt-0 bg-transparent border-top-0">
                    <div class="d-flex align-items-end justify-content-between">
                        <div class="d-flex align-items-center">
                            <img class="rounded-circle me-3" src="https://dummyimage.com/40x40/ced4da/6c757d"
                                 alt="..."/>
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

                $('#searchPartyPostResult').append(tempHtml)


                $('#pageNavigation').empty();

                tempHtml = `<nav aria-label="Page navigation example">
                                    <ul>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},1)">1</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},2)">2</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},3)">3</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},4)">4</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},5)">5</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},6)">6</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},7)">7</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},8)">8</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},9)">9</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},10)">10</a></li>
                                    </ul>
                                </nav>`

                $('#pageNavigation').append(tempHtml);
            }
        }
    });
}

//카테고리 조회 페이지별로 재조회
function searchPartyPostCategoryPage(categoryId, page) {
    $('#searchPartyPostResult').empty();
    $.ajax({
        url: `/api/party-posts/categories/${categoryId}`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        data: {
            page: page
        },
        type: "GET",
        success: function (response) {
            console.log(response);
            let responseData = response['data']
            for (let i = 0; i < responseData.length; i++) {
                let obj = responseData[i];
                let postId = obj['postId'];
                let title = obj['title'];
                let partyOwner = obj['partyOwner'];
                let status = obj['status'];
                let acceptedMember = obj['acceptedMember'];
                let maxMember = obj['maxMember'];
                let partyDate = obj['partyDate'];
                let closeDate = obj['closeDate'];
                let partyAddress = obj['partyAddress'];
                let partyPlace = obj['partyPlace'];
                let tempHtml = `
        <div class="col-lg-4 my-5">
            <div class="card h-100 shadow border-0">
                <div class="card-body p-4" onclick="partypostClick(${postId})">
                    <div class="badge bg-primary bg-gradient rounded-pill mb-2">모집상태 :${status}</div>
                    <a class="text-decoration-none link-dark stretched-link" href="#!"><h5 class="card-title mb-3">${title}</h5></a>
                    <p class="card-text mb-0">위치 정보: ${partyAddress} / ${partyPlace}</p>
                    <p class="card-text mb-0">모임일: ${partyDate}</p>
                    <p class="card-text mb-0">모집 인원 ${acceptedMember}/${maxMember}</p>
                </div>
                <div class="card-footer p-4 pt-0 bg-transparent border-top-0">
                    <div class="d-flex align-items-end justify-content-between">
                        <div class="d-flex align-items-center">
                            <img class="rounded-circle me-3" src="https://dummyimage.com/40x40/ced4da/6c757d"
                                 alt="..."/>
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

                $('#searchPartyPostResult').append(tempHtml)
                $('#pageNavigation').empty();

                tempHtml = `<nav aria-label="Page navigation example">
                                    <ul>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},1)">1</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},2)">2</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},3)">3</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},4)">4</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},5)">5</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},6)">6</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},7)">7</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},8)">8</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},9)">9</a></li>
                                        <li><a href="#" onclick="searchPartyPostCategoryPage(${categoryId},10)">10</a></li>
                                    </ul>
                                </nav>`

                $('#pageNavigation').append(tempHtml);
            }
        }
    });
}


function handleKeyUp(e) {
    if (e.keyCode === 13) {
        searchPartyPost(1);
    }
}