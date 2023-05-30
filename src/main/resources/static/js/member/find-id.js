// 이메일
const $emailInput = $(".input-email .input-text");
const $emailError = $(".error");
const $emailInputvalue = $("#memberEmail");

// 이메일 정규식 이벤트 사용 및 함수
$emailInput.on("blur", function(){
    // 이메일 정규식
    let emailPattern = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
    
    /* 이메일 중복 확인 */
    if (!emailPattern.test($emailInput.val())) {
        $emailError.text("이메일 형식이 올바르지 않습니다.");
        $emailError.css("display", "block");
        $emailInput.css("border-color", "#e52929");
    } else {
        $emailError.css("display", "none");
        $emailInput.css("border-color", "#e0e0e0");
    }
});

const $confirmBtn = $('#find-id');
const $mainDiv = $('.common.password-check');
const $successDiv=$(".id-result");

$confirmBtn.on("click", function() {
	let text = '';
	$.ajax({
		url: '/members/checkEmail', //Controller에서 요청 받을 주소
		method: 'POST', //POST 방식으로 전달
		data: {"memberEmail": $emailInputvalue.val()},
		success: function (result) { //컨트롤러에서 넘어온 cnt값을 받는다
			if (result == 0) { //cnt가 1이 아니면(=0일 경우) -> 사용 가능한 이메일
				text = `
				<div class="email-wrap">
                        <p class="headline">${$emailInputvalue.val()}</p>
                        <p >는 아기자기에 등록되어 있지 않은 계정입니다.</p>
                        <div class="inner-container">
                            <button type="button" class="primary block button" onclick="location.href='/member/login'">회원가입하러 가기</button>
                            <button type="button" class="primary block button" onclick="location.href='/member/find-id'">이메일 다시 확인하기</button>
                        </div>
                    </div>`
				$mainDiv.css("display", "none");
				$successDiv.css("display", "block");
			} else { // cnt가 1일 경우 -> 이미 존재하는 이메일
				text = `
				<div class="email-wrap">
                        <p class="headline">${$emailInputvalue.val()}</p>
                        <p class="result-text">
                            회원으로 등록된 이메일 아이디입니다.
                            <br>
                            해당 이메일로 로그인하고 와디즈를 이용하세요!
                        </p>
                        <div class="inner-container">
                            <button type="button" class="primary block button">로그인</button>
                        </div>
                    </div>`
				$mainDiv.css("display", "none");
				$successDiv.css("display", "block");
			}
			$successDiv.html(text);
		},
		error: function () {
			alert("에러 입니다");
		}
	});
});

