<script setup>
import { selectedObjectType } from "@/SelectedObjectType.js";
import { selectedObject , updateSelectedObject, setSelectedObjectionSelectionValue } from "@/SelectedObject.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser, registerPageShow } from "@/LoginInformation.js";
import { Tabs, Tab } from 'super-vue3-tabs';
import { selectedLatestWeatherReport, updateSelectedLatestWeatherReport, selectedWeatherReports, updateSelectedWeatherReports } from "@/SelectedWeatherReports.js";
import { nudgeObject, nudge, nudgeUpdateObject, nudgeUpdate, nudgeRemoveObject, nudgeRemove  } from "@/nudgeObject.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const localSelectedObject = reactive({ncSelectedObject : { id : null }});

const dialogAceAdd = ref(null);
const dialogAceAddShow = reactive({value : false});
const dialogAceRemove = ref(null);
const dialogAceRemoveShow = reactive({value : false});
const dialogShelterUpdateOperationalMateriel = ref(null);
const dialogShelterUpdateOperationalMaterielShow = reactive({value : false});
const dialogShelterUpdateOperationalFood = ref(null);
const dialogShelterUpdateOperationalFoodShow = reactive({value : false});
const dialogShelterUpdateWorker = ref(null);
const dialogShelterUpdateWorkerShow = reactive({value : false});
const dialogShelterUpdateCensus = ref(null);
const dialogShelterUpdateCensusShow = reactive({value : false});
const dialogShelterUpdateStatus = ref(null);
const dialogShelterUpdateStatusShow = reactive({value : false});
const dialogEOCUpdateMobilization = ref(null);
const dialogEOCUpdateMobilizationShow = reactive({value : false});
const dialogEOCUpdateContacts = ref(null);
const dialogEOCUpdateContactsShow = reactive({value : false});
const dialogClearPriorityReports = ref(null);
const dialogClearPriorityReportsShow = reactive({value : false});
const dialogIdentify = ref(null);
const dialogIdentifyShow = reactive({value : false});
const dialogRemove = ref(null);
const dialogRemoveShow = reactive({value : false});
const dialogEditStation = ref(null);
const dialogEditStationShow = reactive({value : false});
const dialogEditCallsign = ref(null);
const dialogEditCallsignShow = reactive({value : false});
const dialogClearReports = ref(null);
const dialogClearReportsShow = reactive({value : false});
const dialogTrack = ref(null);
const dialogTrackShow = reactive({value : false});
const dialogUntrack = ref(null);
const dialogUntrackShow = reactive({value : false});




const accesstokenRef = reactive({value : ''});
const localLoggedInUserRef = reactive({value : ''});
var callsign = ref(null);
const callsign2Ref = reactive({value : ''});
const name2Ref = reactive({value : ''});
const description2Ref = reactive({value : ''});
const electricalPowerType2Ref = reactive({value : ''});
const backupElectricalPowerType2Ref = reactive({value : ''});
const radioStyle2Ref = reactive({value : ''});
const transmitPower2Ref = reactive({value : ''});
const frequencyTx2Ref = reactive({value : ''});
const frequencyRx2Ref = reactive({value : ''});
const tone2Ref = reactive({value : ''});
const state2Ref = reactive({value : ''});
const country2Ref = reactive({value : ''});
const license2Ref = reactive({value : ''});
const directorNameRef = reactive({value : ''});
const incidentCommanderNameRef = reactive({value : ''});
const eocNameRef = reactive({value : ''});
const messageRef = reactive({value : ''});
const statusRef = reactive({value : 1});
const levelRef = reactive({value : 1});
const stateRef = reactive({value : 1});
const population03Ref = reactive({value : 0});
const population47Ref = reactive({value : 0});
const population812Ref = reactive({value : 0});
const population1318Ref = reactive({value : 0});
const population1965Ref = reactive({value : 0});
const population66Ref = reactive({value : 0});
const shiftRef = reactive({value : 1});
const healthRef = reactive({value : 0});
const mentalRef = reactive({value : 0});
const spiritualRef = reactive({value : 0});
const caseworkerRef = reactive({value : 0});
const feedingRef = reactive({value : 0});
const otherRef = reactive({value : 0});
const timeframeRef = reactive({value : 1});
const breakfastRef = reactive({value : 0});
const lunchRef = reactive({value : 0});
const dinnerRef = reactive({value : 0});
const snackRef = reactive({value : 0});
const cotsRef = reactive({value : 0});
const blanketsRef = reactive({value : 0});
const comfortRef = reactive({value : 0});
const cleanupRef = reactive({value : 0});
const signageRef = reactive({value : 0});
const aceCallsignRef = reactive({value : ''});
const aceCallsignTypeRef = reactive({value : 1});
const aceProximityRef = reactive({value : 500});
const errorMessageRef = reactive({value : null});


const headers = [
        { text: "Time", value: "prettyLdtime", sortable: true },
        { text: "Temperature", value: "temperature", sortable: true},
        { text: "Pressure", value: "barometricPressure", sortable: true},
        { text: "Humidity", value: "humidity", sortable: true},
        { text: "Rain 1h", value: "rainfallLast1Hr", sortable: true},
        { text: "Rain 24h", value: "rainfallLast24Hr", sortable: true},
        { text: "Rain 00:00", value: "rainfallSinceMidnight", sortable: true},
        { text: "Wind MPH", value: "windSpeed", sortable: true},
        { text: "Wind Dir", value: "windDirection", sortable: true},
        { text: "Gust MPH", value: "gust", sortable: true},
        { text: "Snow 24h", value: "snowfallLast24Hr", sortable: true}
      ];

const headersEOCMobilizations = [
        { text: "Time", value: "prettyLastReportedTime", sortable: true },
        { text: "EOC Name", value: "eocName", sortable: true},
        { text: "Status", value: "status", sortable: true},
        { text: "Level", value: "level", sortable: true}
      ];

const headersEOCContacts = [
        { text: "Time", value: "prettyLastReportedTime", sortable: true },
        { text: "EOC Director", value: "directorName", sortable: true},
        { text: "Incident Commander", value: "incidentCommanderName", sortable: true}
      ];

const headersAccessControl = [
        { text: "Callsign", value: "callsignChecked", sortable: true },
        { text: "Type", value: "type", sortable: true},
        { text: "Allowed", value: "allowed", sortable: true},
        { text: "Proximity", value: "proximity", sortable: true}
      ];

const headersShelterCensus = [
        { text: "Time", value: "prettyLastReportedTime", sortable: true },
        { text: "Age 0-3", value: "population03", sortable: true},
        { text: "Age 4-7", value: "population47", sortable: true},
        { text: "Age 8-12", value: "population812", sortable: true},
        { text: "Age 13-18", value: "population1318", sortable: true},
        { text: "Age 19-65", value: "population1965", sortable: true},
        { text: "Age 66+", value: "population66", sortable: true}
      ];

const headersStatus = [
        { text: "Time", value: "prettyLdtime", sortable: true },
        { text: "Status", value: "status", sortable: true}
      ];

onMounted(() => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
})

function updateLocalSelectedObject(newObject) {
  localSelectedObject.ncSelectedObject = newObject.ncSelectedObject;
}

const localSelectedObjectType = reactive({value : null });
function updateLocalSelectedObjectType(newObjectType) {
  localSelectedObjectType.value = newObjectType.value;
}

// Watch for changes in the selected object ref
watch(selectedObject, (newSelectedObject, oldSelectedObject) => {
  updateLocalSelectedObject(newSelectedObject);
});
watch(selectedObjectType, (newSelectedObjectType, oldSelectedObjectType) => {
  updateLocalSelectedObjectType(newSelectedObjectType);
});


const selectedAce = reactive({value : null});
const accessControlList = reactive({value : null});
const weatherReports = reactive({value : null});
const statusReports = reactive({value : null});
const latestWeatherReport = reactive({value : null});
const shelterStatusReport = reactive({value : null});
const shelterCensusReport = reactive({value : null});
const shelterCensusReports = reactive({value : null});
const shelterOperationalFoodReport1 = reactive({value : null});
const shelterOperationalFoodReport2 = reactive({value : null});
const shelterOperationalFoodReport3 = reactive({value : null});
const shelterOperationalMaterielReport1 = reactive({value : null});
const shelterOperationalMaterielReport2 = reactive({value : null});
const shelterOperationalMaterielReport3 = reactive({value : null});
const shelterWorkersReport1 = reactive({value : null});
const shelterWorkersReport2 = reactive({value : null});
const shelterWorkersReport3 = reactive({value : null});
const eocMobilizationReport = reactive({value : null});
const eocMobilizationReports = reactive({value : null});
const eocContactReport = reactive({value : null});
const eocContactReports = reactive({value : null});

function getGetRequestOptions() {
  return {
    method: "GET",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: null
  };
}

function fetchACL() {
  fetch(buildNetCentralUrl('/accessControlLists/'+localSelectedObject.ncSelectedObject.callsign), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        accessControlList.value = data;
    })
    .catch(error => { console.error('Error getting access control list from server:', error); })
}
function fetchShelterReports() {
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/status'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterStatusReport.value = data;
    })
    .catch(error => { console.error('Error getting shelter status report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/workers?shift=1'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterWorkersReport1.value = data;
    })
    .catch(error => { console.error('Error getting shelter workers shift 1 report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/workers?shift=2'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterWorkersReport2.value = data;
    })
    .catch(error => { console.error('Error getting shelter workers shift 2 report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/workers?shift=3'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterWorkersReport3.value = data;
    })
    .catch(error => { console.error('Error getting shelter workers shift 3 report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/census'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterCensusReport.value = data;
    })
    .catch(error => { console.error('Error getting shelter census report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/census/all'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterCensusReports.value = data;
    })
    .catch(error => { console.error('Error getting shelter census report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/operationalFoods?timeframe=1'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterOperationalFoodReport1.value = data;
    })
    .catch(error => { console.error('Error getting shelter food today report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/operationalFoods?timeframe=2'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterOperationalFoodReport2.value = data;
    })
    .catch(error => { console.error('Error getting shelter food tomorrow report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/operationalFoods?timeframe=3'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterOperationalFoodReport3.value = data;
    })
    .catch(error => { console.error('Error getting shelter food needed report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/operationalMateriels?timeframe=1'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterOperationalMaterielReport1.value = data;
    })
    .catch(error => { console.error('Error getting shelter materiel today report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/operationalMateriels?timeframe=2'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterOperationalMaterielReport2.value = data;
    })
    .catch(error => { console.error('Error getting shelter materiel tomorrow report from server:', error); })
  fetch(buildNetCentralUrl('/shelterReports/'+localSelectedObject.ncSelectedObject.callsign+'/operationalMateriels?timeframe=3'), getGetRequestOptions())
    .then(response => {
        if (response.status == 200) {
          return response.json();
        } else {
          return null;
        }
      })
    .then(data => {
        shelterOperationalMaterielReport3.value = data;
    })
    .catch(error => { console.error('Error getting shelter materiel needed report from server:', error); })
}

function fetchEOCReports() {
    fetch(buildNetCentralUrl('/eocReports/'+localSelectedObject.ncSelectedObject.callsign+'/mobilizations/latest'), getGetRequestOptions())
      .then(response => {
          if (response.status == 200) {
            return response.json();
          } else {
            return null;
          }
        })
      .then(data => {
          eocMobilizationReport.value = data;
      })
      .catch(error => { console.error('Error getting EOC mobilization report from server:', error); })
    fetch(buildNetCentralUrl('/eocReports/'+localSelectedObject.ncSelectedObject.callsign+'/mobilizations'), getGetRequestOptions())
      .then(response => {
          if (response.status == 200) {
            return response.json();
          } else {
            return null;
          }
        })
      .then(data => {
          eocMobilizationReports.value = data;
      })
      .catch(error => { console.error('Error getting EOC mobilization reports from server:', error); })
    fetch(buildNetCentralUrl('/eocReports/'+localSelectedObject.ncSelectedObject.callsign+'/contacts/latest'), getGetRequestOptions())
      .then(response => {
          if (response.status == 200) {
            return response.json();
          } else {
            return null;
          }
        })
      .then(data => {
          eocContactReport.value = data;
      })
      .catch(error => { console.error('Error getting EOC contact report from server:', error); })
    fetch(buildNetCentralUrl('/eocReports/'+localSelectedObject.ncSelectedObject.callsign+'/contacts'), getGetRequestOptions())
      .then(response => {
          if (response.status == 200) {
            return response.json();
          } else {
            return null;
          }
        })
      .then(data => {
          eocContactReports.value = data;
      })
      .catch(error => { console.error('Error getting EOC contact reports from server:', error); })
}

watch(
  localSelectedObject,
  async () => {
    accessControlList.value = null;
    weatherReports.value = null;
    latestWeatherReport.value = null;
    shelterStatusReport.value = null;
    shelterOperationalFoodReport1.value = null;
    shelterOperationalFoodReport2.value = null;
    shelterOperationalFoodReport3.value = null;
    shelterOperationalMaterielReport1.value = null;
    shelterOperationalMaterielReport2.value = null;
    shelterOperationalMaterielReport3.value = null;
    shelterCensusReport.value = null;
    shelterCensusReports.value = null;
    shelterWorkersReport1.value = null;
    shelterWorkersReport2.value = null;
    shelterWorkersReport3.value = null;
    eocMobilizationReport.value = null;
    eocMobilizationReports.value = null;
    eocContactReport.value = null;
    eocContactReports.value = null;
    selectedAce.value = null;
    statusReports.value = null;

    if ((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObject != null) && (localSelectedObject.ncSelectedObject != null)) {
      fetch(buildNetCentralUrl('/APRSObjects/statusMessages/'+localSelectedObject.ncSelectedObject.callsign), getGetRequestOptions())
        .then(response => {
            if (response.status == 200) {
              return response.json();
            } else {
              return null;
            }
          })
        .then(data => {
          if (data.length > 0) {
            statusReports.value = data;
          } else {
            statusReports.value = null;
          }
        })
        .catch(error => { console.error('Error getting status reports from server:', error); })
      }
    if ((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'WEATHER') && (localSelectedObject != null) && (localSelectedObject.ncSelectedObject != null)) {
      fetch(buildNetCentralUrl('/weatherReports/callsigns/'+localSelectedObject.ncSelectedObject.callsign+'/all'), getGetRequestOptions())
        .then(response => {
            if (response.status == 200) {
              return response.json();
            } else {
              return null;
            }
          })
        .then(data => {
            weatherReports.value = data;
        })
        .catch(error => { console.error('Error getting weather reports from server:', error); })
      fetch(buildNetCentralUrl('/weatherReports/callsigns/'+localSelectedObject.ncSelectedObject.callsign+'/latest'), getGetRequestOptions())
        .then(response => {
            if (response.status == 200) {
              return response.json();
            } else {
              return null;
            }
          })
        .then(data => {
            latestWeatherReport.value = data;
        })
        .catch(error => { console.error('Error getting shelter report from server:', error); })
    } else if ((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'PRIORITYOBJECT') && (localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.type == 'SHELTER')) {
      fetchShelterReports();
      fetchACL();
    } else if ((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'PRIORITYOBJECT') && (localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.type == 'EOC')) {
      fetchEOCReports();
      fetchACL();
    } else if ((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'PRIORITYOBJECT') && (localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.type == 'MEDICAL')) {
      fetchACL();
    }
  },
  { immediate: true }
)


function clickAce(item) {
    selectedAce.value = item;
}

function getAceRowClass(item, rowNumber) {
    if ((selectedAce.value != null) && (selectedAce.value.callsignChecked == item.callsignChecked)) {
      return 'bold-row';
    }
    return '';
}

function aceAdd() {
    aceCallsignRef.value = '';
    aceCallsignTypeRef.value = 1;
    aceProximityRef.value = 500;
    dialogAceAddShow.value = true;
}

function aceAddYes() {
    dialogAceAddShow.value = false;
    // perform the update
    performAceAdd();
}

function aceAddNo() {
    dialogAceAddShow.value = false;
}

function performAceAdd() {
    var CStarget = localSelectedObject.ncSelectedObject.callsign;
    var CSchecked = aceCallsignRef.value;
    var type = 0;
    var prox = null;
    // UNKNOWN, STATION, NET, OBJECT, WITHIN_PROXIMITY
    if (aceCallsignTypeRef.value == '1') {
      type = 1;
    } else if (aceCallsignTypeRef.value == '2') {
      type = 2;
    } else if (aceCallsignTypeRef.value == '3') {
      type = 2;
      CSchecked = '*';
    } else if (aceCallsignTypeRef.value == '4') {
      prox = aceProximityRef.value;
      CSchecked = '*';
      type = 4;
    }

    var bodyObject = { 
                        callsignTarget : CStarget,
                        callsignChecked : CSchecked,
                        type : type,
                        allowed : true,
                        proximity : prox
                    };

    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/accessControlLists/"+CStarget), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
            dialogAceAddShow.value = false;
            fetchACL();
      })
      .catch(error => { console.error('Error adding ACE:', error); })
}

function aceRemove() {
    dialogAceRemoveShow.value = true;
}

function aceRemoveYes() {
    dialogAceRemoveShow.value = false;

    // perform the update
    performAceRemove();
}

function aceRemoveNo() {
    dialogAceRemoveShow.value = false;
}

function performAceRemove() {
    var CS = localSelectedObject.ncSelectedObject.callsign;
    var checkedCS = selectedAce.value.callsignChecked;

    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(buildNetCentralUrl("/accessControlLists/"+CS+"/"+checkedCS), requestOptions)
      .then(response => {
        return response;
      })
      .then(data => {
          dialogAceRemoveShow.value = false;
          fetchACL();
      })
      .catch(error => { console.error('Error deleting ACE', error); })
}

function shelterUpdateOperationalMateriel() {
    cotsRef.value = 0;
    blanketsRef.value = 0;
    comfortRef.value = 0;
    cleanupRef.value = 0;
    signageRef.value = 0;
    otherRef.value = 0;
    timeframeRef.value = 1;

    dialogShelterUpdateOperationalMaterielShow.value = true;
}

function shelterUpdateOperationalMaterielYes() {
    dialogShelterUpdateOperationalMaterielShow.value = false;
    // perform the update
    performShelterUpdateOperationalMateriel();
}

function shelterUpdateOperationalMaterielNo() {
    dialogShelterUpdateOperationalMaterielShow.value = false;
}

function performShelterUpdateOperationalMateriel() {
    var CS = localSelectedObject.ncSelectedObject.callsign;

    var bodyObject = { 
                        timeframe : timeframeRef.value,
                        cots : cotsRef.value,
                        blankets : blanketsRef.value,
                        comfort : comfortRef.value,
                        cleanup : cleanupRef.value,
                        signage : signageRef.value,
                        other : otherRef.value
                    };

    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/shelterReports/"+CS+"/operationalMateriels"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
            dialogShelterUpdateOperationalMaterielShow.value = false;
            fetchShelterReports();
      })
      .catch(error => { console.error('Error updating shelter operation materiel report:', error); })
}

function shelterUpdateOperationalFood() {
    timeframeRef.value = 1;
    breakfastRef.value = 0;
    lunchRef.value = 0;
    dinnerRef.value = 0;
    snackRef.value = 0;
    dialogShelterUpdateOperationalFoodShow.value = true;
}

function shelterUpdateOperationalFoodYes() {
    dialogShelterUpdateOperationalFoodShow.value = false;
    // perform the update
    performShelterUpdateOperationalFood();
}

function shelterUpdateOperationalFoodNo() {
    dialogShelterUpdateOperationalFoodShow.value = false;
}

function performShelterUpdateOperationalFood() {
    var CS = localSelectedObject.ncSelectedObject.callsign;

    var bodyObject = { 
                        timeframe : timeframeRef.value,
                        breakfast : breakfastRef.value,
                        lunch : lunchRef.value,
                        dinner : dinnerRef.value,
                        snack : snackRef.value
                    };

    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/shelterReports/"+CS+"/operationalFoods"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
            dialogShelterUpdateOperationalFoodShow.value = false;
            fetchShelterReports();
      })
      .catch(error => { console.error('Error updating shelter operation food report:', error); })
}


function shelterUpdateWorker() {
    shiftRef.value = 1;
    healthRef.value = 0;
    mentalRef.value = 0;
    spiritualRef.value = 0;
    caseworkerRef.value = 0;
    feedingRef.value = 0;
    otherRef.value = 0;
    dialogShelterUpdateWorkerShow.value = true;
}

function shelterUpdateWorkerYes() {
    dialogShelterUpdateWorkerShow.value = false;
    // perform the update
    performShelterUpdateWorker();
}

function shelterUpdateWorkerNo() {
    dialogShelterUpdateWorkerShow.value = false;
}

function performShelterUpdateWorker() {
    var CS = localSelectedObject.ncSelectedObject.callsign;

    var bodyObject = { 
                        shift : shiftRef.value,
                        health : healthRef.value,
                        mental : mentalRef.value,
                        spiritual : spiritualRef.value,
                        caseworker : caseworkerRef.value,
                        feeding : feedingRef.value,
                        other : otherRef.value
                    };

    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/shelterReports/"+CS+"/workers"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
            dialogShelterUpdateWorkerShow.value = false;
            fetchShelterReports();
      })
      .catch(error => { console.error('Error updating shelter worker census:', error); })
}

function shelterUpdateCensus() {
    population03Ref.value = 0;
    population47Ref.value = 0;
    population812Ref.value = 0;
    population1318Ref.value = 0;
    population1965Ref.value = 0;
    population66Ref.value = 0;
    dialogShelterUpdateCensusShow.value = true;
}


function shelterUpdateCensusYes() {
    dialogShelterUpdateCensusShow.value = false;
    // perform the update
    performShelterUpdateCensus();
}

function shelterUpdateCensusNo() {
    dialogShelterUpdateCensusShow.value = false;
}

function performShelterUpdateCensus() {
    var CS = localSelectedObject.ncSelectedObject.callsign;

    var bodyObject = { 
                        population03 : population03Ref.value,
                        population47 : population47Ref.value,
                        population812 : population812Ref.value,
                        population1318 : population1318Ref.value,
                        population1965 : population1965Ref.value,
                        population66 : population66Ref.value
                    };

    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/shelterReports/"+CS+"/census"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
          dialogShelterUpdateCensusShow.value = false;
          fetchShelterReports();
      })
      .catch(error => { console.error('Error updating shelter census:', error); })
}

function shelterUpdateStatus() {
    messageRef.value = '';
    statusRef.value = 0;
    stateRef.value = 0;
    dialogShelterUpdateStatusShow.value = true;
}


function shelterUpdateStatusYes() {
    dialogShelterUpdateStatusShow.value = false;
    // perform the update
    performShelterUpdateStatus();
}

function shelterUpdateStatusNo() {
    dialogShelterUpdateStatusShow.value = false;
}

function performShelterUpdateStatus() {
    var CS = localSelectedObject.ncSelectedObject.callsign;

    var bodyObject = { 
                        state : stateRef.value,
                        status : statusRef.value,
                        message : messageRef.value
                    };

    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/shelterReports/"+CS+"/status"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
            dialogShelterUpdateStatusShow.value = false;
            fetchShelterReports();
      })
      .catch(error => { console.error('Error updating shelter status:', error); })
}

function eocUpdateMobilization() {
    eocNameRef.value = '';
    statusRef.value = 0;
    levelRef.value = 0;
    dialogEOCUpdateMobilizationShow.value = true;
}

function eocUpdateMobilizationYes() {
    dialogEOCUpdateMobilizationShow.value = false;
    // perform the update
    performEOCUpdateMobilization();
}

function eocUpdateMobilizationNo() {
    dialogEOCUpdateMobilizationShow.value = false;
}

function performEOCUpdateMobilization() {
    var CS = localSelectedObject.ncSelectedObject.callsign;

    var bodyObject = { 
                        eocName : eocNameRef.value,
                        status : statusRef.value,
                        level : levelRef.value
                    };

    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/eocReports/"+CS+"/mobilizations"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
          dialogEOCUpdateMobilizationShow.value = false;
          fetchEOCReports();
      })
      .catch(error => { console.error('Error updating EOC contacts:', error); })
}

function eocUpdateContacts() {
    incidentCommanderNameRef.value = '';
    directorNameRef.value = '';
    dialogEOCUpdateContactsShow.value = true;
}

function eocUpdateContactsYes() {
    dialogEOCUpdateContactsShow.value = false;
    // perform the update
    performEOCUpdateContacts();
}

function eocUpdateContactsNo() {
    dialogEOCUpdateContactsShow.value = false;
}

function performEOCUpdateContacts() {
    var CS = localSelectedObject.ncSelectedObject.callsign;

    var bodyObject = { 
                        directorName : directorNameRef.value,
                        incidentCommanderName : incidentCommanderNameRef.value
                     };

    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/eocReports/"+CS+"/contacts"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
            dialogEOCUpdateContactsShow.value = false;
            fetchEOCReports();
      })
      .catch(error => { console.error('Error updating EOC contacts:', error); })
}

function identify() {
    dialogIdentifyShow.value = true;
}

function identifyYes() {
    dialogIdentifyShow.value = false;
    // perform the identify
    performIdentify();
}

function identifyNo() {
    dialogIdentifyShow.value = false;
}

function performIdentify() {
    var CS = localSelectedObject.ncSelectedObject.callsign;
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(buildNetCentralUrl("/callsigns/"+CS+"/identifyRequests"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
          dialogIdentifyShow.value = false;
      })
      .catch(error => { console.error('Error identifying callsign:', error); })
}

function clearPriorityReports() {
    dialogClearPriorityReportsShow.value = true;
}

function clearPriorityReportsYes() {
    dialogClearPriorityReportsShow.value = false;

    // perform the clear reports
    performClearPriorityReports();
}

function clearPriorityReportsNo() {
    dialogClearPriorityReportsShow.value = false;
}

function performClearPriorityReports() {
    var CS = localSelectedObject.ncSelectedObject.callsign;
    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(buildNetCentralUrl("/eocReports/"+CS), requestOptions)
      .then(response => {
        return response;
      })
      .then(data => {
      })
      .catch(error => { console.error('Error clearing reports for callsign:', error); })
    fetch(buildNetCentralUrl("/shelterReports/"+CS), requestOptions)
      .then(response => {
        return response;
      })
      .then(data => {
      })
      .catch(error => { console.error('Error clearing reports for callsign:', error); })
}

function remove() {
    dialogRemoveShow.value = true;
}

function removeYes() {
    dialogRemoveShow.value = false;
    // perform the remove
    performRemove();
}

function removeNo() {
    dialogRemoveShow.value = false;
}

function performRemove() {
    var url = "";
    if ((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && ((localSelectedObjectType.value == 'OBJECT') || (localSelectedObjectType.value == 'PRIORITYOBJECT'))) {
      // remove the selected object
      var id = localSelectedObject.ncSelectedObject.id;
      url = buildNetCentralUrl("/APRSObjects/" + id);
    } else if ((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'CALLSIGN')) {
      // remove the selected callsign
      var callsign = localSelectedObject.ncSelectedObject.callsign;
      url = buildNetCentralUrl("/callsigns/" + callsign);
    } else {
      // remove the selected trackstation
      var id = localSelectedObject.ncSelectedObject.id;
      url = buildNetCentralUrl("/trackedStations/" + id);
    }

    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(url, requestOptions)
      .then(response => {
        if (response.status == 200) {
          // refetch the net list
          nudgeRemove(localSelectedObject.ncSelectedObject);
          updateLocalSelectedObject({ncSelectedObject : null});
        }
        return response;
      })
      .catch(error => { console.error('Error removing resource:', error); })
}

function clearReports() {
    dialogClearReportsShow.value = true;
}

function clearReportsYes() {
    // perform the report clearing
    performClearReports();
}

function clearReportsNo() {
    dialogClearReportsShow.value = false;
}

function performClearReports() {
    var callsign = localSelectedObject.ncSelectedObject.callsign;
    var url = buildNetCentralUrl("/weatherReports/callsigns/" + callsign + "/all");

    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(url, requestOptions)
      .then(response => {
        if (response.status == 200) {
          dialogClearReportsShow.value = false;
          latestWeatherReport.value = null;
          weatherReports.value = null;
        } else {
        }
        return response;
      })
      .catch(error => { console.error('Error clearing weather reports:', error); })
}

function track() {
    dialogTrackShow.value = true;
}

function trackYes() {
    dialogTrackShow.value = false;
    // perform the tracking
    performTrack();
}

function trackNo() {
    dialogTrackShow.value = false;
}

function performTrack() {
    performTrackingUpdate(true);
}

function untrack() {
    dialogUntrackShow.value = true;
}

function untrackYes() {
    dialogUntrackShow.value = false;
    // perform the tracking
    performUntrack();
}

function untrackNo() {
    dialogUntrackShow.value = false;
}

function performUntrack() {
    performTrackingUpdate(false);
}

function performTrackingUpdate(enableTrack) {
    var id = localSelectedObject.ncSelectedObject.id;
    localSelectedObject.ncSelectedObject.trackingActive = enableTrack;
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(localSelectedObject.ncSelectedObject)
    };
    fetch(buildNetCentralUrl("/trackedStations/"+id), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
          dialogIdentifyShow.value = false;
          updateLocalSelectedObject({ ncSelectedObject : data });
      })
      .catch(error => { console.error('Error updating tracking:', error); })
}


function editStation() {
    dialogEditStationShow.value = true;
    name2Ref.value = localSelectedObject.ncSelectedObject.name;
    description2Ref.value = localSelectedObject.ncSelectedObject.description;
    callsign2Ref.value  = localSelectedObject.ncSelectedObject.callsign;
    electricalPowerType2Ref.value  = convertElectricalPowerTypeToNumber(localSelectedObject.ncSelectedObject.electricalPowerType);
    backupElectricalPowerType2Ref.value  = convertElectricalPowerTypeToNumber(localSelectedObject.ncSelectedObject.backupElectricalPowerType);
    radioStyle2Ref.value  = convertRadioStyleToNumber(localSelectedObject.ncSelectedObject.radioStyle);
    transmitPower2Ref.value  = localSelectedObject.ncSelectedObject.transmitPower;
    frequencyTx2Ref.value  = localSelectedObject.ncSelectedObject.frequencyTx;
    frequencyRx2Ref.value  = localSelectedObject.ncSelectedObject.frequencyRx;
    tone2Ref.value  = localSelectedObject.ncSelectedObject.tone;
}

function convertElectricalPowerTypeToNumber(val) {
    if (val == null) return null;
    if (val == "COMMERCIAL") return "1";
    if (val == "GENERATOR") return "2";
    if (val == "SOLAR") return "3";
    if (val == "BATTERY") return "4";
    if (val == "OTHER") return "5";
    if (val == "NONE") return "6";
    return "0";
}

function convertRadioStyleToNumber(val) {
    if (val == null) return null;
    if (val == "HANDHELD") return "1";
    if (val == "MOBILE") return "2";
    if (val == "BASE") return "3";
    if (val == "APPLIANCE") return "4";
    if (val == "OTHER") return "5";
    return "0";
}

function convertNumberToElectricalPowerType(val) {
    if (val == null) return null;
    if (val == "1") return "COMMERCIAL";
    if (val == "2") return "GENERATOR";
    if (val == "3") return "SOLAR";
    if (val == "4") return "BATTERY";
    if (val == "5") return "OTHER";
    if (val == "6") return "NONE";
    return "UNKNOWN";
}

function convertNumberToRadioStyle(val) {
    if (val == null) return null;
    if (val == "1") return "HANDHELD";
    if (val == "2") return "MOBILE";
    if (val == "3") return "BASE";
    if (val == "4") return "APPLIANCE";
    if (val == "5") return "OTHER";
    return "UNKNOWN";
}

function editStationYes() {
    dialogEditStationShow.value = false;
    // perform the edit
    performEditStation();
}

function editStationNo() {
    dialogEditStationShow.value = false;
}

function performEditStation() {
    var electricalPowerType2  = convertNumberToElectricalPowerType(electricalPowerType2Ref.value);
    var backupElectricalPowerType2  = convertNumberToElectricalPowerType(backupElectricalPowerType2Ref.value);
    var radioStyle2  = convertNumberToRadioStyle(radioStyle2Ref.value);

    var bodyObject = { callsign : callsign2Ref.value, name : name2Ref.value, description: description2Ref.value, 
                        frequencyTx : frequencyTx2Ref.value, frequencyRx : frequencyRx2Ref.value, tone : tone2Ref.value, 
                        transmitPower : transmitPower2Ref.value , electricalPowerType : electricalPowerType2,
                        backupElectricalPowerType : backupElectricalPowerType2, radioStyle: radioStyle2,
                        id: localSelectedObject.ncSelectedObject.id,
                        lat: localSelectedObject.ncSelectedObject.lat,
                        lon : localSelectedObject.ncSelectedObject.lon,
                        lastHeard: localSelectedObject.ncSelectedObject.lastHeard,
                        trackingActive: localSelectedObject.ncSelectedObject.trackingActive, 
                        status: localSelectedObject.ncSelectedObject.status, 
                        ipAddress: localSelectedObject.ncSelectedObject.ipAddress, 
                        type: localSelectedObject.ncSelectedObject.type 
                      };
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/trackedStations/"+localSelectedObject.ncSelectedObject.id), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogEditStationShow.value = false;
        } else {
          errorMessageRef.value = "An error occurred modifying the station.";
        }
        return response.json();
      })
      .then(data => {
        // refetch the net list
        nudgeUpdate(data);
      })
      .catch(error => { console.error('Error modifying station:', error); })   
}

function editCallsign() {
      dialogEditCallsignShow.value = true;
      callsign2Ref.value  = localSelectedObject.ncSelectedObject.callsign;
      name2Ref.value = localSelectedObject.ncSelectedObject.name;
      state2Ref.value  = localSelectedObject.ncSelectedObject.state;
      country2Ref.value  = localSelectedObject.ncSelectedObject.country;
      license2Ref.value  = localSelectedObject.ncSelectedObject.license;
}

function editCallsignYes() {
    dialogEditCallsignShow.value = false;
    // perform the edit
    performEditCallsign();
}

function editCallsignNo() {
    dialogEditCallsignShow.value = false;
}

function performEditCallsign() {

    var bodyObject = { callsign : callsign2Ref.value, name : name2Ref.value, state: state2Ref.value, 
                        country : country2Ref.value, license : license2Ref.value };
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/callsigns/"+callsign2Ref.value), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogEditCallsignShow.value = false;
        } else {
          errorMessageRef.value = "An error occurred modifying the callsign.";
        }
        return response.json();
      })
      .then(data => {
          // refetch the net list - poke it
          nudgeUpdate(data);
      })
      .catch(error => { console.error('Error modifying callsign', error); })   
}
</script>

<template>
  <!-- dialogs -->
    <div v-if="dialogAceAddShow.value">
      <teleport to="#modals">
        <dialog :open="dialogAceAddShow.value" ref="dialogAceAdd" @close="dialogAceAddShow.value = false" class="topz">  
          <form v-if="dialogAceAddShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            <br>
              <div>
                  <label for="aceCallsignTypeField">Who to allow:</label>
                  <select name="aceCallsignTypeField" id="aceCallsignType" v-model="aceCallsignTypeRef.value" style="display: inline;">
                      <option value="1" selected>Specific Station Callsign</option>
                      <option value="2">Specific Net Callsign Participant</option>
                      <option value="3">Any Net Participant</option>
                      <option value="4">Within proximity</option>
                  </select>
              </div>
              <div v-if="(aceCallsignTypeRef.value == '1')">
                <label for="aceCallsign">Station callsign:</label>
                <input style="padding: 5px;" type="text" id="aceCallsign" v-model="aceCallsignRef.value"/>
              </div>
              <div v-if="(aceCallsignTypeRef.value == '2')">
                <label for="aceCallsign">Net callsign:</label>
                <input style="padding: 5px;" type="text" id="aceCallsign" v-model="aceCallsignRef.value"/>
              </div>
              <div v-if="(aceCallsignTypeRef.value == '4')">
                <label for="aceProximity">Proximity (feet):</label>
                <input style="padding: 5px;" type="number" id="aceProximity" v-model="aceProximityRef.value"/>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="aceAddYes">Add</button>
            <button class="boxButton" v-on:click.native="aceAddNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogAceRemoveShow.value">
      <teleport to="#modals">
        <dialog :open="dialogAceRemoveShow.value" ref="dialogAceRemove" @close="dialogAceRemoveShow.value = false" class="topz">  
          <form v-if="dialogAceRemoveShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            <div v-if="selectedAce.value.proximity != null">
              Do you wish to remove proximity of {{ selectedAce.value.proximity }} feet from {{ selectedAce.value.callsignTarget }} access limitations?
            </div>
            <div v-else>
              Do you wish to remove {{ selectedAce.value.callsignChecked }} from {{ selectedAce.value.callsignTarget }} access?
            </div>
            <br>
            <button class="boxButton" v-on:click.native="aceRemoveYes">Yes</button>
            <button class="boxButton" v-on:click.native="aceRemoveNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogShelterUpdateOperationalMaterielShow.value">
      <teleport to="#modals">
        <dialog :open="dialogShelterUpdateOperationalMaterielShow.value" ref="dialogShelterUpdateOperationalMateriel" @close="dialogShelterUpdateOperationalMaterielShow.value = false" class="topz">  
          <form v-if="dialogShelterUpdateOperationalMaterielShow.value" method="dialog">
            <div class="pagesubheader">Update Shelter Materiel information</div>
            <div class="line"><hr/></div>
            Update the shelter materiel information by time period and type. 
            <br>
              <div>
                  <label for="timeframeField">Timeframe:</label>
                  <select name="timeframeField" id="timeframe" v-model="timeframeRef.value" style="display: inline;">
                      <option value="1" selected>Today</option>
                      <option value="2">Expected tomorrow</option>
                      <option value="3">Needed tomorrow</option>
                  </select>
              </div>
              <div>
                <label for="cots">Cots:</label>
                <input style="padding: 5px;" type="number" id="cots" v-model="cotsRef.value"/>
              </div>
              <div>
                <label for="blankets">Blankets:</label>
                <input style="padding: 5px;" type="number" id="blankets" v-model="blanketsRef.value"/>
              </div>
              <div>
                <label for="comfort">Comfort:</label>
                <input style="padding: 5px;" type="number" id="comfort" v-model="comfortRef.value"/>
              </div>
              <div>
                <label for="cleanup">Cleanup:</label>
                <input style="padding: 5px;" type="number" id="cleanup" v-model="cleanupRef.value"/>
              </div>
              <div>
                <label for="signage">Signage:</label>
                <input style="padding: 5px;" type="number" id="signage" v-model="signageRef.value"/>
              </div>
              <div>
                <label for="other">Other:</label>
                <input style="padding: 5px;" type="number" id="other" v-model="otherRef.value"/>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="shelterUpdateOperationalMaterielYes">Update</button>
            <button class="boxButton" v-on:click.native="shelterUpdateOperationalMaterielNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogShelterUpdateOperationalFoodShow.value">
      <teleport to="#modals">
        <dialog :open="dialogShelterUpdateOperationalFoodShow.value" ref="dialogShelterUpdateOperationalFood" @close="dialogShelterUpdateOperationalFoodShow.value = false" class="topz">  
          <form v-if="dialogShelterUpdateOperationalFoodShow.value" method="dialog">
            <div class="pagesubheader">Update Shelter Food information</div>
            <div class="line"><hr/></div>
            Update the shelter food information by time period and meal types. 
            <br>
              <div>
                  <label for="timeframeField">Timeframe:</label>
                  <select name="timeframeField" id="timeframe" v-model="timeframeRef.value" style="display: inline;">
                      <option value="1" selected>Today</option>
                      <option value="2">Expected tomorrow</option>
                      <option value="3">Needed tomorrow</option>
                  </select>
              </div>
              <div>
                <label for="breakfast">Breakfast:</label>
                <input style="padding: 5px;" type="number" id="breakfast" v-model="breakfastRef.value"/>
              </div>
              <div>
                <label for="lunch">Lunch:</label>
                <input style="padding: 5px;" type="number" id="lunch" v-model="lunchRef.value"/>
              </div>
              <div>
                <label for="dinner">Dinner:</label>
                <input style="padding: 5px;" type="number" id="dinner" v-model="dinnerRef.value"/>
              </div>
              <div>
                <label for="snack">Snack:</label>
                <input style="padding: 5px;" type="number" id="snack" v-model="snackRef.value"/>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="shelterUpdateOperationalFoodYes">Update</button>
            <button class="boxButton" v-on:click.native="shelterUpdateOperationalFoodNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <div v-if="dialogShelterUpdateWorkerShow.value">
      <teleport to="#modals">
        <dialog :open="dialogShelterUpdateWorkerShow.value" ref="dialogShelterUpdateWorker" @close="dialogShelterUpdateWorkerShow.value = false" class="topz">  
          <form v-if="dialogShelterUpdateWorkerShow.value" method="dialog">
            <div class="pagesubheader">Update Worker Census</div>
            <div class="line"><hr/></div>
            Update the shelter worker census by job classification. 
            <br>
              <div>
                  <label for="shiftField">Status:</label>
                  <select name="shiftField" id="shift" v-model="shiftRef.value" style="display: inline;">
                      <option value="1" selected>First</option>
                      <option value="2">Second</option>
                      <option value="3">Third</option>
                  </select>
              </div>
              <div>
                <label for="health">Health:</label>
                <input style="padding: 5px;" type="number" id="health" v-model="healthRef.value"/>
              </div>
              <div>
                <label for="mental">Mental health:</label>
                <input style="padding: 5px;" type="number" id="mental" v-model="mentalRef.value"/>
              </div>
              <div>
                <label for="spiritual">Spiritual:</label>
                <input style="padding: 5px;" type="number" id="spiritual" v-model="spiritualRef.value"/>
              </div>
              <div>
                <label for="caseworker">Case Workers:</label>
                <input style="padding: 5px;" type="number" id="caseworker" v-model="caseworkerRef.value"/>
              </div>
              <div>
                <label for="feeding">Feeding:</label>
                <input style="padding: 5px;" type="number" id="feeding" v-model="feedingRef.value"/>
              </div>
              <div>
                <label for="other">Other:</label>
                <input style="padding: 5px;" type="number" id="other" v-model="otherRef.value"/>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="shelterUpdateWorkerYes">Update</button>
            <button class="boxButton" v-on:click.native="shelterUpdateWorkerNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogShelterUpdateCensusShow.value">
      <teleport to="#modals">
        <dialog :open="dialogShelterUpdateCensusShow.value" ref="dialogShelterUpdateCensus" @close="dialogShelterUpdateCensusShow.value = false" class="topz">  
          <form v-if="dialogShelterUpdateCensusShow.value" method="dialog">
            <div class="pagesubheader">Update Shelter Population Census</div>
            <div class="line"><hr/></div>
            Update the shelter population census by age cohort. 
            <br>
              <div>
                <label for="population03">Ages 0 - 3:</label>
                <input style="padding: 5px;" type="number" id="population03" v-model="population03Ref.value"/>
              </div>
              <div>
                <label for="population47">Ages 4 - 7:</label>
                <input style="padding: 5px;" type="number" id="population47" v-model="population47Ref.value"/>
              </div>
              <div>
                <label for="population812">Ages 8 - 12:</label>
                <input style="padding: 5px;" type="number" id="population812" v-model="population812Ref.value"/>
              </div>
              <div>
                <label for="population1318">Ages 13 - 18:</label>
                <input style="padding: 5px;" type="number" id="population1318" v-model="population1318Ref.value"/>
              </div>
              <div>
                <label for="population1965">Ages 19 - 65:</label>
                <input style="padding: 5px;" type="number" id="population1965" v-model="population1965Ref.value"/>
              </div>
              <div>
                <label for="population66">Ages 66 and over:</label>
                <input style="padding: 5px;" type="number" id="population66" v-model="population66Ref.value"/>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="shelterUpdateCensusYes">Update</button>
            <button class="boxButton" v-on:click.native="shelterUpdateCensusNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogShelterUpdateStatusShow.value">
      <teleport to="#modals">
        <dialog :open="dialogShelterUpdateStatusShow.value" ref="dialogShelterUpdateStatus" @close="dialogShelterUpdateStatusShow.value = false" class="topz">  
          <form v-if="dialogShelterUpdateStatusShow.value" method="dialog">
            <div class="pagesubheader">Update Shelter Status</div>
            <div class="line"><hr/></div>
            Update the shelter status information. 
            <br>
              <div>
                  <label for="statusField">Status:</label>
                  <select name="statusField" id="status" v-model="statusRef.value" style="display: inline;">
                      <option value="0" selected>Unknown</option>
                      <option value="1">Stand-by</option>
                      <option value="2">Active</option>
                  </select>
              </div>
              <div>
                  <label for="stateField">State:</label>
                  <select name="stateField" id="state" v-model="stateRef.value" style="display: inline;">
                      <option value="0" selected>Unknown</option>
                      <option value="1">Closed</option>
                      <option value="2">Open</option>
                  </select>
              </div>
              <div>
                <label for="message">Message:</label>
                <input style="padding: 5px;" type="text" id="message" v-model="messageRef.value"/>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="shelterUpdateStatusYes">Update</button>
            <button class="boxButton" v-on:click.native="shelterUpdateStatusNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogEOCUpdateMobilizationShow.value">
      <teleport to="#modals">
        <dialog :open="dialogEOCUpdateMobilizationShow.value" ref="dialogEOCUpdateMobilization" @close="dialogEOCUpdateMobilizationShow.value = false" class="topz">  
          <form v-if="dialogEOCUpdateMobilizationShow.value" method="dialog">
            <div class="pagesubheader">Update EOC Mobilization Details</div>
            <div class="line"><hr/></div>
            Update the EOC mobilization name, status and level. 
            <br>
              <div>
                <label for="eocName">EOC name:</label>
                <input type="text" id="eocName" v-model="eocNameRef.value"/>
              </div>
              <div>
                  <label for="statusField">Status:</label>
                  <select name="statusField" id="status" v-model="statusRef.value" style="display: inline;">
                      <option value="0" selected>Unknown</option>
                      <option value="1">Normal</option>
                      <option value="2">Partial</option>
                      <option value="3">Full</option>
                  </select>
              </div>
              <div>
                  <label for="levelField">Level:</label>
                  <select name="levelField" id="level" v-model="levelRef.value" style="display: inline;">
                      <option value="0" selected>No active incident</option>
                      <option value="1">Type 1</option>
                      <option value="2">Type 2</option>
                      <option value="3">Type 3</option>
                      <option value="4">Type 4</option>
                      <option value="5">Type 5</option>
                  </select>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="eocUpdateMobilizationYes">Update</button>
            <button class="boxButton" v-on:click.native="eocUpdateMobilizationNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogEOCUpdateContactsShow.value">
      <teleport to="#modals">
        <dialog :open="dialogEOCUpdateContactsShow.value" ref="dialogEOCUpdateContacts" @close="dialogEOCUpdateContactsShow.value = false" class="topz">  
          <form v-if="dialogEOCUpdateContactsShow.value" method="dialog">
            <div class="pagesubheader">Update EOC Contacts</div>
            <div class="line"><hr/></div>
            Update the EOC contacts. 
            <br>
              <div>
                <label for="directorName">EOC director name:</label>
                <input style="padding: 5px;" type="text" id="directorName" v-model="directorNameRef.value"/>
              </div>
              <div>
                <label for="incidentCommanderName">Incident Commander name:</label>
                <input style="padding: 5px;" type="text" id="incidentCommanderName" v-model="incidentCommanderNameRef.value" />
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="eocUpdateContactsYes">Update</button>
            <button class="boxButton" v-on:click.native="eocUpdateContactsNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogClearPriorityReportsShow.value">
      <teleport to="#modals">
        <dialog :open="dialogClearPriorityReportsShow.value" ref="dialogClearPriorityReports" @close="dialogClearPriorityReportsShow.value = false" class="topz">  
          <form v-if="dialogClearPriorityReportsShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to clear all reports for {{ localSelectedObject.ncSelectedObject.callsign }} ?
            <br>
            <button class="boxButton" v-on:click.native="clearPriorityReportsYes">Yes</button>
            <button class="boxButton" v-on:click.native="clearPriorityReportsNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogIdentifyShow.value">
      <teleport to="#modals">
        <dialog :open="dialogIdentifyShow.value" ref="dialogIdentify" @close="dialogIdentifyShow.value = false" class="topz">  
          <form v-if="dialogIdentifyShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to attempt to identify {{ localSelectedObject.ncSelectedObject.callsign }} with WHO-15?
            <br>
            <button class="boxButton" v-on:click.native="identifyYes">Yes</button>
            <button class="boxButton" v-on:click.native="identifyNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogRemoveShow.value">
      <teleport to="#modals">
        <dialog :open="dialogRemoveShow.value" ref="dialogRemove" @close="dialogRemoveShow.value = false" class="topz">  
          <form v-if="dialogRemoveShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to remove {{ localSelectedObject.ncSelectedObject.callsign }} ?
            <br>
            <button class="boxButton" v-on:click.native="removeYes">Yes</button>
            <button class="boxButton" v-on:click.native="removeNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogTrackShow.value">
      <teleport to="#modals">
        <dialog :open="dialogTrackShow.value" ref="dialogTrack" @close="dialogTrackShow.value = false" class="topz">  
          <form v-if="dialogTrackShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to track {{ localSelectedObject.ncSelectedObject.callsign }} ?
            <br>
            <button class="boxButton" v-on:click.native="trackYes">Yes</button>
            <button class="boxButton" v-on:click.native="trackNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogUntrackShow.value">
      <teleport to="#modals">
        <dialog :open="dialogUntrackShow.value" ref="dialogUntrack" @close="dialogUntrackShow.value = false" class="topz">  
          <form v-if="dialogUntrackShow.value" method="dialog">
            Do you wish to stop tracking {{ localSelectedObject.ncSelectedObject.callsign }} ?
            <br>
            <button class="boxButton" v-on:click.native="untrackYes">Yes</button>
            <button class="boxButton" v-on:click.native="untrackNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogClearReportsShow.value">
      <teleport to="#modals">
        <dialog :open="dialogClearReportsShow.value" ref="dialogClearReports" @close="dialogClearReportsShow.value = false" class="topz">  
          <form v-if="dialogClearReportsShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to clear all weather reports provided by {{ localSelectedObject.ncSelectedObject.callsign }} ?
            <br>
            <button class="boxButton" v-on:click.native="clearReportsYes">Yes</button>
            <button class="boxButton" v-on:click.native="clearReportsNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogEditStationShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogEditStationShow.value" ref="dialogEditStation" @close="dialogEditStationShow.value = false" class="topz">  
          <form v-if="dialogEditStationShow.value" method="dialog">
            <div class="pagesubheader">Edit Station {{ callsign2Ref.value }}</div>
            <div class="line"><hr/></div>
            Modify the station information as needed. Some values cannot be changed. 
            <br>
              <div>
                <label for="callsignField">Callsign:</label>
                <input style="padding: 5px;" type="text" id="callsignField" v-model="callsign2Ref.value" readonly/>
              </div>
              <div>
                <label for="nameField">Name:</label>
                <input style="padding: 5px;" type="text" id="nameField" v-model="name2Ref.value" />
              </div>
              <div>
                <label for="descriptionField">Description:</label>
                <input style="padding: 5px;" type="text" id="descriptionField" v-model="description2Ref.value" />
              </div>
              <div>
                  <label for="electricalPowerTypeField">Electrical power:</label>
                  <select name="electricalPowerTypeField" id="electricalPowerType" v-model="electricalPowerType2Ref.value" style="display: inline;">
                    <div v-if="(electricalPowerType2Ref.value == '0')">
                      <option value="0" selected>Unknown</option>
                    </div>
                    <div v-else>
                      <option value="0">Unknown</option>
                    </div>
                    <div v-if="(electricalPowerType2Ref.value == '1')">
                      <option value="1" selected>Commercial</option>
                    </div>
                    <div v-else>
                      <option value="1">Commercial</option>
                    </div>
                    <div v-if="(electricalPowerType2Ref.value == '2')">
                      <option value="2" selected>Generator</option>
                    </div>
                    <div v-else>
                      <option value="2">Generator</option>
                    </div>
                    <div v-if="(electricalPowerType2Ref.value == '3')">
                      <option value="3" selected>Solar</option>
                    </div>
                    <div v-else>
                      <option value="3">Solar</option>
                    </div>
                    <div v-if="(electricalPowerType2Ref.value == '4')">
                      <option value="4" selected>Battery</option>
                    </div>
                    <div v-else>
                      <option value="4">Battery</option>
                    </div>
                    <div v-if="(electricalPowerType2Ref.value == '5')">
                      <option value="5" selected>Other</option>
                    </div>
                    <div v-else>
                      <option value="5">Other</option>
                    </div>
                    <div v-if="(electricalPowerType2Ref.value == '6')">
                      <option value="5" selected>None</option>
                    </div>
                    <div v-else>
                      <option value="5">None</option>
                    </div>
                  </select>
              </div>
              <div>
                  <label for="backupElectricalPowerTypeField">Backup electrical power:</label>
                  <select name="backupElectricalPowerTypeField" id="backupElectricalPowerType" v-model="backupElectricalPowerType2Ref.value" style="display: inline;">
                    <div v-if="(backupElectricalPowerType2Ref.value == '0')">
                      <option value="0" selected>Unknown</option>
                    </div>
                    <div v-else>
                      <option value="0">Unknown</option>
                    </div>
                    <div v-if="(backupElectricalPowerType2Ref.value == '1')">
                      <option value="1" selected>Commercial</option>
                    </div>
                    <div v-else>
                      <option value="1">Commercial</option>
                    </div>
                    <div v-if="(backupElectricalPowerType2Ref.value == '2')">
                      <option value="2" selected>Generator</option>
                    </div>
                    <div v-else>
                      <option value="2">Generator</option>
                    </div>
                    <div v-if="(backupElectricalPowerType2Ref.value == '3')">
                      <option value="3" selected>Solar</option>
                    </div>
                    <div v-else>
                      <option value="3">Solar</option>
                    </div>
                    <div v-if="(backupElectricalPowerType2Ref.value == '4')">
                      <option value="4" selected>Battery</option>
                    </div>
                    <div v-else>
                      <option value="4">Battery</option>
                    </div>
                    <div v-if="(backupElectricalPowerType2Ref.value == '5')">
                      <option value="5" selected>Other</option>
                    </div>
                    <div v-else>
                      <option value="5">Other</option>
                    </div>
                    <div v-if="(backupElectricalPowerType2Ref.value == '6')">
                      <option value="5" selected>None</option>
                    </div>
                    <div v-else>
                      <option value="5">None</option>
                    </div>
                  </select>
              </div>
              <div>
                  <label for="radioStyleField">Radio style:</label>
                  <select name="radioStyleField" id="radioStyle" v-model="radioStyle2Ref.value" style="display: inline;">
                    <div v-if="(radioStyle2Ref.value == '0')">
                      <option value="0" selected>Unknown</option>
                    </div>
                    <div v-else>
                      <option value="0">Unknown</option>
                    </div>
                    <div v-if="(radioStyle2Ref.value == '1')">
                      <option value="1" selected>Handheld</option>
                    </div>
                    <div v-else>
                      <option value="1">Handheld</option>
                    </div>
                    <div v-if="(radioStyle2Ref.value == '2')">
                      <option value="2" selected>Mobile</option>
                    </div>
                    <div v-else>
                      <option value="2">Mobile</option>
                    </div>
                    <div v-if="(radioStyle2Ref.value == '3')">
                      <option value="3" selected>Base</option>
                    </div>
                    <div v-else>
                      <option value="3">Base</option>
                    </div>
                    <div v-if="(radioStyle2Ref.value == '4')">
                      <option value="4" selected>Appliance</option>
                    </div>
                    <div v-else>
                      <option value="4">Appliance</option>
                    </div>
                    <div v-if="(radioStyle2Ref.value == '5')">
                      <option value="5" selected>Internet</option>
                    </div>
                    <div v-else>
                      <option value="5">Internet</option>
                    </div>
                    <div v-if="(radioStyle2Ref.value == '6')">
                      <option value="5" selected>Other</option>
                    </div>
                    <div v-else>
                      <option value="5">Other</option>
                    </div>
                  </select>
              </div>
              <div>
                <label for="transmitPowerField">Transmit power (watts):</label>
                <input style="padding: 5px;" type="text" id="transmitPowerField" v-model="transmitPower2Ref.value" />
              </div>
              <div>
                <label for="transmitFrequencyField">Transmit Frequency:</label>
                <input style="padding: 5px;" type="text" id="transmitFrequencyField" v-model="frequencyTx2Ref.value" />
              </div>
              <div>
                <label for="receiveFrequencyField">Receive Frequency:</label>
                <input style="padding: 5px;" type="text" id="receiveFrequencyField" v-model="frequencyRx2Ref.value" />
              </div>
              <div>
                <label for="toneField">Tone:</label>
                <input style="padding: 5px;" type="text" id="toneField" v-model="tone2Ref.value" />
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="editStationYes">Modify</button>
            <button class="boxButton" v-on:click.native="editStationNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogEditCallsignShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogEditCallsignShow.value" ref="dialogEditCallsign" @close="dialogEditCallsignShow.value = false" class="topz">  
          <form v-if="dialogEditCallsignShow.value" method="dialog">
            <div class="pagesubheader">Edit Callsign {{ callsign2Ref.value }}</div>
            <div class="line"><hr/></div>
            Modify the callsign owner information.
            <br>
              <div>
                <label for="nameField">Name:</label>
                <input style="padding: 5px;" type="text" id="nameField" v-model="name2Ref.value" />
              </div>
              <div>
                <label for="stateField">State:</label>
                <input style="padding: 5px;" type="text" id="stateField" v-model="state2Ref.value" />
              </div>
              <div>
                <label for="countryField">Country:</label>
                <input style="padding: 5px;" type="text" id="countryField" v-model="country2Ref.value" />
              </div>
              <div>
                <label for="licenseField">License:</label>
                <input style="padding: 5px;" type="text" id="licenseField" v-model="license2Ref.value" />
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="editCallsignYes">Modify</button>
            <button class="boxButton" v-on:click.native="editCallsignNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

  <!-- main page -->
    <div v-if="((localSelectedObject != null) && (localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.id != null))">
      <div v-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && ((localSelectedObjectType.value == 'OBJECT')))">
        <!-- plain old object -->
        <Tabs>
          <Tab value="Details">
            <br>Name: {{ localSelectedObject.ncSelectedObject.name }}
            <br>Status: {{ localSelectedObject.ncSelectedObject.status }}
            <br>Location: {{ localSelectedObject.ncSelectedObject.lat }} / {{ localSelectedObject.ncSelectedObject.lon }}
            <br>Comment: {{ localSelectedObject.ncSelectedObject.comment }}
            <br>Last heard: {{ localSelectedObject.ncSelectedObject.prettyLastHeard }}
            <br>Type: {{ localSelectedObject.ncSelectedObject.type }}
          </Tab>
          <Tab value="Actions" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))">
            <div class="grid-container-actions">
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="remove">Remove</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Remove the station from Net Central.  It may be added if heard again via APRS.
              </div>
            </div>
          </Tab>
          <Tab value="APRS Status">
            <div v-if="((statusReports != null) && (statusReports.value != null))">
              <b>APRS Status information:</b>
              <EasyDataTable :headers="headersStatus" :items="statusReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No APRS Status found.
            </div>
          </Tab>
        </Tabs>
      </div>

      <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'PRIORITYOBJECT') && (localSelectedObject.ncSelectedObject.type == 'SHELTER'))">
        <!-- shelter -->
        <Tabs>
          <Tab value="Details">
            <br>Name: {{ localSelectedObject.ncSelectedObject.name }}
            <br>Status: {{ localSelectedObject.ncSelectedObject.status }}
            <br>Location: {{ localSelectedObject.ncSelectedObject.lat }} / {{ localSelectedObject.ncSelectedObject.lon }}
            <br>Comment: {{ localSelectedObject.ncSelectedObject.comment }}
            <br>Last heard: {{ localSelectedObject.ncSelectedObject.prettyLastHeard }}
            <br>Type: {{ localSelectedObject.ncSelectedObject.type }}
          </Tab>
          <Tab value="APRS Status">
            <div v-if="((statusReports != null) && (statusReports.value != null))">
              <b>APRS Status information:</b>
              <EasyDataTable :headers="headersStatus" :items="statusReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No APRS Status found.
            </div>
          </Tab>
          <Tab value="Operational Status">
            <div v-if="((shelterStatusReport != null) && (shelterStatusReport.value != null))">
              <b>Latest operational status:</b>
              <table>
                <tbody>
                  <tr>
                    <td>Status</td> <td>{{ shelterStatusReport.value.status }}</td>
                  </tr>
                  <tr>
                    <td>State</td> <td>{{ shelterStatusReport.value.state }}</td>
                  </tr>
                  <tr>
                    <td>Message</td> <td>{{ shelterStatusReport.value.message }}</td>
                  </tr>
                </tbody>
              </table>
              <br>
              <br>Last report: {{ shelterStatusReport.value.prettyLastReportedTime }}
            </div>
            <div v-else>
              <br>
              <br> No shelter status report found.
            </div>
          </Tab>
          <Tab value="Census">
            <div v-if="((shelterCensusReport != null) && (shelterCensusReport.value != null))">
              <b>Latest population census by age:</b>
              <table>
                <thead>
                  <tr>
                    <th>Range</th><th>Count</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>0-3</td> <td>{{ shelterCensusReport.value.population03 }}</td>
                  </tr>
                  <tr>
                    <td>4-7</td> <td>{{ shelterCensusReport.value.population47 }}</td>
                  </tr>
                  <tr>
                    <td>8-12</td> <td>{{ shelterCensusReport.value.population812 }}</td>
                  </tr>
                  <tr>
                    <td>13-18</td> <td>{{ shelterCensusReport.value.population1318 }}</td>
                  </tr>
                  <tr>
                    <td>19-65</td> <td>{{ shelterCensusReport.value.population1965 }}</td>
                  </tr>
                  <tr>
                    <td>66+</td> <td>{{ shelterCensusReport.value.population66 }}</td>
                  </tr>
                </tbody>
              </table>
              <br>
              <br>Last report: {{ shelterCensusReport.value.prettyLastReportedTime }}
            </div>
            <div v-else>
              <br>
              <br> No shelter census report found.
            </div>
          </Tab>
          <Tab value="Census History">
            <div v-if="((shelterCensusReports != null) && (shelterCensusReports.value != null))">
              <b>Historical census information:</b>
              <EasyDataTable :headers="headersShelterCensus" :items="shelterCensusReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No shelter census report found.
            </div>
          </Tab>
          <Tab value="Workers">
            <div v-if="((shelterWorkersReport1 != null) && (shelterWorkersReport1.value != null) && (shelterWorkersReport2 != null) && (shelterWorkersReport2.value != null) && (shelterWorkersReport3 != null) && (shelterWorkersReport3.value != null))">
              <b>Latest worker census:</b>
              <table>
                <thead>
                  <tr>
                    <th>Classification</th><th>First Shift</th><th>Second Shift</th><th>Third Shift</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>Health</td> <td>{{ shelterWorkersReport1.value.health }}</td><td>{{ shelterWorkersReport2.value.health }}</td><td>{{ shelterWorkersReport3.value.health }}</td>
                  </tr>
                  <tr>
                    <td>Mental</td> <td>{{ shelterWorkersReport1.value.mental }}</td><td>{{ shelterWorkersReport2.value.mental }}</td><td>{{ shelterWorkersReport3.value.mental }}</td>
                  </tr>
                  <tr>
                    <td>Spiritual</td> <td>{{ shelterWorkersReport1.value.spiritual }}</td><td>{{ shelterWorkersReport2.value.spiritual }}</td><td>{{ shelterWorkersReport3.value.spiritual }}</td>
                  </tr>
                  <tr>
                    <td>Case Worker</td> <td>{{ shelterWorkersReport1.value.caseworker }}</td><td>{{ shelterWorkersReport2.value.caseworker }}</td><td>{{ shelterWorkersReport3.value.caseworker }}</td>
                  </tr>
                  <tr>
                    <td>Feeding</td> <td>{{ shelterWorkersReport1.value.feeding }}</td><td>{{ shelterWorkersReport2.value.feeding }}</td><td>{{ shelterWorkersReport3.value.feeding }}</td>
                  </tr>
                  <tr>
                    <td>Other</td> <td>{{ shelterWorkersReport1.value.other }}</td><td>{{ shelterWorkersReport2.value.other }}</td><td>{{ shelterWorkersReport3.value.other }}</td>
                  </tr>
                  <tr>
                    <td>Last report:</td> <td>{{ shelterWorkersReport1.value.prettyLastReportedTime }}</td><td>{{ shelterWorkersReport2.value.prettyLastReportedTime }}</td><td>{{ shelterWorkersReport3.value.prettyLastReportedTime }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div v-else>
              <br>
              <br> No shelter workers report found.
            </div>
          </Tab>
          <Tab value="Food">
            <div v-if="((shelterOperationalFoodReport1 != null) && (shelterOperationalFoodReport1.value != null) && (shelterOperationalFoodReport2 != null) && (shelterOperationalFoodReport2.value != null) && (shelterOperationalFoodReport3 != null) && (shelterOperationalFoodReport3.value != null))">
              <b>Latest shelter food:</b>
              <table>
                <thead>
                  <tr>
                    <th>Meal</th><th>Provided Today</th><th>Have for Tomorrow</th><th>Needed for Tomorrow</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>Breakfast</td> <td>{{ shelterOperationalFoodReport1.value.breakfast }}</td><td>{{ shelterOperationalFoodReport2.value.breakfast }}</td><td>{{ shelterOperationalFoodReport3.value.breakfast }}</td>
                  </tr>
                  <tr>
                    <td>Lunch</td> <td>{{ shelterOperationalFoodReport1.value.lunch }}</td><td>{{ shelterOperationalFoodReport2.value.lunch }}</td><td>{{ shelterOperationalFoodReport3.value.lunch }}</td>
                  </tr>
                  <tr>
                    <td>Dinner</td> <td>{{ shelterOperationalFoodReport1.value.dinner }}</td><td>{{ shelterOperationalFoodReport2.value.dinner }}</td><td>{{ shelterOperationalFoodReport3.value.dinner }}</td>
                  </tr>
                  <tr>
                    <td>Snack</td> <td>{{ shelterOperationalFoodReport1.value.snack }}</td><td>{{ shelterOperationalFoodReport2.value.snack }}</td><td>{{ shelterOperationalFoodReport3.value.snack }}</td>
                  </tr>
                  <tr>
                    <td>Last report</td> <td>{{ shelterOperationalFoodReport1.value.prettyLastReportedTime }}</td><td>{{ shelterOperationalFoodReport2.value.prettyLastReportedTime }}</td><td>{{ shelterOperationalFoodReport3.value.prettyLastReportedTime }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div v-else>
              <br>
              <br> No shelter food report found.
            </div>
          </Tab>
          <Tab value="Materiel">
            <div v-if="((shelterOperationalMaterielReport1 != null) && (shelterOperationalMaterielReport1.value != null) && (shelterOperationalMaterielReport2 != null) && (shelterOperationalMaterielReport2.value != null) && (shelterOperationalMaterielReport3 != null) && (shelterOperationalMaterielReport3.value != null))">
              <b>Latest shelter materiel:</b>
              <table>
                <thead>
                  <tr>
                    <th>Category</th><th>Provided Today</th><th>Have for Tomorrow</th><th>Needed for Tomorrow</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>Cots</td> <td>{{ shelterOperationalMaterielReport1.value.cots }}</td><td>{{ shelterOperationalMaterielReport2.value.cots }}</td><td>{{ shelterOperationalMaterielReport3.value.cots }}</td>
                  </tr>
                  <tr>
                    <td>Blankets</td> <td>{{ shelterOperationalMaterielReport1.value.blankets }}</td><td>{{ shelterOperationalMaterielReport2.value.blankets }}</td><td>{{ shelterOperationalMaterielReport3.value.blankets }}</td>
                  </tr>
                  <tr>
                    <td>Comfort</td> <td>{{ shelterOperationalMaterielReport1.value.comfort }}</td><td>{{ shelterOperationalMaterielReport2.value.comfort }}</td><td>{{ shelterOperationalMaterielReport3.value.comfort }}</td>
                  </tr>
                  <tr>
                    <td>Cleanup</td> <td>{{ shelterOperationalMaterielReport1.value.cleanup }}</td><td>{{ shelterOperationalMaterielReport2.value.cleanup }}</td><td>{{ shelterOperationalMaterielReport3.value.cleanup }}</td>
                  </tr>
                  <tr>
                    <td>Signage</td> <td>{{ shelterOperationalMaterielReport1.value.signage }}</td><td>{{ shelterOperationalMaterielReport2.value.signage }}</td><td>{{ shelterOperationalMaterielReport3.value.signage }}</td>
                  </tr>
                  <tr>
                    <td>Other</td> <td>{{ shelterOperationalMaterielReport1.value.other }}</td><td>{{ shelterOperationalMaterielReport2.value.other }}</td><td>{{ shelterOperationalMaterielReport3.value.other }}</td>
                  </tr>
                  <tr>
                    <td>Last report</td> <td>{{ shelterOperationalMaterielReport1.value.prettyLastReportedTime }}</td><td>{{ shelterOperationalMaterielReport2.value.prettyLastReportedTime }}</td><td>{{ shelterOperationalMaterielReport3.value.prettyLastReportedTime }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div v-else>
              <br>
              <br> No shelter materiel report found.
            </div>
          </Tab>
          <Tab value="Access">
            <div v-if="((accessControlList != null) && (accessControlList.value != null) && (accessControlList.value.length != 0))">
              <b>Only the following callsigns have the ability to send report updates:</b>
              <div class="grid-container-access-control">
                <div>
                  <EasyDataTable :headers="headersAccessControl" :items="accessControlList.value" :rows-per-page="10"
                    :body-row-class-name="getAceRowClass"
                    @click-row="clickAce" buttons-pagination
                  />
                </div>
                <div>
                  <!-- buttons -->
                  <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                    <button class="boxButton" v-on:click.native="aceAdd">Add</button>
                  </div>
                  <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null) && (selectedAce != null) && (selectedAce.value != null))" class="grid-item">
                    <button class="boxButton" v-on:click.native="aceRemove">Remove</button>
                  </div>
                </div>
              </div>
            </div>
            <div v-else>
              <br>
              <br> Access open to all.
              <br>
              <button class="boxButton" v-on:click.native="aceAdd">Add</button>
            </div>
          </Tab>
          <Tab value="Actions" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))">
            <div class="grid-container-actions">
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="shelterUpdateStatus">Update status</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Update shelter status.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="shelterUpdateCensus">Update census</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Update population census by age cohort
              </div>

              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="shelterUpdateWorker">Update worker census</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Update shelter worker census by category.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="shelterUpdateOperationalFood">Update food</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Update shelter food information by meal (today, tomorrow, needs).
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="shelterUpdateOperationalMateriel">Update materiel</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Update shelter materiel information by type (today, tomorrow, needs).
              </div>

              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="clearPriorityReports">Clear reports</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Clear all of the reports.  New ones will be added if heard again via APRS.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="remove">Remove</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Remove the station from Net Central.  It may be added if heard again via APRS.
              </div>
            </div>
          </Tab>
        </Tabs>
      </div>

      <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'PRIORITYOBJECT') && (localSelectedObject.ncSelectedObject.type == 'EOC'))">
        <!-- shelter -->
        <Tabs>
          <Tab value="Details">
            <br>Name: {{ localSelectedObject.ncSelectedObject.name }}
            <br>Status: {{ localSelectedObject.ncSelectedObject.status }}
            <br>Location: {{ localSelectedObject.ncSelectedObject.lat }} / {{ localSelectedObject.ncSelectedObject.lon }}
            <br>Comment: {{ localSelectedObject.ncSelectedObject.comment }}
            <br>Last heard: {{ localSelectedObject.ncSelectedObject.prettyLastHeard }}
            <br>Type: {{ localSelectedObject.ncSelectedObject.type }}
          </Tab>
          <Tab value="APRS Status">
            <div v-if="((statusReports != null) && (statusReports.value != null))">
              <b>APRS Status information:</b>
              <EasyDataTable :headers="headersStatus" :items="statusReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No APRS Status found.
            </div>
          </Tab>
          <Tab value="Mobilization">
            <div v-if="((eocMobilizationReport != null) && (eocMobilizationReport.value != null))">
              <b>Latest mobilization information:</b>
              <table>
                <tbody>
                  <tr>
                    <td>EOC Name</td> <td>{{ eocMobilizationReport.value.eocName }}</td>
                  </tr>
                  <tr>
                    <td>Status</td> <td>{{ eocMobilizationReport.value.status }}</td>
                  </tr>
                  <tr>
                    <td>Level</td> <td>{{ eocMobilizationReport.value.level }}</td>
                  </tr>
                </tbody>
              </table>
              <br>
              <br>Last report: {{ eocMobilizationReport.value.prettyLastReportedTime }}
            </div>
            <div v-else>
              <br>
              <br> No EOC mobilization report found.
            </div>
          </Tab>
          <Tab value="Mobilization History">
            <div v-if="((eocMobilizationReports != null) && (eocMobilizationReports.value != null))">
              <b>Historical mobilization information:</b>
              <EasyDataTable :headers="headersEOCMobilizations" :items="eocMobilizationReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No EOC mobilization reports found.
            </div>
          </Tab>
          <Tab value="Contacts">
            <div v-if="((eocContactReport != null) && (eocContactReport.value != null))">
              <b>Latest contact information:</b>
              <table>
                <tbody>
                  <tr>
                    <td>EOC Director:</td> <td>{{ eocContactReport.value.directorName }}</td>
                  </tr>
                  <tr>
                    <td>Incident Commander:</td> <td>{{ eocContactReport.value.incidentCommanderName }}</td>
                  </tr>
                </tbody>
              </table>
              <br>
              <br>Last report: {{ eocContactReport.value.prettyLastReportedTime }}
            </div>
            <div v-else>
              <br>
              <br> No EOC contact report found.
            </div>
          </Tab>
          <Tab value="Contact History">
            <div v-if="((eocContactReports != null) && (eocContactReports.value != null))">
              <b>Historical contact information:</b>
              <EasyDataTable :headers="headersEOCContacts" :items="eocContactReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No EOC contact reports found.
            </div>
          </Tab>
          <Tab value="Access">
            <div v-if="((accessControlList != null) && (accessControlList.value != null) && (accessControlList.value.length != 0))">
              <b>Only the following callsigns have the ability to send report updates:</b>
              <div class="grid-container-access-control">
                <div>
                  <EasyDataTable :headers="headersAccessControl" :items="accessControlList.value" :rows-per-page="10"
                    :body-row-class-name="getAceRowClass"
                    @click-row="clickAce" buttons-pagination
                  />
                </div>
                <div>
                  <!-- buttons -->
                  <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                    <button class="boxButton" v-on:click.native="aceAdd">Add</button>
                  </div>
                  <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null) && (selectedAce != null) && (selectedAce.value != null))" class="grid-item">
                    <button class="boxButton" v-on:click.native="aceRemove">Remove</button>
                  </div>
                </div>
              </div>
            </div>
            <div v-else>
              <br>
              <br> Access open to all.
              <br>
              <button class="boxButton" v-on:click.native="aceAdd">Add</button>
            </div>
          </Tab>
          <Tab value="Actions" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))">
            <div class="grid-container-actions">
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="eocUpdateContacts">Update contacts</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Update EOC contact information, including director and incident commander names.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="eocUpdateMobilization">Update status</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Update EOC mobilization state
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="clearPriorityReports">Clear reports</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Clear all of the reports.  New ones will be added if heard again via APRS.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="remove">Remove</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Remove the station from Net Central.  It may be added if heard again via APRS.
              </div>
            </div>
          </Tab>
        </Tabs>
      </div>

      <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'PRIORITYOBJECT') && (localSelectedObject.ncSelectedObject.type == 'MEDICAL'))">
        <Tabs>
          <Tab value="Details">
            <br>Name: {{ localSelectedObject.ncSelectedObject.name }}
            <br>License: {{ localSelectedObject.ncSelectedObject.license }}
            <br>Location: {{ localSelectedObject.ncSelectedObject.state }} / {{ localSelectedObject.ncSelectedObject.country }}
          </Tab>
          <Tab value="APRS Status">
            <div v-if="((statusReports != null) && (statusReports.value != null))">
              <b>APRS Status information:</b>
              <EasyDataTable :headers="headersStatus" :items="statusReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No APRS Status found.
            </div>
          </Tab>
          <Tab value="Workers">
            Medical worker information
          </Tab>
          <Tab value="Access">
            <div v-if="((accessControlList != null) && (accessControlList.value != null) && (accessControlList.value.length != 0))">
              <b>Only the following callsigns have the ability to send report updates:</b>
              <div class="grid-container-access-control">
                <div>
                  <EasyDataTable :headers="headersAccessControl" :items="accessControlList.value" :rows-per-page="10"
                    :body-row-class-name="getAceRowClass"
                    @click-row="clickAce" buttons-pagination
                  />
                </div>
                <div>
                  <!-- buttons -->
                  <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                    <button class="boxButton" v-on:click.native="aceAdd">Add</button>
                  </div>
                  <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null) && (selectedAce != null) && (selectedAce.value != null))" class="grid-item">
                    <button class="boxButton" v-on:click.native="aceRemove">Remove</button>
                  </div>
                </div>
              </div>
            </div>
            <div v-else>
              <br>
              <br> Access open to all.
              <br>
              <button class="boxButton" v-on:click.native="aceAdd">Add</button>
            </div>
          </Tab>
          <Tab value="Actions" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))">
            <div class="grid-container-actions">
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="clearPriorityReports">Clear reports</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Clear all of the reports.  New ones will be added if heard again via APRS.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="remove">Remove</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Remove the station from Net Central.  It may be added if heard again via APRS.
              </div>
            </div>
          </Tab>
        </Tabs>
      </div>

      <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'CALLSIGN'))">
        <Tabs>
          <Tab value="Details">
            <br>Name: {{ localSelectedObject.ncSelectedObject.name }}
            <br>License: {{ localSelectedObject.ncSelectedObject.license }}
            <br>Location: {{ localSelectedObject.ncSelectedObject.state }} / {{ localSelectedObject.ncSelectedObject.country }}
          </Tab>
          <Tab value="Actions" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))">
            <div class="grid-container-actions">
              <div v-if="accesstokenRef.value != null" class="grid-item">
                <button class="boxButton" v-on:click.native="identify">Identify</button>
              </div>
              <div v-if="accesstokenRef.value != null" class="grid-item">
                Contact WHO-15 to determine the name, location and license for the callsign.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="editCallsign">Edit</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Edit properties of the callsign.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="remove">Remove</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Remove the station from Net Central.  It may be added if heard again via APRS.
              </div>
            </div>
          </Tab>
        </Tabs>
      </div>

      <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'WEATHER'))">
        <Tabs>
          <Tab value="Details">
            <br>Name: {{ localSelectedObject.ncSelectedObject.name }}
            <br>License: {{ localSelectedObject.ncSelectedObject.license }}
            <br>Location: {{ localSelectedObject.ncSelectedObject.state }} / {{ localSelectedObject.ncSelectedObject.country }}
          </Tab>
          <Tab value="APRS Status">
            <div v-if="((statusReports != null) && (statusReports.value != null))">
              <b>APRS Status information:</b>
              <EasyDataTable :headers="headersStatus" :items="statusReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No APRS Status found.
            </div>
          </Tab>
          <Tab value="Latest Weather">
            <div v-if="((latestWeatherReport != null) && (latestWeatherReport.value != null))" class="grid-container-weather" >
                <div class="weather-box-header">Temperature</div>
                <div class="weather-box-header">Atmosphere</div>
                <div class="weather-box-header">Precipitation</div>
                <div class="weather-temperature">{{ latestWeatherReport.value.temperature }}F</div>
                <div>Humidity: {{ latestWeatherReport.value.humidity }} %</div>
                <div>Last hour: {{ latestWeatherReport.value.rainfallLast1Hr }} inches</div>
                <div>Pressure: {{ latestWeatherReport.value.barometricPressure }} mb</div>
                <div>Last day: {{ latestWeatherReport.value.rainfallLast24Hr }} inches</div>
                <div>Wind: {{ latestWeatherReport.value.windSpeed }} MPH {{ latestWeatherReport.value.windDirection }} degrees</div>
                <div>Since 00:00: {{ latestWeatherReport.value.rainfallSinceMidnight }} inches</div>
                <div>{{ latestWeatherReport.value.prettyLdtime }}</div>
                <div>Gust: {{ latestWeatherReport.value.gust }} MPH</div>
                <div>Snow: {{ latestWeatherReport.value.snowfallLast24Hr }} inches</div>
              </div>
          </Tab>
          <Tab value="Past Weather">
            <div v-if="((weatherReports != null) && (weatherReports.value != null))" >
              <EasyDataTable :headers="headers" :items="weatherReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
          </Tab>
          <Tab value="Actions" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))">
            <div class="grid-container-actions">
              <div v-if="accesstokenRef.value != null" class="grid-item">
                <button class="boxButton" v-on:click.native="identify">Identify</button>
              </div>
              <div v-if="accesstokenRef.value != null" class="grid-item">
                Contact WHO-15 to determine the name, location and license for the callsign.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="editCallsign">Edit</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Edit properties of the callsign.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton" v-on:click.native="remove">Remove</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Remove the station from Net Central.  It may be added if heard again via APRS.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null) && (weatherReports != null) && (weatherReports.value != null) && (weatherReports.value.length != 0))" class="grid-item">
                <button class="boxButton" v-on:click.native="clearReports">Clear reports</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null) && (weatherReports != null) && (weatherReports.value != null) && (weatherReports.value.length != 0))" class="grid-item">
                Clear all weather reports from Net Central.
              </div>
            </div>
          </Tab>
        </Tabs>
      </div>

      <div v-else>
        <Tabs>
          <Tab value="Details">
            <br>Name: {{ localSelectedObject.ncSelectedObject.name }}
            <br>Description: {{ localSelectedObject.ncSelectedObject.description }}
            <br>Status: {{ localSelectedObject.ncSelectedObject.status }}
            <br>Location: {{ localSelectedObject.ncSelectedObject.lat }} / {{ localSelectedObject.ncSelectedObject.lon }}
            <br>Electrical Power: {{ localSelectedObject.ncSelectedObject.electricalPowerType }} / {{ localSelectedObject.ncSelectedObject.backupElectricalPowerType }}
            <br>Radio style: {{ localSelectedObject.ncSelectedObject.radioStyle }}
            <br>Transmit power: {{ localSelectedObject.ncSelectedObject.transmitPower }}W
            <br>Tracked? {{ localSelectedObject.ncSelectedObject.trackingActive }}
            <br>Last heard: {{ localSelectedObject.ncSelectedObject.prettyLastHeard }}
          </Tab>
          <Tab value="APRS Status">
            <div v-if="((statusReports != null) && (statusReports.value != null))">
              <b>APRS Status information:</b>
              <EasyDataTable :headers="headersStatus" :items="statusReports.value" 
              :rows-per-page="10" buttons-pagination
              />
            </div>
            <div v-else>
              <br>
              <br> No APRS Status found.
            </div>
          </Tab>
          <Tab value="Actions" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))">
            <div class="grid-container-actions">
              <div v-if="((accesstokenRef.value != null) && (!localSelectedObject.ncSelectedObject.trackingActive))" class="grid-item">
                <button class="boxButton" v-on:click.native="track">Track</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (!localSelectedObject.ncSelectedObject.trackingActive))" class="grid-item">
                Add station to the special set of tracked stations that can be added to certain maps.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject.trackingActive))" class="grid-item">
                <button class="boxButton"  v-on:click.native="untrack">Stop Tracking</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject.trackingActive))" class="grid-item">
                Remove station from the special set of tracked stations that can be added to certain maps.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton"  v-on:click.native="editStation">Edit</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Edit properties of the station.
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                <button class="boxButton"  v-on:click.native="remove">Remove</button>
              </div>
              <div v-if="((accesstokenRef.value != null) && (localSelectedObject.ncSelectedObject != null))" class="grid-item">
                Remove the station from Net Central.  It may be added if heard again via APRS.
              </div>
            </div>
          </Tab>
        </Tabs>
      </div>
    </div>
    <div v-else>
    </div>
</template>

<style scoped>
.box {
  font-size: 1rem;
  font-weight: 400;

  border: 2px solid black;
  width: 100px;
  height: 30px;
  text-align: center;
  vertical-align: middle;
}
.grid-container-actions {
  display: grid;
  grid-template-columns: 30%  70%;
  margin: 20px;
  gap: 20px;
}
.grid-container-access-control {
  display: grid;
  grid-template-columns: 70%  30%;
  margin: 20px;
  gap: 20px;
}
.grid-container-weather {
  display: grid;
  grid-template-columns: 34% 33% 33%
}
.weather-box-header {
  font-size: 2rem; 
  font-weight: 400;
}
.weather-temperature {
  grid-row: span 3; 
  font-size: 3rem; 
  font-weight: 400;
}
</style>
