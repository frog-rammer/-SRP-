const yearSelect = document.getElementById('yearSelect');
const monthSelect = document.getElementById('monthSelect');
const calendar = document.getElementById('calendar');
let highlightedCells = [];

// 연도와 월 초기화
for (let i = 2020; i <= 2030; i++) {
    let option = document.createElement('option');
    option.value = i;
    option.textContent = i;
    yearSelect.appendChild(option);
}
yearSelect.value = new Date().getFullYear();
monthSelect.value = new Date().getMonth();

// 비동기 달력 렌더링
async function renderCalendar(year, month) {
    const firstDay = new Date(year, month, 1).getDay();
    const lastDate = new Date(year, month + 1, 0).getDate();

    calendar.innerHTML = '<div>Sun</div><div>Mon</div><div>Tue</div><div>Wed</div><div>Thu</div><div>Fri</div><div>Sat</div>';

    // 빈 셀 추가
    for (let i = 0; i < firstDay; i++) {
        calendar.innerHTML += '<div></div>';
    }

    // 날짜 셀 추가
    for (let i = 1; i <= lastDate; i++) {
        const dateDiv = document.createElement('div');
        dateDiv.textContent = i;
        calendar.appendChild(dateDiv);
    }
}

// 비동기 날짜 하이라이트
async function highlightProductionPeriod(startDate, endDate) {
    clearHighlight();

    const start = new Date(startDate);
    const end = new Date(endDate);
    const year = start.getFullYear();
    const month = start.getMonth();

    // 달력 연도와 월이 다르면 변경 후 렌더링
    if (year != yearSelect.value || month != monthSelect.value) {
        yearSelect.value = year;
        monthSelect.value = month;
        await renderCalendar(year, month); // 달력을 새로 렌더링
    }

    // 날짜 셀 하이라이트
    const firstDay = new Date(year, month, 1).getDay();
    for (let i = start.getDate(); i <= end.getDate(); i++) {
        const index = i + firstDay - 1;
        const cell = calendar.children[index];
        cell.classList.add('highlight');
        highlightedCells.push(cell);
    }
}

// 비동기 하이라이트 초기화
async function clearHighlight() {
    highlightedCells.forEach(cell => cell.classList.remove('highlight'));
    highlightedCells = [];
}

// 비동기 생산 진행 상황 업데이트
async function updateProgress() {
    // 예시로 비동기 처리를 위해 Promise 사용
    await new Promise(resolve => setTimeout(resolve, 500)); // 0.5초 대기 (데이터를 불러오는 시뮬레이션)
    alert('생산 진행 상황이 업데이트되었습니다!');
}

// 초기 달력 렌더링 및 이벤트 설정
(async function initialize() {
    await renderCalendar(yearSelect.value, monthSelect.value);

    yearSelect.addEventListener('change', async () => {
        await renderCalendar(yearSelect.value, monthSelect.value);
    });

    monthSelect.addEventListener('change', async () => {
        await renderCalendar(yearSelect.value, monthSelect.value);
    });
})();
