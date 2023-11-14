function sample6_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById("address").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("detailAddress").focus();
        }
    }).open();
}




//----------------------------------------------------

// input type="file" 태그의 특징
// - 파일이 선택되면 change 이벤트 발생

// - 파일이 선택 화면에서 취소를 누르면 
//   기존에 세팅된 value/files 가 없어지게 된다!
//   --> 1차 이미지 변경 후   2차 변경 과정에서 취소를 누르면
//      1차 이미지 value/files가 없어지게 되어
//      제출 시 파일이 서버로 전달되지 않는다

// - value에 대입 가능한 값은 빈칸("") 뿐이다!
// - files는 동일한 자료형(FileList) 또는 undefined 대입 가능



// 프로필 이미지 추가/변경/삭제
const profileImg = document.getElementById("profileImg"); // img 태그
let imageInput = document.getElementById("imageInput"); // input 태그
const deleteImage = document.getElementById("deleteImage"); // x버튼


let statusCheck = -1; 
// 프로필 이미지가 새로 업로드 되거나 삭제 되었음을 나타내는 변수
// -1 == 초기값 ,  0 == 프로필 삭제(x버튼),  1 == 새 이미지 업로드

// input type="file" 태그의 값이 변경 되었을 때 변경된 상태를 백업해둘 변수
// 요소.cloneNode(true/false) : 요소 복제(true이면 하위 요소도 복제)
let backupInput; // 이미지가 선택(변경) 되었을 때 상태를 기록할 변수(선택 -> 취소 시 사용)


if(imageInput != null){ // 화면에 imageInput이 있을 경우



    /******* 프로필 이미지 선택 시 수행할 함수 *******/
    const changeImageFn = e => {
        // 1MB로 최대 크기 제한 
        const maxSize = 1 * 1024 * 1024 * 1; // 파일 최대 크기 지정(바이트 단위)

        console.log(e.target); // input
        console.log(e.target.value); // 업로드된 파일 경로
        console.log(e.target.files); // 업로드된 파일의 정보가 담긴 배열

        const file = e.target.files[0]; // 업로드한 파일의 정보가 담긴 객체


        // ------------------------ 파일을 한 번 선택한 후 취소했을 때 ------------------------
        if(file == undefined){ 
            console.log("파일 선택이 취소됨");
            //statusCheck = -1; // 취소 == 파일 없음 == 초기상태


            // 파일 선택 단계에서 취소를 누를 경우
            // input type="file"에 이전에 선택한 파일 데이터가 남지않고 지워지는 문제 발생!!
            // -> 이전 input요소의 파일 데이터를 복사(백업)해놓고
            //    취소, 잘못된 상황에 input 요소를 백업본으로 변경하자!!


            // 요소/노드.cloneNode(true) : 요소/노드 복제 (true == 하위 요소도 모두 복제)
            const temp = backupInput.cloneNode(true);

            imageInput.after(temp); // input 요소 다음에 백업 요소를 추가하고
            
            imageInput.remove(); // 화면에 있던 기존 요소는 제거

            imageInput = temp; // imageInput 변수가 참조하는 요소를 백업 본으로 변경

            // 화면에 추가된 백업본은 이벤트 리스너가 되지 않았으므로 이벤트 리스너 추가
            imageInput.addEventListener("change", changeImageFn);

            return; 
        }


        // ------------------------ 선택된 파일의 크기가 최대 크기를 초과한 경우 ------------------------
        if( file.size > maxSize){ 
            alert("1MB 이하의 이미지를 선택해주세요.");

            if(statusCheck == -1){ // 이미지 변경이 없었을 때
                
                // 최대 크기를 초과해도 input에 value가 남기 때문에
                // 이를 제거하는 코드가 필요하다!

                // input type="file" 태그에 대입할 수 있는 value는 "" (빈칸) 뿐이다!
                imageInput.value = ''; // value 삭제
                                    // 동시에 files도 삭제됨
                statusCheck = -1; // 선택 없음 상태


            } else{ // 기존 이미지가 있었을 때

                // 1) backup한 요소를 복제
                const temp = backupInput.cloneNode(true);

                // 2) 화면에 원본 input을 temp로 바꾸기
                imageInput.after(temp); // 원본 다음에 temp 추가
                imageInput.remove(); // 원본을 화면에서 제거
                imageInput = temp; // temp를 imageInput 변수에 대입

                // 복제본은 이벤트가 복제 안되니까 다시 이벤트를 추가
                imageInput.addEventListener("change", changeImageFn);

                statusCheck = 1;
            }


            return;
        }



        // ------------------------ 선택된 이미지 읽어와 미리보기 만들기 ------------------------

        // JS에서 파일을 읽는 객체
        // - 파일을 읽고 클라이언트 컴퓨터에 파일을 저장할 수 있음
        const reader = new FileReader();

        // 매개변수에 작성된 파일을 읽어서 저장 후
        // 파일을 나타내는 URL을 result 속성으로 얻어올 수 있게 함.
        reader.readAsDataURL(file);

        // 다 읽었을 때
        reader.onload = e => {
            //console.log(e.target);
            //console.log(e.target.result); // 읽은 파일의 URL

            const url = e.target.result;

            // 프로필이미지(img) 태그에 src 속성으로 추가
            profileImg.setAttribute("src", url);

            statusCheck = 1;

            //  문제 없이 이미지 파일이 선택된 경우  파일이 추가된 input을 backup 해두기
            backupInput = imageInput.cloneNode(true);
        }
    }


    // change 이벤트 : 값이 변했을 때
    // - input type="file", "checkbox", "radio" 에서 많이 사용
    // - text/number 형식 사용 가능
    //   -> 이 때 input값 입력 후 포커스를 잃었을 때 
    //      이전 값과 다르면 change 이벤트 발생
    imageInput.addEventListener("change", changeImageFn);


    /*******  x버튼 클릭 시 *******/
    deleteImage.addEventListener('click', () => {

        /* input type="file" 의 value 속성은 기본적으로 읽기 전용, 단 빈칸은 대입 가능(데이터 삭제 의미)  */
        imageInput.value = ""; // input type="file"의 value 삭제
        backupInput.value = ""; // input type="file"의 value 삭제
        
        // 프로필 이미지를 기본 이미지로 변경
        profileImg.setAttribute("src", "/images/user.png");

        statusCheck = 0; // x를 눌렀다를 기록
    });





    /******* #profileFrm이 제출 되었을 때 *******/
    document.getElementById("profileFrm").addEventListener("submit", e => {

        // statusCheck
        // 프로필 이미지가 새로 업로드 되거나 삭제 되었음을 나타내는 변수
        // -1 == 초기값 ,  0 == 프로필 삭제(x버튼),  1 == 새 이미지 업로드

        let flag = true;

        // 프로필 이미지가 없다 -> 있다
        if( loginMemberProfileImg == null  &&  statusCheck == 1 )   flag = false;

        // 이전 프로필 이미지가 있다 -> 삭제
        if( loginMemberProfileImg != null  &&  statusCheck == 0 )   flag = false;

        // 이전 프로필 이미지가 있다 -> 새 이미지
        if( loginMemberProfileImg != null  && statusCheck == 1 )    flag = false;
        

        if(flag){ // flag == true -> 제출하면 안되는 경우
            e.preventDefault(); // form 기본 이벤트 제거
            alert("이미지 변경 후 클릭하세요");
        }

    });

}