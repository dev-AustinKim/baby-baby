/* 상세보기 모달 ===================== */

function openModal(num, e) {
    var selectedModal = '#guide-detail' + num;
    if (!$(e.target).is('input[type="checkbox"]') && !$(e.target).hasClass('no-modal')) {
        $(selectedModal).show();
    }
    console.log(num+"번 모달 클릭")
}


/* 상세 모달 닫기 */
function closeModal(num) {
    var selectedModal = '#guide-detail' + num;
    $(selectedModal).hide();
}


/* 삭제 모달 ======================== */
const $showDelete = $(".delete-button");
const $cancelDelete = $(".cancel-delete");

// "전체선택" 체크박스 클릭 이벤트
$('#allSelect').click(function() {
    if ($(this).is(':checked')) {
        // 전체선택 체크박스가 체크되었을 때 개별 체크박스 모두 체크
        $('input[name="check"]').prop('checked', true);
    } else {
        // 전체선택 체크박스가 체크해제되었을 때 개별 체크박스 모두 체크해제
        $('input[name="check"]').prop('checked', false);
    }
});

// 삭제 버튼 클릭 이벤트
$(".delete-button").click(function() {
    if ($("input[type=checkbox]:checked").length === 0) {
        // 선택된 체크박스가 없으면 모달 띄우기
        $(".delete-no-check-modal").show();
    } else {
        // 선택된 체크박스가 있으면 모달 띄우기
        $(".delete-modal").show();
    }
});

// 모달 확인 버튼 클릭 시 모달 닫기
$(".confirm-delete, .no-check-confirm-delete").click(function() {
    $(".delete-no-check-modal, .delete-modal").hide();
});

// 모달 취소 버튼 클릭 시 모달 닫기
$(".cancel-delete").click(function() {
    $(".delete-modal").hide();
    $('input[name="check"]').prop('checked', false);
});


//-----------------------------------------ajax--------------------------------------------------

const PAGE_AMOUNT = 10;
const $pageWrap = $(".page-button-box");
const $contentWrap = $(".guide-list");
const $detailContentWrap = $(".guide-modal-list");
const adminGuideSearch = {
    memberName: null,
    guideType : null,
    acceptanceType : null
};

// 장애인 비장애인 카테고리
$('.category-entire').click(function() {
    adminGuideSearch.guideType = null;
    adminGuideSearch.memberName = null;
    adminGuideSearch.acceptanceType = null;
    getAdminGuideList();

    $('.category-entire').css('color', '#c97793');
    $('.category-abled').css('color', 'black');
    $('.category-disabled').css('color', 'black');
    $('.accepted').css('color', 'gray');
    $('.hold').css('color', 'gray');
});

$('.category-abled').click(function() {
    adminGuideSearch.guideType = 'NON_DISABLED';
    getAdminGuideList();

    $('.category-entire').css('color', 'black');
    $('.category-abled').css('color', '#c97793');
    $('.category-disabled').css('color', 'black');
});

$('.category-disabled').click(function() {
    adminGuideSearch.guideType = 'DISABLED';
    getAdminGuideList();

    $('.category-entire').css('color', 'black');
    $('.category-abled').css('color', 'black');
    $('.category-disabled').css('color', '#c97793');
});



// 승인상태
$('.accepted').click(function() {
    adminGuideSearch.acceptanceType = 'ACCEPTED';
    getAdminGuideList();

    $('.accepted').css('color', 'green');
    $('.hold').css('color', 'gray');
});

$('.hold').click(function() {
    adminGuideSearch.acceptanceType = 'HOLD';
    getAdminGuideList();

    $('.accepted').css('color', 'gray');
    $('.hold').css('color', 'red');
});







function getAdminGuideList() {
    $.ajax({
        url: `guideList/${globalThis.page}`,
        data: adminGuideSearch,
        success: function(data) {
            $pageWrap.empty();
            showPage(data);
            $contentWrap.empty();
            showList(data.content);
            updateStatus();
            downloadCertification();
        }

    })
}

globalThis.page = 1;



function findPage(page) {
    globalThis.page = page;
    getAdminGuideList();
}

//검색
$(".search-btn-icon").on("click", function (e) {
    e.preventDefault();
    let val;
    let $search = $(".search-input");

    val = $search.val();

    if ($search.val() === ""){
        val = null
    };

    adminGuideSearch.memberName = val;
    adminGuideSearch.guideType = null;
    adminGuideSearch.acceptanceType = null;

    console.log( adminGuideSearch.eventTitle + "777");
    getAdminGuideList();
    $(".search-input").val("");
});

function showPage(data) {
    let pageable = data.pageable;
    let pageNumber = pageable.pageNumber;
    let totalPages = data.totalPages;
    let count = Math.floor(pageNumber/PAGE_AMOUNT);

    let startPage = count * PAGE_AMOUNT;
    let endPage = startPage + PAGE_AMOUNT;

    endPage = endPage > data.totalPages ? data.totalPages : endPage;

    let hasPrev = startPage > 1;
    // 170 page / 5 = 24 -> 171 /
    let hasNext = endPage < data.totalPages;

    let text = "";


    // Previous button
    if (hasPrev) {
        text += `<div class="">`;
        text += `<div class="page-button-margin">`;
        text += `<div>`;
        text += `<img src="https://cdn3.iconfinder.com/data/icons/google-material-design-icons/48/ic_keyboard_arrow_left_48px-128.png" class="left-button" onclick="findPage(${startPage})" data-page = "${pageNumber}" aria-label="이전 목록">`;
        text += `</div>`;
        text += `</div>`;
        text += `</div>`;
    }

    // Page buttons
    for (let i = startPage + 1; i < endPage + 1; i++) {
        let page = i;
        if (pageNumber  + 1 == page) {
            text += `<div class="page-button-active page-button" onclick="findPage(${i})">`;
        } else {
            text += `<div class="page-button" onclick="findPage(${i})">`;
        }
        text += `<div class="page-button-margin">`;
        text += `<div>`;
        text += `<span>${i}</span>`;
        text += `</div>`;
        text += `</div>`;
        text += `</div>`;
    }

    // Next button
    if (hasNext) {
        text += `<div class="">`;
        text += `<div class="page-button-margin">`;
        text += `<div>`;
        text += `<img src="https://cdn3.iconfinder.com/data/icons/google-material-design-icons/48/ic_keyboard_arrow_right_48px-128.png" class="right-button" onclick="findPage(${endPage+1})" data-page="' + (pageNumber + 1) + '" aria-label="다음 목록">`;
        text += `</div>`;
        text += `</div>`;
        text += `</div>`;
    }

    $pageWrap.html(text);
}


//    통솔자 목록
function showList(guideDTOS) {
    var content = "";
    var detailContent = "";
    guideDTOS.forEach(guide => {

        const guideRegisterDate = formatDate(new Date(guide.memberRegisterDate));
        let changeCategoryName = null;
        let changeAcceptStatus = null;
        let acceptStatusColor = null;
        let disabledStatusColor = null;
        let guideInterestCategory = null;
        // let  fullAddress = eventBoard.eventAddress + ' ' + eventBoard.eventAddressDetail + ' ' + eventBoard.memberAddressSubDetail;

        if (guide.memberGuideType === null) {
            changeCategoryName = '전체';
        } else if (guide.memberGuideType === 'DISABLED'){
            changeCategoryName = '장애인';
            disabledStatusColor = 'purple'
        } else{
            changeCategoryName = '비장애인';
            disabledStatusColor = 'black'
        }

        if (guide.memberGuideStatus === 'HOLD') {
            changeAcceptStatus = '승인 대기';
            acceptStatusColor = 'red';
        } else if (guide.memberGuideStatus === 'ACCEPTED'){
            changeAcceptStatus = '승인 완료';
            acceptStatusColor = 'green';
        } else{
            changeAcceptStatus = '승인 취소';
            acceptStatusColor = 'orange';
        }


        if (guide.memberGuideInterest === 'AGRICULTURE'){
            guideInterestCategory = '농촌';
        } else if (guide.memberGuideInterest === 'ART'){
            guideInterestCategory = '예술';
        } else if (guide.memberGuideInterest === 'TRADITION'){
            guideInterestCategory = '전통';
        } else if (guide.memberGuideInterest === 'CRAFT'){
            guideInterestCategory = '공방';
        } else if (guide.memberGuideInterest === 'SCIENCE'){
            guideInterestCategory = '과학';
        } else if (guide.memberGuideInterest === 'MUSEUM'){
            guideInterestCategory = '박물관';
        } else if (guide.memberGuideInterest === 'SPORTS') {
            guideInterestCategory = '스포츠';
        } else{
            guideInterestCategory = '기타';
        }
        content +=
            `
               <tr class="row" onclick="openModal(${guide.id}, event)">
                    <td>${guide.id}</td>
                    <td>${guide.memberName}</td>
                    <td>${guide.memberNickname}</td>
                    <td>${guide.memberPhone}</td>
                    <td>${guide.memberEmail}</td>
                    <td>${guideRegisterDate}</td>
                    <td class="field abled" style="color: ${disabledStatusColor}">${changeCategoryName}</td>
                    <td class="status waiting" style="color: ${acceptStatusColor}">${changeAcceptStatus}</td>
                </tr>
            `

        detailContent +=
            `
              <section class="user-modal" id="guide-detail${guide.id}">
			<div class="user-modal-shape">
				<div class="modal-header">
					<h4>회원 상세보기</h4>
					<a class="modal-close">
						<svg
							xmlns="http://www.w3.org/2000/svg"
							data-name="Capa 1"
							id="Capa_1"
							viewBox="0 0 20 19.84"
							onclick="closeModal(${guide.id})"
						>
							<path d="M10.17,10l3.89-3.89a.37.37,0,1,0-.53-.53L9.64,9.43,5.75,5.54a.37.37,0,1,0-.53.53L9.11,10,5.22,13.85a.37.37,0,0,0,0,.53.34.34,0,0,0,.26.11.36.36,0,0,0,.27-.11l3.89-3.89,3.89,3.89a.34.34,0,0,0,.26.11.35.35,0,0,0,.27-.11.37.37,0,0,0,0-.53Z"/>
						</svg>
					</a>
				</div>
				<form class="user-info" action>
					<main class="user-detail">
						<h5 class="detail-title">회원 상세정보</h5>
						<div class="profile-img-wrapper">
							<label>
								<div class="content-img">
									<img
										src="/members/display?fileName=Member/profile/${guide.memberProfilePath}/${guide.memberProfileUUID}_${guide.memberProfileOriginalName}"
									/>
								</div>
							</label>
						</div>
						<ul class="content-list-wrap">
							<li class="content-list">
								<span>이름</span>
								<p class="guide-name">${guide.memberName}</p>
							</li>
							<li class="content-list">
								<span>닉네임</span>
								<p class="guide-nickname">${guide.memberNickname}</p>
							</li>
							<li class="content-list">
								<span>휴대전화</span>
								<p class="guide-phone">${guide.memberPhone}</p>
							</li>
							<li class="content-list">
								<span>이메일</span>
								<p class="guide-email">${guide.memberEmail}</p>
							</li>
							<li class="content-list">
								<span>가입일</span>
								<p class="guide-birth">${guideRegisterDate}</p>
							</li>
							<li class="content-list">
								<span>지원분야</span>
								<p class="guide-support-area">${changeCategoryName}</p>
							</li>
							<li class="content-list">
								<span>흥미카테고리</span>
								<p class="guide-interest-category">${guideInterestCategory}</p>
							</li>
							<li class="content-list">
								<span>증명서류</span>
								<a href="/files/download?fileName=Member/profile/${guide.memberFilePath}/${guide.memberFileUUID}_${guide.memberFileOriginalName}"><div class="guide-file">${guide.memberFileOriginalName}</div></a>
							</li>
						</ul>
						<div class="update-box">
							<button type="button" class="update-button deny">승인취소</button>
							<button type="button" class="update-button accept">승인하기</button>
							<input class="idForAccept" style="display: none" value="${guide.id}">
						</div>
					</main>
				</form>
			</div>
		</section>
            `
    });
    $contentWrap.append(content);
    $detailContentWrap.append(detailContent);

}

getAdminGuideList();

//------------------------------- 승인하기 ajax---------------------------------------

function updateStatus(){
    const dataForAccept = {
        memberId: null,
        confirm : null,
    };

    function setAdminGuideStatus() {
        $.ajax({
            url: `guide/change-status`,
            data: dataForAccept,
            success: function() {
                getAdminGuideList();
            }

        })
    }
    $('.update-button').on("click", function () {
        dataForAccept.memberId = $(this).parent().find('.idForAccept').val();
        dataForAccept.confirm = $(this).text();

        console.log("업데이트버튼눌림")
        setAdminGuideStatus();
        $('#guide-detail' + dataForAccept.memberId).hide();
    });

    $('.accept').on("click", function () {
        $('.accept').hide();
    });
}
