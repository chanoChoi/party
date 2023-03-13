//페이지 로딩될때 실행되는 함수입니다
$(document).ready(function () {
    getProfile();

});


function getProfile() {
    $.ajax({
        type: "GET",
        url: `/api/users/profile`,
        headers: {
            "Authorization": getCookieValue('Authorization')
        },
        error(error, response) {
            alert(response.msg)
            console.error(error);
            console.error(response);
        },
        success: function (response) {
            alert("프로필정보 가져오기 성공")
            console.log(response.data)
            if (response.code === 200) {
                let comment = response['data']['comment'];
                let nickname = response['data']['nickName'];
                let email = response['data']['email'];
                let participationCount = response['data']['participationCount'];
                let phoneNum = response['data']['phoneNum'];

                $('#nickname').append(nickname);
                $('#message').append(comment);

                $('#email').append(email);
                $('#phoneNum').append(phoneNum);
                $('#participationCount').append(participationCount);


            }


        }

    })

}


function updateProfile() {
    //다른곳 코드를 이용해서 해봤는데 아직 테스트도 안해봄 추후에 테스트밑 수정 필요
    //
    // let imageUrl = $('#image-input-text').val();
    // let nickname = $('#nickname-input-text').val();
    // let comment = $('#comment-input-text').val();
    // let newpassword = $('#new-password').val();
    // let confirmpassword = $('#confirm-password').val();
    // let nowpassword = $('#now-password').val();
    // //빈 입력값 확인, 수정비번-재확인비번 일치확인
    // if (nickname == '') {
    //     alert('닉네임를 입력해주세요');
    //     return;
    // } else if (nowpassword == '') {
    //     alert('현재 비밀번호를 입력해주세요');
    //     return;
    // } else if (newpassword == confirmpassword) {
    //     alert('수정 비밀번호와 확인 비밀번호가 불일치합니다.');
    //     return;
    // }
    // $.ajax({
    //     type: "PATCH",
    //     url: `/api/users/profile`,
    //     contentType: "application/json",
    //     data: JSON.stringify({
    //         imageUrl: imageUrl, nickname: nickname, comment: comment,
    //         newpassword: newpassword, confirmpassword: confirmpassword, nowpassword: nowpassword
    //     }),
    //     success: function (response, status, xhr) {
    //         if (response === 'success') {
    //             let host = window.location.host;
    //             let url = host + '/api/index';
    //
    //             document.cookie =
    //                 'Authorization' + '=' + xhr.getResponseHeader('Authorization') + ';path=/';
    //             window.location.href = 'http://' + url;
    //         } else {
    //             alert('프로필 수정 적용을 실패했습니다')
    //             window.location.reload();
    //         }
    //     }
    // })
}