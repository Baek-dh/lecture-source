// 미리보기 관련 요소 모두 얻어오기

// img 5개
const previewList = document.getElementsByClassName("preview"); 

// file 5개
const inputImageList = document.getElementsByClassName("inputImage"); 

// x버튼 5개
const deleteImageList = document.getElementsByClassName("delete-image"); 

// -> 위에 얻어온 요소들의 개수가 같음 == 인덱스가 일치함

 // 이미지가 선택(변경) 되었을 때 상태를 기록할 변수(선택 -> 취소 시 사용)
const backupInputList = new Array(inputImageList.length);



/******* 이미지 선택 시 수행할 함수 *******/
const changeImageFn = (imageInput, order) => {
    // 10MB로 최대 크기 제한 
    const maxSize = 1 * 1024 * 1024 * 1; // 파일 최대 크기 지정(바이트 단위)


    const uploadFile = imageInput.files[0]; // 업로드한 파일의 정보가 담긴 객체


    // ------------------------ 파일을 한 번 선택한 후 취소했을 때 ------------------------
    if(uploadFile == undefined){ 
        console.log("파일 선택이 취소됨");

        // 요소/노드.cloneNode(true) : 요소/노드 복제 (true == 하위 요소도 모두 복제)
        const temp = backupInputList[order].cloneNode(true);

        imageInput.after(temp); // input 요소 다음에 백업 요소를 추가하고
        
        imageInput.remove(); // 화면에 있던 기존 요소는 제거

        imageInput = temp; // imageInput 변수가 참조하는 요소를 백업 본으로 변경

        // 화면에 추가된 백업본은 이벤트 리스너가 되지 않았으므로 이벤트 리스너 추가
        imageInput.addEventListener("change", ()=>{
            changeImageFn(imageInput , order);
        });

        return; 
    }


    // ------------------------ 선택된 파일의 크기가 최대 크기를 초과한 경우 ------------------------
    if( uploadFile.size > maxSize){ 
        alert("10MB 이하의 이미지를 선택해주세요.");


        if(backupInputList[order] == undefined || backupInputList[order].value == ''){ 
            
            imageInput.value = ''; // value 삭제
        } else{ // 기존 이미지가 있었을 때

            // 1) backup한 요소를 복제
            const temp = backupInputList[order].cloneNode(true);

            // 2) 화면에 원본 input을 temp로 바꾸기
            imageInput.after(temp); // 원본 다음에 temp 추가
            imageInput.remove(); // 원본을 화면에서 제거
            imageInput = temp; // temp를 imageInput 변수에 대입

            // 복제본은 이벤트가 복제 안되니까 다시 이벤트를 추가
            imageInput.addEventListener("change", e => {
                changeImageFn(e.target, order);
            });
        }

        return;
    }



    // ------------------------ 선택된 이미지 읽어와 미리보기 만들기 ------------------------

    // JS에서 파일을 읽는 객체
    // - 파일을 읽고 클라이언트 컴퓨터에 파일을 저장할 수 있음
    const reader = new FileReader();

    // 매개변수에 작성된 파일을 읽어서 저장 후
    // 파일을 나타내는 URL을 result 속성으로 얻어올 수 있게 함.
    reader.readAsDataURL(uploadFile);

    // 다 읽었을 때
    reader.onload = e => {

        const url = e.target.result;

        // 프로필이미지(img) 태그에 src 속성으로 추가
        previewList[order].setAttribute("src", url);


        //  문제 없이 이미지 파일이 선택된 경우  파일이 추가된 input을 backup 해두기
        backupInputList[order] = imageInput.cloneNode(true);
    }
}


for(let i=0 ; i< inputImageList.length ; i++){

    /******* 이미지 선택 시 *******/
    inputImageList[i].addEventListener("change", e => {
        changeImageFn(e.target, i);
    });

    /*******  x버튼 클릭 시 *******/
    deleteImageList[i].addEventListener('click', () => {

        /* input type="file" 의 value 속성은 기본적으로 읽기 전용, 단 빈칸은 대입 가능(데이터 삭제 의미)  */
        inputImageList[i].value = ""; // input type="file"의 value 삭제
        backupInputList[i] = undefined; // input type="file"의 value 삭제
        
        // 프로필 이미지를 기본 이미지로 변경
        previewList[i].removeAttribute("src");
    });
    
}
