import { ref, reactive } from 'vue';

export const selectedLatestWeatherReport = reactive( { value: null });

export function updateSelectedLatestWeatherReport(newObject) {
  selectedLatestWeatherReport.value = newObject;
}

export const selectedWeatherReports = reactive( { value: null } );

export function updateSelectedWeatherReports(newObject) {
  selectedWeatherReports.value = newObject;
}

// import { selectedLatestWeatherReport, updateSelectedLatestWeatherReport, selectedWeatherReports, updateSelectedWeatherReports } from "@/SelectedWeatherReports.js";
