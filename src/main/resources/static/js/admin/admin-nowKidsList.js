/* 상세보기 모달 ===================== */

function openModal(num, e) {
    var selectedModal = '#nowKids-detail' + num;
    if (!$(e.target).is('input[type="checkbox"]') && !$(e.target).hasClass('no-modal')) {
        $(selectedModal).show();
    }
    console.log(num+"번 모달 클릭")
}


/* 상세 모달 닫기 */
function closeModal(num) {
    var selectedModal = '#nowKids-detail' + num;
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


// 수정하기 썸네일
$('.update-profile').change(function(event) {
    var input = event.target;
    var reader = new FileReader();
    reader.onload = function() {
        var dataURL = reader.result;
        var image = $('.img-updated');
        image.attr('src', dataURL);
    };
    reader.readAsDataURL(input.files[0]);
});

function kidList() {
    $('.maker_up_button').click(function () {
        var table = $(this).parents('tr').next('.crew-table');
        if (table.is(':visible')) {
            table.hide();
            $(this).removeClass('rotate');
        } else {
            table.show();
            $(this).addClass('rotate');
        }
    });
}

//-----------------------------------------ajax--------------------------------------------------

const PAGE_AMOUNT = 10;
const $itemWrap = $(".show-item-wrap");
const SEARCH_URL = "/parentsYard/list/show";
const $pageWrap = $(".page-button-box");
const $contentWrap = $(".nowKids-list");
const $detailContentWrap = $(".nowKids-modal-list");
const adminEventSearch = {
    eventTitle: null,
    categoryType: null,
    eventStatus: "전체"
};

// 이벤트 카테고리
$('.category-entire').click(function() {
    adminEventSearch.categoryType = null;
    adminEventSearch.eventStatus = '전체';
    adminEventSearch.eventTitle = null;

    getAdminNowKidsList();

    $('.category-entire').css('color', '#c97793')
    $('.category-country-side').css('color', 'black');
    $('.category-gallery-art').css('color', 'black');
    $('.category-tradition').css('color', '#black');
    $('.category-craft').css('color', 'black');
    $('.category-science').css('color', 'black');
    $('.category-museum').css('color', 'black');
    $('.category-sports').css('color', 'black');
    $('.category-etc').css('color', 'black');

    $('.hold').css('color', 'gray');
    $('.process').css('color', 'gray');
    $('.end').css('color', 'gray');
});

$('.category-country-side').click(function() {
    adminEventSearch.categoryType = 'AGRICULTURE';

    getAdminNowKidsList();

    $('.category-entire').css('color', 'black')
    $('.category-country-side').css('color', '#c97793');
    $('.category-gallery-art').css('color', 'black');
    $('.category-tradition').css('color', 'black');
    $('.category-craft').css('color', 'black');
    $('.category-science').css('color', 'black');
    $('.category-museum').css('color', 'black');
    $('.category-sports').css('color', 'black');
    $('.category-etc').css('color', 'black');
});

$('.category-gallery-art').click(function() {
    adminEventSearch.categoryType = 'ART';

    getAdminNowKidsList();

    $('.category-entire').css('color', 'black')
    $('.category-country-side').css('color', 'black');
    $('.category-gallery-art').css('color', '#c97793');
    $('.category-tradition').css('color', 'black');
    $('.category-craft').css('color', 'black');
    $('.category-science').css('color', 'black');
    $('.category-museum').css('color', 'black');
    $('.category-sports').css('color', 'black');
    $('.category-etc').css('color', 'black');
});

$('.category-tradition').click(function() {
    adminEventSearch.categoryType = 'TRADITION';

    getAdminNowKidsList();

    $('.category-entire').css('color', 'black')
    $('.category-country-side').css('color', 'black');
    $('.category-gallery-art').css('color', 'black');
    $('.category-tradition').css('color', '#c97793');
    $('.category-craft').css('color', 'black');
    $('.category-science').css('color', 'black');
    $('.category-museum').css('color', 'black');
    $('.category-sports').css('color', 'black');
    $('.category-etc').css('color', 'black');
});

$('.category-craft').click(function() {
    adminEventSearch.categoryType = 'CRAFT';
    getAdminNowKidsList();

    $('.category-entire').css('color', 'black')
    $('.category-country-side').css('color', 'black');
    $('.category-gallery-art').css('color', 'black');
    $('.category-tradition').css('color', 'black');
    $('.category-craft').css('color', '#c97793');
    $('.category-science').css('color', 'black');
    $('.category-museum').css('color', 'black');
    $('.category-sports').css('color', 'black');
    $('.category-etc').css('color', 'black');
});

$('.category-science').click(function() {
    adminEventSearch.categoryType = 'SCIENCE';
    getAdminNowKidsList();

    $('.category-entire').css('color', 'black')
    $('.category-country-side').css('color', 'black');
    $('.category-gallery-art').css('color', 'black');
    $('.category-tradition').css('color', 'black');
    $('.category-craft').css('color', 'black');
    $('.category-science').css('color', '#c97793');
    $('.category-museum').css('color', 'black');
    $('.category-sports').css('color', 'black');
    $('.category-etc').css('color', 'black');
});

$('.category-museum').click(function() {
    adminEventSearch.categoryType = 'MUSEUM';
    getAdminNowKidsList();

    $('.category-entire').css('color', 'black')
    $('.category-country-side').css('color', 'black');
    $('.category-gallery-art').css('color', 'black');
    $('.category-tradition').css('color', 'black');
    $('.category-craft').css('color', 'black');
    $('.category-science').css('color', 'black');
    $('.category-museum').css('color', '#c97793');
    $('.category-sports').css('color', 'black');
    $('.category-etc').css('color', 'black');
});

$('.category-sports').click(function() {
    adminEventSearch.categoryType = 'SPORTS';
    getAdminNowKidsList();

    $('.category-entire').css('color', 'black')
    $('.category-country-side').css('color', 'black');
    $('.category-gallery-art').css('color', 'black');
    $('.category-tradition').css('color', 'black');
    $('.category-craft').css('color', 'black');
    $('.category-science').css('color', 'black');
    $('.category-museum').css('color', 'black');
    $('.category-sports').css('color', '#c97793');
    $('.category-etc').css('color', 'black');
});

$('.category-etc').click(function() {
    adminEventSearch.categoryType = 'ETC';
    getAdminNowKidsList();

    $('.category-entire').css('color', 'black')
    $('.category-country-side').css('color', 'black');
    $('.category-gallery-art').css('color', 'black');
    $('.category-tradition').css('color', 'black');
    $('.category-craft').css('color', 'black');
    $('.category-science').css('color', 'black');
    $('.category-museum').css('color', 'black');
    $('.category-sports').css('color', 'black');
    $('.category-etc').css('color', '#c97793');
});

// 이벤트 진행상황
// $('.all').click(function() {
//     adminEventSearch.eventStatus = '전체';
//     getAdminEventBoardList();
// });

$('.hold').click(function() {
    adminEventSearch.eventStatus = '대기';
    getAdminNowKidsList();

    $('.hold').css('color', 'red');
    $('.process').css('color', 'gray');
    $('.end').css('color', 'gray');
});

$('.process').click(function() {
    adminEventSearch.eventStatus = '진행중';
    getAdminNowKidsList();

    $('.hold').css('color', 'gray');
    $('.process').css('color', 'green');
    $('.end').css('color', 'gray');
});

$('.end').click(function() {
    adminEventSearch.eventStatus = '종료';
    getAdminNowKidsList();

    $('.hold').css('color', 'gray');
    $('.process').css('color', 'gray');
    $('.end').css('color', 'black');
});


function getAdminNowKidsList() {
    $.ajax({
        url: `nowKidsList/${globalThis.page}`,
        data: adminEventSearch,
        success: function(data) {
            $pageWrap.empty();
            showPage(data);
            $contentWrap.empty();
            $detailContentWrap.empty();
            showList(data.content);
            kidList();

        }

    })
}

globalThis.page = 1;



function findPage(page) {
    globalThis.page = page;
    getAdminNowKidsList();
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

    adminEventSearch.eventTitle = val;
    adminEventSearch.eventStatus = '전체';
    adminEventSearch.categoryType = null;

    console.log( adminEventSearch.eventTitle + "777");
    getAdminNowKidsList();
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


//    지금우리아이들은 목록
function showList(nowKidsDTOS) {
    var content = "";
    var detailContent = "";
    nowKidsDTOS.forEach(nowKids => {
        console.log(nowKids.adminKidDTOS)
        const formattedStartDate = formatDate(new Date(nowKids.startDate));
        const formattedEndDate = formatDate(new Date(nowKids.endDate));
        let changeCategoryName = null;
        let now = new Date();
        let changeEventStatus = null;
        let eventStatusColor = null;
        let  fullAddress = nowKids.eventAddress;

        if (nowKids.category === null) {
            changeCategoryName = '전체';
        } else if (nowKids.category === 'AGRICULTURE'){
            changeCategoryName = '농촌';
        } else if (nowKids.category === 'ART'){
            changeCategoryName = '예술';
        } else if (nowKids.category === 'TRADITION'){
            changeCategoryName = '전통';
        } else if (nowKids.category === 'CRAFT'){
            changeCategoryName = '공방';
        } else if (nowKids.category === 'SCIENCE'){
            changeCategoryName = '과학';
        } else if (nowKids.category === 'MUSEUM'){
            changeCategoryName = '박물관';
        } else if (nowKids.category === 'SPORTS') {
            changeCategoryName = '스포츠';
        } else{
            changeCategoryName = '기타';
        }

        const dateStartTimeString = nowKids.startDate.toString();
        const dateEndTimeString = nowKids.endDate.toString();

        const jsStartDate = new Date(dateStartTimeString);
        const jsEndDate = new Date(dateEndTimeString);

        if (jsStartDate > now) {
            changeEventStatus = '대기';
            eventStatusColor = 'red';
        } else if(jsStartDate <= now  && jsEndDate >= now ){
            changeEventStatus = '진행중';
            eventStatusColor = 'green';
        } else{
            changeEventStatus = '종료';
            eventStatusColor = 'black';
        }
        content +=
            `
               <tr class="row" onclick="openModal(${nowKids.id}, event)">
                <td class="no-modal">
                <input type="checkbox" name="check">
                </td>
                <td>${nowKids.id}</td>
                <td>${changeCategoryName}</td>
                <td>${nowKids.boardTitle}</td>
                <td>${nowKids.guideName}</td>
                <td>5</td>
                <td><span>${formattedStartDate}</span><span> ~ </span><span>${formattedEndDate}</span></td>
                <td class="event-status" style="color: ${eventStatusColor}">${changeEventStatus}</td>
               </tr>
            `
        detailContent +=
            `
               <section class="user-modal" id="nowKids-detail${nowKids.id}">
			<div class="user-modal-shape">
				<div class="modal-header">
					<h4>게시글 상세</h4>
					<a class="modal-close">
						<svg
							xmlns="http://www.w3.org/2000/svg"
							data-name="Capa 1"
							id="Capa_1"
							viewBox="0 0 20 19.84"
							onclick="closeModal(${nowKids.id})"
						>
							<path d="M10.17,10l3.89-3.89a.37.37,0,1,0-.53-.53L9.64,9.43,5.75,5.54a.37.37,0,1,0-.53.53L9.11,10,5.22,13.85a.37.37,0,0,0,0,.53.34.34,0,0,0,.26.11.36.36,0,0,0,.27-.11l3.89-3.89,3.89,3.89a.34.34,0,0,0,.26.11.35.35,0,0,0,.27-.11.37.37,0,0,0,0-.53Z"/>
						</svg>
					</a>
				</div>
				<form class="user-info" action>
					<main class="user-detail">
						<h5 class="detail-title company-name">통솔자 ${nowKids.guideName}</h5>
						<div class="profile-img-wrapper">
							<label>
								<div class="content-img">
								   ${
                                        nowKids.nowKidsFileDTOS && nowKids.nowKidsFileDTOS.length > 0
                                            ? nowKids.nowKidsFileDTOS.map(fileDTO => `<img src="/nowKidsFiles/display?fileName=NowKids/${fileDTO.filePath}/${fileDTO.fileUUID}_${fileDTO.fileOriginalName}" />`).join('')
                                            : `<img src="https://us.123rf.com/450wm/mathier/mathier1905/mathier190500002/134557216-%EC%8D%B8%EB%84%A4%EC%9D%BC-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%97%86%EC%9D%8C-%ED%8F%AC%EB%9F%BC-%EB%B8%94%EB%A1%9C%EA%B7%B8-%EB%B0%8F-%EC%9B%B9%EC%82%AC%EC%9D%B4%ED%8A%B8%EC%9A%A9-%EC%9E%90%EB%A6%AC-%ED%91%9C%EC%8B%9C%EC%9E%90.jpg?ver=6" />`
                                    }
								</div>
							</label>
						</div>
						<div class="info-table" style="width: 1200px;">
							<!-- <colgroup> 
								<col style="width: 5%;"/> 
								<col style="width: 30%;"/> 
								<col style="width: 30%;"/> 
								<col style="width: 15%;"/> 
								<col style="width: 10%;"/> 
								<col style="width: 10%;"/> 
							</colgroup>  -->
							<table class="table">
								<thead>
									<tr>
										<th style="width: 5%;">No</th>
										<th style="width: 10%;">행사 종류</th>
										<th style="width: 30%;">행사 명</th>
										<th style="width: 20%;">행사 위치</th>
										<th style="width: 15%;">행사 기간</th>
										<th style="width: 10%;">종료 여부</th>
<!--										<th style="width: 10%;">참여 명단</th>-->
									</tr>
								</thead>
								<!-- row 1개 -->
								<tr>
									<td class="event-num">${nowKids.id}</td>
									<td>${changeCategoryName}</td>
									<td class="event-title modal">${nowKids.boardTitle}</td>
									<td class="event-location modal">${fullAddress}</td>
									<td class="event-period modal"><span class="start-date">${formattedStartDate}</span><span> ~ </span><span class="send-date">${formattedEndDate}</span></td>
									<!-- <td class="event-status hold">대기</td>
									<td class="event-status end">종료</td>-->
									<td class="event-status" style="color: ${eventStatusColor}">${changeEventStatus}</td> 
									<td class="event-guide maker_up_button">
										<svg
										viewbox="0 0 32 32"
										focusable="false"
										role="presentation"
										class="maker_svg"
										aria-hidden="true"
										style="width: 12px;"
										>
										<path
											d="M16 22.4L5.6 12l1.12-1.12L16 20.16l9.28-9.28L26.4 12 16 22.4z"
										></path>
										</svg>
									</td>
								</tr>
								<tr class="crew-table" style="display: none;">
									<td colspan="7" style="padding: 15px;">
											<table class="rice-table">
												<thead class="rice-table-thead">
													<tr class="rice-table-tr">
														<th class="rice-table-th" style="width: 5%;">No</th>
														<th class="rice-table-th" style="width: 10%;">닉네임</th>
														<th class="rice-table-th" style="width: 10%;">이름</th>
														<th class="rice-table-th" style="width: 5%;">나이</th>
														<th class="rice-table-th" style="width: 45%;">참여행사</th>
														<th class="rice-table-th" style="width: 25%;">참여일자</th>
													</tr>
												</thead>
												<tbody class="kid-list">
												${nowKids.adminKidDTOS && nowKids.adminKidDTOS.length > 0
                                                    ? nowKids.adminKidDTOS.map(kidDTO => `
                                                    <tr class="rice-table-tr">
														<td class="rice-table-td">${kidDTO.id}</td>
														<td class="rice-table-td">${kidDTO.parentNickName}</td>
														<td class="rice-table-td">${kidDTO.kidName}</td>
														<td class="rice-table-td">${kidDTO.kidAge}</td>
														<td class="rice-table-td">${nowKids.boardTitle}</td>
														<td class="rice-table-td"></td>
													</tr>
                                                    `).join('') : `<tr><td>참가한 아이가 없습니다.</td></tr>`
                                                }
												</tbody>
											</table>
									</td>
								</tr>
								<!-- //row 1개 -->
							</table>
						</div>
					</main>
				</form>
			</div>
			<input class="guideIdForKidList" style="display: none" value="${nowKids.guideId}">
			<input class="eventIdForKidList" style="display: none" value="${nowKids.eventId}">
		</section>
            `
    });
    $contentWrap.append(content);
    $detailContentWrap.append(detailContent);

}

getAdminNowKidsList();


//-----------------------------------------아이들 명단 ajax--------------------------------------------------

const $contentKidsWrap = $(".kid-list");
const findKidData = {
    guideId: null,
    eventId: null
};

findKidData.guideId = $('.guideIdForKidList').val();
findKidData.eventId = $('.eventIdForKidList').val();

function getKidList() {
    $.ajax({
        url: `kid-list`,
        data: findKidData,
        success: function(data) {
            $contentKidsWrap.empty();
            showKidList(data.content);
        }

    })
}



//    아이목록 목록
function showKidList(kidDTOS) {
    var content = "";
    console.log(kidDTOS)
    kidDTOS.forEach(kid => {
        const formattedStartDate = formatDate(new Date(kid.eventStartDate));
        content +=
            `eventStartDate
               <tr class="rice-table-tr">
                    <td class="rice-table-td">${kid.id}</td>
                    <td class="rice-table-td">${kid.parentNickName}</td>
                    <td class="rice-table-td">${kid.kidName}</td>
                    <td class="rice-table-td">${kid.kidAge}</td>
                    <td class="rice-table-td">${kid.eventTitle}</td>
                    <td class="rice-table-td">${formattedStartDate}</td>
                </tr>
            `
    });
    $contentKidsWrap.append(content);

}

getKidList();