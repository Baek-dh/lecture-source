// 요소 얻어오기
const region = document.querySelector("#region");
const regionName = document.querySelector("#regionName");
const currentWeather = document.querySelector(".current-weather");
const containerBody = document.querySelector(".container-body");
const loading = document.querySelector("#loading");

// 서비스키 (발급 받은 서비스키)
// const serviceKey = "VAnmAF7hu0kgMMh9%2FXpGXQQiBohl1%2BEZzdMhrIi%2FYtM5qrXECcSUitkVSHTixeqN6yX%2FEOv9jVeojFVnEGC5Nw%3D%3D";
const serviceKey = "VAnmAF7hu0kgMMh9/XpGXQQiBohl1+EZzdMhrIi/YtM5qrXECcSUitkVSHTixeqN6yX/EOv9jVeojFVnEGC5Nw==";


const numOfRows = 900; // 한 페이지 결과 수
const pageNo = 1; // 페이지 번호(1 고정)
const dataType = "JSON"; // 결과 데이터 타입(XML/JSON)

// 선택한 지역
let selectRegion;

// coordinates : 좌표
// 지역별 좌표(기상청 api excel 파일 참고)
const coordinateList = {
    "서울" : {"nx":60, "ny":127},
    "경기" : {"nx":60, "ny":120},
    "인천" : {"nx":55, "ny":124},
    "제주" : {"nx":52, "ny":38},
    "세종" : {"nx":66, "ny":103},
    "광주" : {"nx":58, "ny":74},
    "대구" : {"nx":89, "ny":90},
    "대전" : {"nx":67, "ny":100},
    "부산" : {"nx":98, "ny":76},
    "울산" : {"nx":102, "ny":84},
    "강원" : {"nx":73, "ny":134},
    "경북" : {"nx":89, "ny":91},
    "경남" : {"nx":91, "ny":77},
    "전북" : {"nx":63, "ny":89},
    "전남" : {"nx":51, "ny":67},
    "충북" : {"nx":69, "ny":107},
    "충남" : {"nx":68, "ny":100}
}

// const codeList = {
//     "POP" : {name:"강수확률", unit:"%"},
//     "PTY" : {name:"강수형태", unit:""},
//     "PCP" : {name:"1시간 강수량", unit:"범주 (1 mm)"},
//     "REH" : {name:"습도", unit:"%"},
//     "SNO" : {name:"1시간 신적설", unit:"범주(1 cm)"},
//     "SKY" : {name:"하늘상태", unit:""},
//     "TMP" : {name:"1시간 기온", unit:"℃"},
//     "TMN" : {name:"일 최저기온", unit:"℃"},
//     "TMX" : {name:"일 최고기온", unit:"℃"},
//     "UUU" : {name:"풍속(동서성분)", unit:"m/s"},
//     "VVV" : {name:"풍속(남북성분)", unit:"m/s"},
//     "WAV" : {name:"파고", unit:"M"},
//     "VEC" : {name:"풍향", unit:"deg"},
//     "WSD" : {name:"풍속", unit:"m/s"}
// };


//----------------------------------------------------------------------------------------------------
const getBase = (type) => {
    
    const base = {};
    const now = new Date();

    // base_date(오늘 날짜 YYYYMMDD 형식) 계산
    const year = now.getFullYear();

    let month = now.getMonth() + 1;
    month = month < 10 ? "0" + month : month;

    let date = now.getDate();
    date = date < 10 ? "0" + date : date;

    base.baseDate = `${year}${month}${date}`;

    // --------------------------------------
    // 1. 초단기 예보
    if(type === 1){

        // 40분 이하인 경우 1시간 전을 baseTime으로 지정
        const hour = now.getMinutes() <= 45 ? now.getHours()-1 : now.getHours()

        if(hour < 10)    base.baseTime =  "0" + hour + "00";
        else             base.baseTime =  hour + "00";
        return base;
    }
    
    
    // --------------------------------------
    // 2. 단기 예보
    // base_time 계산 (2, 5, 8, 11, 14, 17, 20, 23 시 중 하나 선택하여 HH00 형식)
    // (개선 필요!)
    // - API 제공 시간(~이후) : 02:10, 05:10, 08:10, 11:10, 14:10, 17:10, 20:10, 23:10
    if(type === 2){
        const arr = [2, 5, 8, 11, 14, 17, 20, 23];
        const currentHour = now.getHours();

        base.baseTime = "23:00";
        for(let i=0 ; i<arr.length-1 ; i++){
            if(currentHour >= arr[i] && currentHour < arr[i+1]){
                if(currentHour < 10) base.baseTime =  "0" + arr[i] + "00";
                else         base.baseTime =  arr[i] + "00";
            }
        }

        return base;
    }
}
//----------------------------------------------------------------------------------------------------

// 하늘 상태(SKY) 코드 변환
const getSkyState = (code) => {
    code = Number(code);
    switch(code){
    case 1: return "맑음";
    case 3: return "구름많음";
    case 4: return "흐림";
    }
}

// 초단기 강수 형태(PTY) 코드 변환
const getUltraSrtPtyState = (code) => {
    code = Number(code);
    switch(code){
        case 0: return "없음";
        case 1: return "비";
        case 2: return "비/눈";
        case 3: return "눈";
        case 5: return "빗방울";
        case 6: return "빗방울눈날림";
        case 7: return "눈날림";
    }
}

// 단기 강수 형태(PTY) 코드 변환
const getSrtPtyState = (code) => {
    code = Number(code);
    switch(code){
    case 0: return "없음";
    case 1: return "비";
    case 2: return "비/눈";
    case 3: return "눈";
    case 4: return "소나기";
    }
}


//----------------------------------------------------------------------------------------------------
/* 초단기 예보 조회 API 요청 */
const getUltraSrtFcst = (regionValue) => {
    const requestUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";

    const base = getBase(1); // 단기 예보 base_date, base_time 계산

    const nx = coordinateList[regionValue].nx;
    const ny = coordinateList[regionValue].ny;

    // 쿼리 스트링 생성 (URLSearchParams.toString())
    const searchParams = new URLSearchParams();

    searchParams.append("serviceKey", serviceKey);
    searchParams.append("numOfRows", numOfRows);
    searchParams.append("pageNo", pageNo);
    searchParams.append("dataType", dataType);
    searchParams.append("base_date", base.baseDate);
    searchParams.append("base_time", base.baseTime);
    searchParams.append("nx", nx);
    searchParams.append("ny", ny);


    currentWeather.innerHTML = "";

     // ajax를 이용해 현재 지역의 최신 단기 예보 조회
     fetch(requestUrl + "?" + searchParams.toString())
     .then(resp => resp.json())
     .then(result => {
        console.log(result);

        const list = result.response.body.items.item;

        const weatherObj = {};

        weatherObj.fcstDate = list[0].fcstDate;
        weatherObj.fcstTime = list[0].fcstTime;
        
        for(let item of list){
            if(item.fcstDate == weatherObj.fcstDate && 
                item.fcstTime == weatherObj.fcstTime){

                weatherObj[item.category] = item.fcstValue;
            }
        }

        console.log(weatherObj);

        // -----

        const h1 = document.createElement("h1");
        h1.innerText = weatherObj["T1H"] + "℃";

        const p1 = document.createElement("p");
        p1.innerText = getSkyState(weatherObj["SKY"] );

        const p2 = document.createElement("p");
        p2.innerText = "습도(%) :" + weatherObj["REH"];

        const p3 = document.createElement("p");
        p3.innerText = getUltraSrtPtyState(weatherObj["PTY"]);

        const p4 = document.createElement("p");
        p4.innerText = "강수 확률(%) : " + weatherObj["RN1"];

        currentWeather.append(h1, p1, p2, p3, p4);


     })
     .catch(e => console.log(e))
}



//----------------------------------------------------------------------------------------------------
/* 단기 예보 API 요청 */
const getShortWeather = (regionValue) => {

    const requestUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    const base = getBase(2); // 단기 예보 base_date, base_time 계산

    const nx = coordinateList[regionValue].nx;
    const ny = coordinateList[regionValue].ny;

    // 쿼리 스트링 생성 (URLSearchParams.toString())
    const searchParams = new URLSearchParams();

    searchParams.append("serviceKey", serviceKey);
    searchParams.append("numOfRows", numOfRows);
    searchParams.append("pageNo", pageNo);
    searchParams.append("dataType", dataType);
    searchParams.append("base_date", base.baseDate);
    searchParams.append("base_time", base.baseTime);
    searchParams.append("nx", nx);
    searchParams.append("ny", ny);

    // console.log(requestUrl);
    // console.log(searchParams.toString());


    /* 로딩 이미지 보이기 */
    loading.classList.toggle("show");


       
     /* .container-body 만들기 */
     containerBody.innerHTML = "";


    // ajax를 이용해 현재 지역의 최신 단기 예보 조회
    fetch(requestUrl + "?" + searchParams.toString())
    .then(resp => resp.json())
    .then(result => {
        // console.log(result);

        /* 로딩 이미지 숨기기 */
        loading.classList.toggle("show");

        /* 예보 내용 부분만 뽑아내기 */
        const list = result.response.body.items.item;

        // console.log(list);

        /* 흩어져 있는 예보 내용을 합치기*/
        const weatherObj = {};
        for(let item of list){
            if(weatherObj[item.fcstDate] == undefined){
                weatherObj[item.fcstDate] = {};
            }

            if(weatherObj[item.fcstDate][item.fcstTime]  == undefined){
                weatherObj[item.fcstDate][item.fcstTime] = {};
            }
            weatherObj[item.fcstDate][item.fcstTime][item.category] = item.fcstValue;
        }
        console.log(weatherObj);


        /* 날짜가 변할 때 마크로 출력하는 로직에 사용하는 변수 */
        let dateMarkValue;

        for(let date in weatherObj){

            /* 시간을 오름차순으로 정렬 */
            const timeList = Object.keys(weatherObj[date]).sort();
            // console.log(timeList);

            for(let time of timeList){

                const col = document.createElement("div");
                col.classList.add("col");

                /* 처음으로 해당 날짜 출력시 date-mark 추가 */
                if(dateMarkValue != date){
                    dateMarkValue = date;
                    const dateMark = document.createElement("div");
                    dateMark.classList.add("date-mark");
                    dateMark.innerText = date;
                    col.append(dateMark);
                }


                /* 시간 */
                const t = document.createElement("p");
                t.innerText = (time+"").substring(0,2) + "시";
                col.append(t);

                /* 하늘 상태 */
                const sky = document.createElement("p");
                const skyValue = weatherObj[date][time]["SKY"];

                sky.innerText = getSkyState(skyValue); // 하늘 상태 변환
                col.append(sky);


                /* 기온 */
                const tmp = document.createElement("p");
                tmp.innerText = weatherObj[date][time]["TMP"];
                col.append(tmp);

                /* 강수확률 */
                const pop = document.createElement("p");
                pop.innerText = weatherObj[date][time]["POP"];
                col.append(pop);

                /* 강수확률 */
                const reh = document.createElement("p");
                reh.innerText = weatherObj[date][time]["REH"];
                col.append(reh);


                containerBody.append(col);
            }
        }

    })
    .catch(e => console.log(e))
}




region.addEventListener("change", (e) => {
    const val = e.target.value;

    /* 지역명 변경 */
    regionName.innerText = val;
    getUltraSrtFcst(val);
    getShortWeather(val);

});

getUltraSrtFcst("서울");
getShortWeather("서울");