jQuery(document).ready(function () {
    getPartyPostListAdmin();
})

function getPartyPostListAdmin() {

    $('#partyPostList-Admin').empty();
    $.ajax({
        type: "GET",
        url: `/api/party-post`,
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
                    let partyPostId = response['data'][i]['postId'];
                    let partyOwnerNickName = response['data'][i]['partyOwner'];
                    let title = response['data'][i]['title'];
                    let status = response['data'][i]['status'];

                    let temp_html =
                        `<tr>
                           <td>${partyOwnerNickName}</td>
                           <td>${title}</td>
                           <td>${status}</td>
                          </tr>`
                    $('#partyPostList-Admin').append(temp_html)

                }
            }

        }
    })
}
