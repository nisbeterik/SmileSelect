<template>
  <div class="form-group">
    <label for="dob_patient">Date of Birth:</label>
    <div class="dob-select horizontal-fields">
      <Multiselect
        :options="years"
        v-model="selectedYear"
        placeholder="Year"
        class="dob-field"
        searchable
      ></Multiselect>
      <Multiselect
        :options="months"
        v-model="selectedMonth"
        placeholder="Month"
        class="dob-field"
        searchable
      ></Multiselect>
      <Multiselect
        :options="daysInMonth"
        v-model="selectedDay"
        placeholder="Day"
        class="dob-field"
        searchable
      ></Multiselect>
    </div>
  </div>

</template>

<script>
import Multiselect from '@vueform/multiselect';
import '@vueform/multiselect/themes/default.css';

export default {
  components: {
    Multiselect,
  },
  data() {
    return {
      selectedYear: '',
      selectedMonth: '',
      selectedDay: '',
      years: Array.from({ length: 101 }, (_, i) => new Date().getFullYear() - i),
      months: [
        { value: 1, label: 'January' },
        { value: 2, label: 'February' },
        { value: 3, label: 'March' },
        { value: 4, label: 'April' },
        { value: 5, label: 'May' },
        { value: 6, label: 'June' },
        { value: 7, label: 'July' },
        { value: 8, label: 'August' },
        { value: 9, label: 'September' },
        { value: 10, label: 'October' },
        { value: 11, label: 'November' },
        { value: 12, label: 'December' },
      ],
    };
  },
  computed: {
    daysInMonth() {
      if (!this.selectedYear || !this.selectedMonth) return [];
      const days = new Date(this.selectedYear, this.selectedMonth, 0).getDate();
      return Array.from({ length: days }, (_, i) => i + 1);
    },
  },
  watch: {
    selectedYear() {
      this.emitDate();
    },
    selectedMonth() {
      this.emitDate();
    },
    selectedDay() {
      this.emitDate();
    },
  },
  methods: {
    emitDate() {
      if (this.selectedYear && this.selectedMonth && this.selectedDay) {
        const dob = `${this.selectedYear}-${String(this.selectedMonth).padStart(2, '0')}-${String(this.selectedDay).padStart(2, '0')}`;
        this.$emit('dateSelected', dob);
      }
    },
  },
};
</script>

<style scoped>
::v-deep(.multiselect-dropdown) {
  background-color: rgba(255, 255, 255, 0.9) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}
::v-deep(.multiselect-option) {
  color: #333;
  padding: 5px 10px;
  font-size: 14px;
  line-height: 1.4;
  cursor: pointer;
  margin: 3px;
}
::v-deep(.multiselect-option:hover) {
  background-color: rgba(255, 255, 255, 0.1);;
}
.horizontal-fields {
  display: flex;
  gap: 10px;
  align-items: center;
}
.dob-field {
  border: 0px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  width: 100%;
  box-sizing: border-box;
  flex: 1;
}
.dob-select{
  border: 0px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  padding: 8px;
  width: 100%;
  box-sizing: border-box;
  flex: 1; /* Makes all fields the same width */
}
</style>