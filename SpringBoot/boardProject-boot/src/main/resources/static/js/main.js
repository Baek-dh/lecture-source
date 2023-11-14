
// 로그인 관련 요소
const loginFrm = document.getElementById("loginFrm");
const memberEmail = document.querySelector("#loginFrm input[name='memberEmail']");
const memberPw = document.querySelector("#loginFrm input[name='memberPw']");

// 로그인 form 태그가 화면에 존재하는 경우
if(loginFrm != null){
    // 로그인 시도를 할 때
    loginFrm.addEventListener("submit", e => {
        
        // form태그 기본 이벤트 제거
        // e.preventDefault();

        // 이메일이 입력되지 않은 경우
        // 문자열.trim() : 문자열 좌우 공백 제거
        if(memberEmail.value.trim().length == 0){
            alert("이메일을 입력해주세요.");

            memberEmail.value = ""; // 잘못 입력된 값(공백) 제거
            memberEmail.focus(); // 이메일 input태그에 초점을 맞춤

            e.preventDefault(); // 제출 못하게하기
            return; 
        }


        // 비밀번호가 입력되지 않은 경우
        if(memberPw.value.trim().length == 0){
            alert("비밀번호를 입력해주세요.");

            memberPw.value = ""; // 잘못 입력된 값(공백) 제거
            memberPw.focus(); // 이메일 input태그에 초점을 맞춤

            e.preventDefault(); // 제출 못하게하기
            return; 
        }


    });
}

// ----------------------------------------------

// 쿠키 얻어오기 함수 정의
const getCookie = key =>{
    const cookies = document.cookie;
    const cookieList = cookies.split(`; `).map((el) => el.split('='));
    
    const obj = {};
    for(let i=0 ; i<cookieList.length ; i+=2){
        obj[cookieList[i][0]] = cookieList[i][1];
    }

    return obj[key];
}

// 쿠키에 saveId가 있을 경우
if(document.querySelector('input[name="memberEmail"]') != null){
    const saveId = getCookie("saveId");

    if(saveId != undefined){
        document.querySelector('input[name="memberEmail"]').value = getCookie("saveId");
        document.querySelector('input[name="saveId"]').checked = true;

    }
} 


// -------------------------------------------------------------------

// 웹소켓 테스트
// 1. SockJS 라이브러리 추가

// 2. SockJS를 이용해서 클라이언트용 웹소켓 객체 생성
let testSock = new SockJS("/testSock");

function sendMessage(name, str){
    // 매개변수를 JS 객체에 저장
    let obj = {}; // 비어있는 객체

    obj.name = name; // 객체에 일치하는 key가 없다면 자동으로 추가
    obj.str = str;

    // console.log(obj);

    // 웹소켓 연결된 곳으로 메시지를 보냄
    testSock.send(JSON.stringify(obj));         
                 // JS객체 -> JSON
}


// 웹소켓 객체(testSock)가 서버로 부터 전달 받은 메시지가 있을 경우
testSock.onmessage = e => {
    // e : 이벤트 객체
    // e.data : 전달 받은 메세지 (JSON)

    let obj = JSON.parse(e.data); // JSON -> JS 객체

    console.log(`보낸사람 : ${obj.name} / ${obj.str}`);
}
