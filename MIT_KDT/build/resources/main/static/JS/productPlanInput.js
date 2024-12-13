document.addEventListener('DOMContentLoaded', () => {
    const yearSelect = document.getElementById('yearSelect');
    const monthSelect = document.getElementById('monthSelect');
    const calendar = document.getElementById('calendar');
    const startDateInput = document.getElementById('planStartDate');
    const endDateInput = document.getElementById('planEndDate');
    let dateClicked = 0;
    let startDateCell = null;
    let endDateCell = null;
    let selectedStartDate = null; // 시작일 저장
    let selectedEndDate = null; // 종료일 저장

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
            calendar.appendChild(dateDiv);

            // 상태 복원
            if (selectedStartDate && selectedStartDate.year == year && selectedStartDate.month == month && selectedStartDate.day == i) {
                dateDiv.insertAdjacentHTML('beforeend', '<span class="label">시작일</span>');
                startDateCell = dateDiv;
            }

            if (selectedEndDate && selectedEndDate.year == year && selectedEndDate.month == month && selectedEndDate.day == i) {
                dateDiv.insertAdjacentHTML('beforeend', '<span class="label end">종료일</span>');
                endDateCell = dateDiv;
            }
        }
    }

    async function selectDate(year, month, day, cell) {
        const selectedDate = `${year}-${String((Number(month) + 1)).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

        if (dateClicked % 2 === 0) { // 시작일 선택
            if (startDateCell) startDateCell.querySelector('.label')?.remove();
            startDateInput.value = selectedDate;
            startDateCell = cell;
            selectedStartDate = { year, month, day }; // 상태 저장
            cell.insertAdjacentHTML('beforeend', '<span class="label">시작일</span>');
            disableDatesBefore(day);
        } else { // 종료일 선택
            if (endDateCell) endDateCell.querySelector('.label')?.remove();
            endDateInput.value = selectedDate;
            endDateCell = cell;
            selectedEndDate = { year, month, day }; // 상태 저장
            cell.insertAdjacentHTML('beforeend', '<span class="label end">종료일</span>');
            enableAllDates();
        }
        dateClicked++;
    }

    function disableDatesBefore(startDay) {
        const dateCells = calendar.querySelectorAll('div');
        dateCells.forEach((cell) => {
            const day = parseInt(cell.textContent);
            if (!isNaN(day) && day < startDay) {
                cell.classList.add('disabled');
                cell.style.pointerEvents = 'none';
            }
        });
    }

    function enableAllDates() {
        const dateCells = calendar.querySelectorAll('div');
        dateCells.forEach((cell) => {
            cell.classList.remove('disabled');
            cell.style.pointerEvents = 'auto';
        });
    }

    function resetDates() {
        if (startDateCell) startDateCell.querySelector('.label')?.remove();
        if (endDateCell) endDateCell.querySelector('.label')?.remove();
        startDateInput.value = '';
        endDateInput.value = '';
        selectedStartDate = null;
        selectedEndDate = null;
        dateClicked = 0;
        enableAllDates();
    }

    yearSelect.addEventListener('change', async () => await renderCalendar(yearSelect.value, monthSelect.value));
    monthSelect.addEventListener('change', async () => await renderCalendar(yearSelect.value, monthSelect.value));

    const resetButton = document.getElementById('resetDatesButton');
    if (resetButton) {
        resetButton.addEventListener('click', resetDates);
    }

    // 초기 달력 렌더링
    renderCalendar(yearSelect.value, monthSelect.value);
});

function uploadExcel() {
    const fileInput = document.getElementById('excelFile');
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    fetch("/productionPlan/uploadExcel", {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (response.ok) {
                alert('엑셀 파일이 성공적으로 업로드되었습니다.');
                location.reload();
            } else {
                alert('엑셀 업로드에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error uploading Excel file:', error);
            alert('업로드 중 오류가 발생했습니다.');
        });
}