document.addEventListener('DOMContentLoaded', () => {
    const yearSelect = document.getElementById('yearSelect');
    const monthSelect = document.getElementById('monthSelect');
    const calendar = document.getElementById('calendar');
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    let dateClicked = 0;
    let startDateCell = null;
    let endDateCell = null;

    // 연도와 월 초기화
    for (let i = 2020; i <= 2030; i++) {
        let option = document.createElement('option');
        option.value = i;
        option.textContent = i;
        yearSelect.appendChild(option);
    }
    yearSelect.value = new Date().getFullYear();
    monthSelect.value = new Date().getMonth();

    async function renderCalendar(year, month) {
        const firstDay = new Date(year, month, 1).getDay();
        const lastDate = new Date(year, month + 1, 0).getDate();

        calendar.innerHTML = '<div>Sun</div><div>Mon</div><div>Tue</div><div>Wed</div><div>Thu</div><div>Fri</div><div>Sat</div>';

        for (let i = 0; i < firstDay; i++) {
            calendar.innerHTML += '<div></div>';
        }

        for (let i = 1; i <= lastDate; i++) {
            const dateDiv = document.createElement('div');
            dateDiv.textContent = i;
            dateDiv.addEventListener('click', async () => await selectDate(year, month, i, dateDiv));
            dateDiv.classList.remove('disabled'); // 비활성화 클래스 제거
            calendar.appendChild(dateDiv);
        }
    }

    async function selectDate(year, month, day, cell) {
        const selectedDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

        if (dateClicked % 2 === 0) { // 시작일 선택
            if (startDateCell) startDateCell.querySelector('.label')?.remove();
            startDateInput.value = selectedDate; // 시작일 입력란 업데이트

            startDateCell = cell;
            cell.insertAdjacentHTML('beforeend', '<span class="label">시작일</span>');

            // 시작일 선택 후 이전 날짜 비활성화
            disableDatesBefore(day);

        } else { // 종료일 선택
            if (endDateCell) endDateCell.querySelector('.label')?.remove();
            endDateInput.value = selectedDate; // 종료일 입력란 업데이트
            endDateCell = cell;
            cell.insertAdjacentHTML('beforeend', '<span class="label">종료일</span>');

            // 종료일 선택 후 모든 날짜 활성화
            enableAllDates();
        }
        dateClicked++;
    }

    // 시작일 선택 후 해당 날짜 이전의 날짜 비활성화
    function disableDatesBefore(startDay) {
        const dateCells = calendar.querySelectorAll('div');
        dateCells.forEach((cell, index) => {
            const day = parseInt(cell.textContent);
            if (!isNaN(day) && day < startDay) {
                cell.classList.add('disabled');
                cell.style.pointerEvents = 'none'; // 클릭 비활성화
            }
        });
    }

    // 모든 날짜 활성화
    function enableAllDates() {
        const dateCells = calendar.querySelectorAll('div');
        dateCells.forEach(cell => {
            cell.classList.remove('disabled');
            cell.style.pointerEvents = 'auto'; // 클릭 활성화
        });
    }

    function resetDates() {
        if (startDateCell) startDateCell.querySelector('.label')?.remove();
        if (endDateCell) endDateCell.querySelector('.label')?.remove();
        startDateInput.value = '';
        endDateInput.value = '';
        dateClicked = 0;
        enableAllDates(); // 모든 날짜 활성화
    }

    yearSelect.addEventListener('change', async () => await renderCalendar(yearSelect.value, monthSelect.value));
    monthSelect.addEventListener('change', async () => await renderCalendar(yearSelect.value, monthSelect.value));

    // 초기 달력 렌더링
    renderCalendar(yearSelect.value, monthSelect.value);
});
