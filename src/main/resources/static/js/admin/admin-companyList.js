
/* 상세보기 모달 ===================== */
function openModal(num, e) {
    var selectedModal = '#company-detail' + num;
    if (!$(e.target).is('input[type="checkbox"]') && !$(e.target).hasClass('no-modal')) {
        $(selectedModal).show();
    }
    console.log(num+"번 모달 클릭")
}


/* 상세 모달 닫기 */
function closeModal(num) {
    var selectedModal = '#company-detail' + num;
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
const $itemWrap = $(".show-item-wrap");
const SEARCH_URL = "/parentsYard/list/show";
const $pageWrap = $(".page-button-box");
const $contentWrap = $(".company-list");
const $detailContentWrap = $(".company-modal-list");
const adminMemberSearch = {
    memberName: null
};


function getAdminCompanyList() {
    $.ajax({
        url: `companyList/${globalThis.page}`,
        data: adminMemberSearch,
        success: function(data) {
            $pageWrap.empty();
            showPage(data);
            $contentWrap.empty();
            showList(data.content);
        }

    })
}

globalThis.page = 1;



function findPage(page) {
    globalThis.page = page;
    getAdminCompanyList();
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

    adminMemberSearch.memberName = val;

    console.log( adminMemberSearch.memberName + "777");
    getAdminCompanyList();
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


//    기업 목록
function showList(companyDTOS) {
    var content = "";
    var detailcontent = "";
    console.log(companyDTOS)
    companyDTOS.forEach(company => {
        content +=
            `
               <tr class="row" onclick="openModal(${company.id}, event)">
                    <td class="no-modal">
                    <input type="checkbox" name="check">
                    </td>
                    <td class="company-id">${company.id}</td>
                    <td>${company.memberName}</td>
                    <td>${company.memberPhone}</td>
                    <td>${company.memberEmail}</td>
                    <td>${company.eventCount}</td>
                </tr>
            `


        detailcontent +=
            `
               <section class="user-modal" id="company-detail${company.id}">
			<div class="user-modal-shape">
				<div class="modal-header">
					<h4>행사 목록</h4>
					<a class="modal-close">
						<svg
							xmlns="http://www.w3.org/2000/svg"
							data-name="Capa 1"
							id="Capa_1"
							viewBox="0 0 20 19.84"
							onclick="closeModal(${company.id})"
						>
							<path d="M10.17,10l3.89-3.89a.37.37,0,1,0-.53-.53L9.64,9.43,5.75,5.54a.37.37,0,1,0-.53.53L9.11,10,5.22,13.85a.37.37,0,0,0,0,.53.34.34,0,0,0,.26.11.36.36,0,0,0,.27-.11l3.89-3.89,3.89,3.89a.34.34,0,0,0,.26.11.35.35,0,0,0,.27-.11.37.37,0,0,0,0-.53Z"/>
						</svg>
					</a>
				</div>
				<form class="user-info" action>
					<main class="user-detail">
						<h5 class="detail-title company-name">기업이름</h5>
						<div class="profile-img-wrapper">
							<label>
								<div class="content-img">
									<img
										src="https://us.123rf.com/450wm/mathier/mathier1905/mathier190500002/134557216-%EC%8D%B8%EB%84%A4%EC%9D%BC-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%97%86%EC%9D%8C-%ED%8F%AC%EB%9F%BC-%EB%B8%94%EB%A1%9C%EA%B7%B8-%EB%B0%8F-%EC%9B%B9%EC%82%AC%EC%9D%B4%ED%8A%B8%EC%9A%A9-%EC%9E%90%EB%A6%AC-%ED%91%9C%EC%8B%9C%EC%9E%90.jpg?ver=6"
									/>
								</div>
							</label>
						</div>
						<div class="info-table">
							<table class="table">
								<thead>
									<tr>
										<th>No</th>
										<th>행사 명</th>
										<th>행사 종류</th>
										<th>행사 위치</th>
										<th>행사 기간</th>
										<th>모집 인원</th>
										<th>진행 여부</th>
									</tr>
								</thead>
								
								<tbody class="company-event-list">
								${company.companyEventList && company.companyEventList.length > 0
                                ? company.companyEventList.map(event => `
                                                    <tr>
                                                        <td class="event-num">${event.id}</td>
                                                        <td class="event-title">${event.boardTitle}</td>
                                                        <td class="event-category">${event.category}</td>
                                                        <td class="event-location">여의도</td>
                                                        <td class="event-period"><span class="start-date">2023.10.22</span><span> ~ </span><span class="send-date">2023.11.15</span></td>
                                                        <td class="event-amount">${event.eventRecruitCount}<span>명</span></td>
                                                        <td class="event-status process">진행중</td>
                                                    </tr>
                                                    `).join('') : `<tr><td>제공한 행사가 없습니다.</td></tr>`
                                }
								</tbody>
							</table>
							<!-- 페이지 버튼 -->
							<div class="page-button-box-layout">
								<div class="page-button-box event-page-button-box">
									<!-- 페이지 번호 -->
<!--									<div class="">-->
<!--										<div class="page-button-margin">-->
<!--											<div>-->
<!--												<img src="https://cdn3.iconfinder.com/data/icons/google-material-design-icons/48/ic_keyboard_arrow_left_48px-128.png" class="left-button">-->
<!--											</div>-->
<!--										</div>-->
<!--									</div>-->
<!--									<div class="page-button-active page-button">-->
<!--										<div class="page-button-margin">-->
<!--											<div>-->
<!--												<span>1</span>-->
<!--											</div>-->
<!--										</div>-->
<!--									</div>-->
<!--									<div class="page-button">-->
<!--										<div class="page-button-margin">-->
<!--											<div>-->
<!--												<span>2</span>-->
<!--											</div>-->
<!--										</div>-->
<!--									</div>-->
<!--									<div class="page-button">-->
<!--										<div class="page-button-margin">-->
<!--											<div>-->
<!--												<span>3</span>-->
<!--											</div>-->
<!--										</div>-->
<!--									</div>-->
<!--									<div class="">-->
<!--										<div class="page-button-margin">-->
<!--											<div>-->
<!--												<img src="https://cdn3.iconfinder.com/data/icons/google-material-design-icons/48/ic_keyboard_arrow_right_48px-128.png" class="right-button">-->
<!--											</div>-->
<!--										</div>-->
<!--									</div>-->
								</div>
							</div>
						</div>
						
					</main>
				</form>
			</div>
		</section>
            `
    });
    $contentWrap.append(content);
    $detailContentWrap.append(detailcontent)
}

getAdminCompanyList();


// ---------------------- 공지사항 상세 ------------------------
// const $contentDetailWrap = $(".company-modal-list");
//
//
//
// function getAdminComapnyDetailList() {
//
//
//     $.ajax({
//         url: `companyDetail`,
//         success: function(data) {
//             $contentDetailWrap.empty();
//             showDetailList(data);
//         }
//
//     })
// }
//
//
// //    공지사항 상세 목록
// function showDetailList(companyDetailDTOS) {
//     var detailContent = "";
//     console.log(companyDetailDTOS)
//     companyDetailDTOS.forEach(companyDetail => {
//         const formattedDate = formatDate(new Date(companyDetail.writeDate));
//         detailContent +=
//             `
//                 <section class="user-modal" id="company-detail1">
//                 <div class="user-modal-shape">
//                 <div class="modal-header">
//                 <h4>행사 목록</h4>
//                 <a class="modal-close">
//                 <svg
//                 xmlns="http://www.w3.org/2000/svg"
//                 data-name="Capa 1"
//                 id="Capa_1"
//                 viewBox="0 0 20 19.84"
//                 onclick="closeModal(1)"
//                 >
//                 <path d="M10.17,10l3.89-3.89a.37.37,0,1,0-.53-.53L9.64,9.43,5.75,5.54a.37.37,0,1,0-.53.53L9.11,10,5.22,13.85a.37.37,0,0,0,0,.53.34.34,0,0,0,.26.11.36.36,0,0,0,.27-.11l3.89-3.89,3.89,3.89a.34.34,0,0,0,.26.11.35.35,0,0,0,.27-.11.37.37,0,0,0,0-.53Z"/>
//                 </svg>
//                 </a>
//                 </div>
//                 <form class="user-info" action>
//                 <main class="user-detail">
//                 <h5 class="detail-title company-name">기업이름</h5>
//                 <div class="profile-img-wrapper">
//                 <label>
//                 <div class="content-img">
//                 <img
//                 src="https://us.123rf.com/450wm/mathier/mathier1905/mathier190500002/134557216-%EC%8D%B8%EB%84%A4%EC%9D%BC-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%97%86%EC%9D%8C-%ED%8F%AC%EB%9F%BC-%EB%B8%94%EB%A1%9C%EA%B7%B8-%EB%B0%8F-%EC%9B%B9%EC%82%AC%EC%9D%B4%ED%8A%B8%EC%9A%A9-%EC%9E%90%EB%A6%AC-%ED%91%9C%EC%8B%9C%EC%9E%90.jpg?ver=6"
//                 />
//                 </div>
//                 </label>
//                 </div>
//                 <div class="info-table">
//                 <table class="table">
//                 <thead>
//                 <tr>
//                 <th>No</th>
//                 <th>행사 명</th>
//                 <th>행사 종류</th>
//                 <th>행사 위치</th>
//                 <th>행사 기간</th>
//                 <th>모집 인원</th>
//                 <th>진행 여부</th>
//                 </tr>
//                 </thead>
//                 <tbody class="">
//                 <tr>
//                 <td class="event-num">1</td>
//                 <td class="event-title">[아이와 함께하는] 진흙놀이</td>
//                 <td class="event-category">과학</td>
//                 <td class="event-location">여의도</td>
//                 <td class="event-period"><span class="start-date">2023.10.22</span><span> ~ </span><span class="send-date">2023.11.15</span></td>
//                 <td class="event-amount">1200<span>명</span></td>
//                 <td class="event-status process">진행중</td>
//                 </tr>
//                 </tbody>
//                 </table>
//                 </div>
//                 </main>
//                 </form>
//                 </div>
//                 </section>
//             `
//     });
//     $contentDetailWrap.append(detailContent);
//
// }
//
// getAdminComapnyDetailList();


//-----------------------------------------ajax--------------------------------------------------

$(".no-hover-row").on('click', function (e) {



    const EVENT_PAGE_AMOUNT = 10;
    const $eventPageWrap = $(".event-page-button-box");
    const $eventContentWrap = $(".company-event-list");
    const companyIdData = {
        companyId: null
    };

    companyIdData.companyId =$(this).find('.company-id').text();


    function getAdminCompanyEventList() {
        $.ajax({
            url: `company-event-List/${globalThis.detailPage}`,
            data: companyIdData,
            success: function(data) {
                $eventPageWrap.empty();
                showEventPage(data);
                $eventContentWrap.empty();
                showEventList(data.content);
            }

        })
    }

    globalThis.detailPage = 1;



    function findEventPage(page) {
        globalThis.detailPage = page;
        getAdminCompanyEventList();
    }


    function showEventPage(data) {
        let eventPageable = data.pageable;
        let eventPageNumber = pageable.pageNumber;
        let eventTotalPages = data.totalPages;
        let eventCount = Math.floor(eventPageNumber/EVENT_PAGE_AMOUNT);

        let eventStartPage = eventCount * EVENT_PAGE_AMOUNT;
        let eventEndPage = eventStartPage + EVENT_PAGE_AMOUNT;

        eventEndPage = eventEndPage > data.totalPages ? data.totalPages : eventEndPage;

        let hasEventPrev = eventStartPage > 1;
        // 170 page / 5 = 24 -> 171 /
        let hasEventNext = eventEndPage < data.totalPages;

        let text = "";


        // Previous button
        if (hasEventPrev) {
            text += `<div class="">`;
            text += `<div class="page-button-margin">`;
            text += `<div>`;
            text += `<img src="https://cdn3.iconfinder.com/data/icons/google-material-design-icons/48/ic_keyboard_arrow_left_48px-128.png" class="left-button" onclick="findPage(${startPage})" data-page = "${pageNumber}" aria-label="이전 목록">`;
            text += `</div>`;
            text += `</div>`;
            text += `</div>`;
        }

        // Page buttons
        for (let i = eventStartPage + 1; i < eventEndPage + 1; i++) {
            let eventPage = i;
            if (eventPageNumber  + 1 == eventPage) {
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
        if (hasEventNext) {
            text += `<div class="">`;
            text += `<div class="page-button-margin">`;
            text += `<div>`;
            text += `<img src="https://cdn3.iconfinder.com/data/icons/google-material-design-icons/48/ic_keyboard_arrow_right_48px-128.png" class="right-button" onclick="findPage(${endPage+1})" data-page="' + (pageNumber + 1) + '" aria-label="다음 목록">`;
            text += `</div>`;
            text += `</div>`;
            text += `</div>`;
        }

        $eventPageWrap.html(text);
    }


    //    기업 목록
    function showEventList(companyEventDTOS) {
        var content = "";
        console.log(companyEventDTOS)
        companyEventDTOS.forEach(companyEvent => {
            content +=
                `
                   <tr>
                        <td class="event-num">${companyEvent.id}</td>
                        <td class="event-title">${companyEvent.boardTitle}</td>
                        <td class="event-category">${companyEvent.category}</td>
                        <td class="event-location">여의도</td>
                        <td class="event-period"><span class="start-date">2023.10.22</span><span> ~ </span><span class="send-date">2023.11.15</span></td>
                        <td class="event-amount">1200<span>명</span></td>
                        <!-- <td class="event-status hold">대기</td>
                        <td class="event-status end">종료</td>-->
                        <td class="event-status process">진행중</td>
                    </tr>
                `
        });
        $eventPageWrap.append(content);

    }

    getAdminCompanyEventList();

    // 선택된 항목 삭제하기
    $('.confirm-delete').on('click', function () {
        var checkedIds = new Array();
        // 체크 박스 체크된 값
        $('input:checkbox[name=check]:checked').closest('tr').find('.company-id').each(function () {
            checkedIds.push(this.innerText);
            console.log(this.innerText);
        });

        // $('input:checkbox[name=check]:checked').closest('tr').find('.reply-id').each(function () {
        //     var id = $(this).text();
        //     checkedIds.push(parseInt(id, 10));
        // });
        console.log(checkedIds);
        console.log(typeof checkedIds[0]);
        $.ajax({
            url: "member/delete",
            type: "patch",
            data: {
                "checkedIds": checkedIds,
            },
            success: function () {
                findPage(page);
            }
        });

        $('input:checkbox[id=allSelect]:checked').prop('checked', false);
    });

});
