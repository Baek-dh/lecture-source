// 좋아요 버튼이 클릭 되었을 때
const boardLike = document.getElementById("boardLike");

boardLike.addEventListener("click", e => {

    // 로그인 여부 검사
    if(!loginCheck){ // false인 경우
        alert("로그인 후 이용해주세요")
        return;
    }

    let check; // 기존에  좋아요 X(빈하트) : 0  
               //         좋아요 O(꽉찬하트) : 1

    // contains("클래스명") : 클래스가 있으면 true, 없으면 false
    if(e.target.classList.contains("fa-regular")){ // 좋아요 X(빈하트)
        check = 0;
    }else{ // 좋아요 O(꽉찬하트) 
        check = 1;
    }

    // ajax로 서버로 제출할 파라미터를 모아둔 JS 객체
    const data =   {"boardNo" : boardNo ,  "check" : check };

    // ajax 코드 작성
    fetch("/board/like", {
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(data)
    })
    .then(response => {
        
        if(response.ok) return response.text();
        return -1;
    }) // 응답 객체를 필요한 형태로 파싱하여 리턴

    .then(count => { 

        console.log("count : " + count);

        if(count == -1){ // INSERT, DELETE 실패 시
            console.log("좋아요 처리 실패");
            return;
        }

        // toggle() : 클래스가 있으면 없애고, 없으면 추가하고
        e.target.classList.toggle("fa-regular");
        e.target.classList.toggle("fa-solid");

        // 현재 게시글의 좋아요 수를 화면에 출력
        e.target.nextElementSibling.innerText = count;


    }) // 파싱된 데이터를 받아서 처리하는 코드 작성

    .catch(err => {
        console.log("예외 발생");
        console.log(err);
    }) // 예외 발생 시 처리하는 부분


});

// ----------------------------------------------------------------------
// 게시글 삭제 버튼이 클릭 되었을 때
const deleteBtn = document.getElementById("deleteBtn");
if(deleteBtn != null){
    deleteBtn.addEventListener("click", () => {
        if(confirm("정말 삭제 하시겠습니까?")){
            location.href 
            = location.pathname.replace("board","editBoard")
                + "/delete";
            //   /editBoard/1/2006/delete (GET)

            // 삭제 서비스 호출 성공 시 redirect:/board/{boardCode}
            // + RedirectAttributes 이용해서 "삭제 되었습니다" alert 출력

            // 삭제 서비스 호출 실패 시 redirect:/board/{boardCoed}/{boardNo}
            // + RedirectAttributes 이용해서 "삭제 실패" alert 출력
        }
    })
}


// ----------------------------------------------------------------------

// 게시글 수정 버튼 클릭 시
const updateBtn = document.getElementById("updateBtn");

if(updateBtn != null){
    updateBtn.addEventListener("click", ()=>{

        location.href 
            = location.pathname.replace("board","editBoard")
                + "/update"
                + location.search
        // /editBoard/1/2006/update?cp=1 (GET)
    });
}




// ----------------------------------------

// 목록으로 버튼
const goToListBtn = document.getElementById('goToListBtn');

if(goToListBtn != null){

    // 함수 선언
    const goToListFn = () => {
        const params = new URL(location.href).searchParams;

        const obj = {};

        // 현재 존재하는 쿼리 스트링을 모두 얻어와 객체로 변경
        obj.cp = params.get("cp");
        obj.key = params.get("key"); // t, c, tc, w 중 하나
        obj.query = params.get("query");  // 검색어

        // console.log(obj);
    
        // 상세 조회 페이지 주소
        // /board/{boardCode}/{boardNo}?cp=1&key=t&query=test

        // 돌아갈 목록 주소
        // -> /board/{boardCode}?cp=1&key=t&query=test

        // 쿼리스트링을 조합 하기 위한 객체 생성
        const tempParams = new URLSearchParams();

        for(let key in obj){
            if(obj[key] != null) tempParams.append(key, obj[key])
        }


        // console.log(`/board/${boardCode}?${tempParams.toString()}`);
    
        // /board/{전역변수 boardCode}?조합된 쿼리스트링
        location.href = `/board/${boardCode}?${tempParams.toString()}`;
    }


    // 이벤트 리스너 추가
    goToListBtn.addEventListener("click", goToListFn);
}