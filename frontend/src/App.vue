<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import Swal from 'sweetalert2'

const seats = ref([])        
const employees = ref([])    
const selectedSeat = ref(null) 
const selectedEmpId = ref('')  

const fetchData = async () => {
  try {
    const seatRes = await axios.get('http://localhost:8080/api/seating-chart')
    const empRes = await axios.get('http://localhost:8080/api/employees')
    seats.value = seatRes.data
    employees.value = empRes.data
  } catch (error) {
    Swal.fire('錯誤', '無法連接後端伺服器', 'error')
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
      confirmButtonColor: '#ff4757',
      cancelButtonColor: '#7f8c8d',
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
    confirmButtonColor: '#2ed573',
    cancelButtonColor: '#7f8c8d',
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
    Swal.fire('失敗', '資料更新出現錯誤', 'error')
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
  background-color: #f1f2f6;
  font-family: 'Segoe UI', 'Microsoft JhengHei', sans-serif;
}

.sidebar {
  width: 260px;
  background: white;
  padding: 25px;
  box-shadow: 2px 0 15px rgba(0,0,0,0.05);
  position: sticky;
  top: 0;
  height: 100vh;
  flex-shrink: 0;
}

.brand h2 { color: #2f3542; font-size: 1.6rem; margin-bottom: 0; }
.brand p { color: #a4b0be; font-size: 0.8rem; margin-bottom: 30px; }

.modern-select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  margin-bottom: 20px;
}

.selection-status { margin-bottom: 20px; }
.status-badge {
  background: #e8f0fe;
  color: #1a73e8;
  padding: 10px;
  border-radius: 6px;
  font-weight: bold;
  text-align: center;
}

.primary-btn {
  width: 100%;
  background: #2f3542;
  color: white;
  border: none;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.2s;
}
.primary-btn:hover:not(:disabled) { background: #57606f; }
.primary-btn:disabled { background: #dfe4ea; cursor: not-allowed; }

.divider { height: 1px; background: #eee; margin: 25px 0; }

.legend-panel { margin-top: 20px; font-size: 0.85rem; }
.legend-item { display: flex; align-items: center; margin-bottom: 8px; }
.dot { width: 10px; height: 10px; border-radius: 50%; margin-right: 10px; }
.dot.empty { background: #dcdde1; }
.dot.occupied { background: #ff4757; }
.dot.selected { background: #2ed573; }

.main-content {
  flex: 1;
  padding: 30px;
}

.floor-section {
  background: white;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.02);
}

.floor-header {
  font-size: 1.1rem;
  font-weight: bold;
  color: #57606f;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f1f2f6;
}

.seat-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

.seat-tile {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 15px 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s;
}

.seat-tile:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.seat-info .label { font-size: 0.6rem; color: #a4b0be; display: block; text-align: center; }
.seat-info .number { font-size: 1.4rem; font-weight: bold; color: #2f3542; }

.occupant-info {
  margin-top: 8px;
  text-align: center;
  width: 100%;
}
.occupant-info.preview {
  opacity: 0.9;
  font-style: italic;
}
.occ-id { font-size: 0.65rem; color: #747d8c; font-weight: bold; }
.occ-name { font-size: 0.85rem; color: #2f3542; font-weight: 500; }

.seat-tile.is-occupied { background: #ff4757; border-color: #ff4757; color: white; }
.seat-tile.is-occupied .label, .seat-tile.is-occupied .number, 
.seat-tile.is-occupied .occ-id, .seat-tile.is-occupied .occ-name { color: white; }

.seat-tile.is-selected { background: #2ed573; border-color: #2ed573; color: white; }
.seat-tile.is-selected .label, .seat-tile.is-selected .number,
.seat-tile.is-selected .occ-id, .seat-tile.is-selected .occ-name { color: white; }
</style>