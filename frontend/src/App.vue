<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'

const seats = ref([])
const employees = ref([])
const selectedSeat = ref(null)
const selectedEmpId = ref('')

const E_SUN_GREEN = '#00948b';
const E_SUN_GRAY = '#7f8c8d';
const E_SUN_DANGER = '#ff4757';

const fetchData = async () => {
  try {
    const seatRes = await axios.get('http://localhost:8080/api/seating-chart')
    const empRes = await axios.get('http://localhost:8080/api/employees')
    seats.value = seatRes.data
    employees.value = empRes.data
  } catch (error) {
    Swal.fire({
      title: '錯誤',
      text: '無法連接後端伺服器',
      icon: 'error',
      confirmButtonColor: E_SUN_GREEN
    })
  }
}

watch(selectedEmpId, () => {
  selectedSeat.value = null
})

const getOccupant = (floorSeatSeq) => {
  return employees.value.find(emp => emp.seatingChart?.floorSeatSeq === floorSeatSeq)
}

const getSelectedEmployee = () => {
  return employees.value.find(emp => emp.empId === selectedEmpId.value)
}

const handleSeatClick = (seat) => {
  const occupant = getOccupant(seat.floorSeatSeq)
  
  if (occupant) {
    Swal.fire({
      title: '釋放座位？',
      text: `確定要移除 「${occupant.name}（${occupant.empId}）」的位置嗎？`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: E_SUN_DANGER,
      cancelButtonColor: E_SUN_GRAY,
      confirmButtonText: '移除',
      cancelButtonText: '取消'
    }).then((result) => {
      if (result.isConfirmed) {
        executeApiUpdate(occupant.empId, null)
      }
    })
    return
  }

  if (!selectedEmpId.value) {
    Swal.fire({
      title: '請先選擇員工',
      text: '您必須先從左側下拉選單選擇一位員工，才能安排座位！',
      icon: 'info',
      confirmButtonColor: E_SUN_GREEN,
      confirmButtonText: '返回'
    })
    return
  }

  selectedSeat.value = seat
}

const submitUpdate = () => {
  const targetEmp = getSelectedEmployee();
  
  Swal.fire({
    title: '確認安排座位？',
    html: `確定要把 <b>${selectedSeat.value.floorNo}F - ${selectedSeat.value.seatNo} 號位</b><br>安排給 <b>${targetEmp.name}（ID：${targetEmp.empId}）</b> 嗎？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: E_SUN_GREEN,
    cancelButtonColor: E_SUN_GRAY,
    confirmButtonText: '確定',
    cancelButtonText: '取消'
  }).then((result) => {
    if (result.isConfirmed) {
      executeApiUpdate(selectedEmpId.value, selectedSeat.value.floorSeatSeq)
    }
  })
}

const executeApiUpdate = async (empId, seatSeq) => {
  try {
    await axios.post('http://localhost:8080/api/update-seat', {
      empId: empId,
      newSeatSeq: seatSeq
    })
    
    Swal.fire({
      title: seatSeq === null ? '已釋放' : '安排成功',
      icon: 'success',
      timer: 1500,
      showConfirmButton: false
    })

    selectedSeat.value = null
    selectedEmpId.value = ''
    fetchData()
  } catch (error) {
    Swal.fire({
      title: '失敗',
      text: '資料更新出現錯誤',
      icon: 'error',
      confirmButtonColor: E_SUN_GREEN
    })
  }
}

onMounted(() => {
  fetchData();
  document.title = "SeatSystem 員工座位系統";
})
</script>

<template>
  <div class="app-wrapper">
    <aside class="sidebar">
      <div class="brand">
        <h2>SeatSystem</h2>
        <p>員工座位系統</p>
      </div>

      <div class="control-group">
        <label>1. 選擇員工</label>
        <select v-model="selectedEmpId" class="modern-select">
          <option value="">請選擇員工...</option>
          <option v-for="emp in employees" :key="emp.empId" :value="emp.empId">
            {{ emp.empId }} - {{ emp.name }}
          </option>
        </select>
      </div>

      <div class="selection-status" v-if="selectedSeat">
        <p>2. 預計安排位置</p>
        <div class="status-badge">
          {{ selectedSeat.floorNo }}F - 座位 {{ selectedSeat.seatNo }}
        </div>
      </div>

      <button class="primary-btn" 
              @click="submitUpdate" 
              :disabled="!selectedSeat || !selectedEmpId">
        確認送出
      </button>

      <div class="divider"></div>

      <div class="legend-panel">
        <div class="legend-item"><span class="dot empty"></span> 空位</div>
        <div class="legend-item"><span class="dot occupied"></span> 已佔用</div>
        <div class="legend-item"><span class="dot selected"></span> 已選擇</div>
      </div>
    </aside>

    <main class="main-content">
      <div v-for="floor in [1, 2, 3, 4]" :key="floor" class="floor-section">
        <div class="floor-header">
          {{ floor }}F 樓層座位
        </div>
        
        <div class="seat-grid">
          <div v-for="seat in seats.filter(s => s.floorNo === floor)" :key="seat.floorSeatSeq"
               class="seat-tile"
               :class="{ 
                 'is-occupied': getOccupant(seat.floorSeatSeq),
                 'is-selected': selectedSeat?.floorSeatSeq === seat.floorSeatSeq 
               }"
               @click="handleSeatClick(seat)">
            
            <div class="seat-info">
              <span class="label">NO.</span>
              <span class="number">{{ seat.seatNo }}</span>
            </div>
            
            <div v-if="getOccupant(seat.floorSeatSeq)" class="occupant-info">
              <div class="occ-id">ID: {{ getOccupant(seat.floorSeatSeq).empId }}</div>
              <div class="occ-name">{{ getOccupant(seat.floorSeatSeq).name }}</div>
            </div>

            <div v-else-if="selectedSeat?.floorSeatSeq === seat.floorSeatSeq && getSelectedEmployee()" class="occupant-info preview">
              <div class="occ-id">ID: {{ getSelectedEmployee().empId }}</div>
              <div class="occ-name">{{ getSelectedEmployee().name }}</div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.app-wrapper {
  display: flex;
  min-height: 100vh;
  background-color: #f4f7f6; 
  font-family: 'Segoe UI', 'Microsoft JhengHei', sans-serif;
}

.sidebar {
  width: 260px;
  background: #ffffff;
  padding: 25px;
  box-shadow: 2px 0 15px rgba(0,0,0,0.03);
  position: sticky;
  top: 0;
  height: 100vh;
  flex-shrink: 0;
  border-right: 1px solid #e0e6e5;
}

.brand h2 {
  color: #00948b;
  font-size: 1.6rem;
  margin-bottom: 0;
  font-weight: bold;
}

.brand p {
  color: #7f8c8d;
  font-size: 0.8rem;
  margin-bottom: 30px;
  letter-spacing: 1px;
}

.modern-select {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d8d7;
  border-radius: 6px;
  margin-bottom: 20px;
  outline-color: #00948b;
}

.selection-status {
  margin-bottom: 20px;
}

.status-badge {
  background: #e6f4f3;
  color: #00948b;
  padding: 10px;
  border-radius: 6px;
  font-weight: bold;
  text-align: center;
  border: 1px solid #b8dcd8;
}

.primary-btn {
  width: 100%;
  background: #00948b;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.primary-btn:hover:not(:disabled) {
  background: #007a72;
}

.primary-btn:disabled {
  background: #ced6d5;
  cursor: not-allowed;
}

.divider {
  height: 1px;
  background: #e0e6e5;
  margin: 25px 0;
}

.legend-panel {
  margin-top: 20px;
  font-size: 0.85rem;
  color: #57606f;
}

.legend-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  margin-right: 10px;
}

.dot.empty {
  background: #ffffff;
  border: 1px solid #d1d8d7;
}

.dot.occupied {
  background: #718093;
  border: 1px solid #718093;
}

.dot.selected {
  background: #00948b;
}

.main-content {
  flex: 1;
  padding: 40px;
}

.floor-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 35px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.04);
}

.floor-header {
  font-size: 1.2rem;
  font-weight: bold;
  color: #2f3542;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 5px solid #00948b;
}

.seat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.seat-tile {
  background: #ffffff;
  border-radius: 8px;
  padding: 18px 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid #e0e6e5;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}

.seat-tile:hover {
  border-color: #00948b;
  background-color: #f0f9f8;
}

.seat-info .label {
  font-size: 0.65rem;
  color: #95a5a6;
  display: block;
  text-align: center;
}

.seat-info .number {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
}

.occupant-info {
  margin-top: 10px;
  text-align: center;
  width: 100%;
}

.occ-id {
  font-size: 0.7rem;
  color: #7f8c8d;
}

.occ-name {
  font-size: 0.9rem;
  color: #2c3e50;
  font-weight: 600;
}

.seat-tile.is-occupied { 
  background: #718093; 
  border-color: #57606f; 
  cursor: pointer; 
}

.seat-tile.is-occupied .label, 
.seat-tile.is-occupied .number, 
.seat-tile.is-occupied .occ-id, 
.seat-tile.is-occupied .occ-name { 
  color: #ffffff !important; 
}

.seat-tile.is-selected { 
  background: #00948b; 
  border-color: #00948b; 
  box-shadow: 0 4px 12px rgba(0,148,139,0.3);
}

.seat-tile.is-selected .label, 
.seat-tile.is-selected .number, 
.seat-tile.is-selected .occ-id, 
.seat-tile.is-selected .occ-name { 
  color: #ffffff !important; 
}

.occupant-info.preview {
  color: #00948b;
  font-style: normal;
}
</style>